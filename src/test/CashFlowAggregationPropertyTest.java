package test;

import dao.Conexao;
import dao.ContractDAO;
import dao.InstallmentDAO;
import dao.SequenceGenerator;
import dto.CashFlowReportDTO;
import model.Contracts;
import model.ContractStatus;
import model.Installments;
import model.InstallmentStatus;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade para agregação de fluxo de caixa.
 *
 * Propriedade 11: Agregação de fluxo de caixa preserva totais.
 * Para qualquer conjunto de contratos com parcelas conhecidas, a soma dos valores
 * "recebido" + "pendente" + "emAtraso" por mês no relatório de fluxo de caixa
 * deve ser igual à soma total dos valores das parcelas daquele mês.
 *
 * **Validates: Requisito 4.6**
 */
public class CashFlowAggregationPropertyTest {

    private static final int NUM_CONFIGURATIONS = 6;
    private static final Random random = new Random();
    private static final String CONTRACTS_COLLECTION = "contracts";

    // Track inserted contract IDs for cleanup
    private static final List<Integer> insertedContractIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade 11: Agregação de fluxo de caixa preserva totais ===");
        System.out.println("Validates: Requisito 4.6");
        System.out.println("Configurações de dados: " + NUM_CONFIGURATIONS);
        System.out.println();

        // Step 1: Check MongoDB connection with 3s timeout
        if (!checkMongoConnection()) {
            System.out.println("ERRO: MongoDB não está disponível na porta 27017.");
            System.out.println("Inicie o MongoDB antes de executar este teste:");
            System.out.println("  mongod --dbpath /data/db");
            System.out.println("  ou: docker run -d -p 27017:27017 mongo:7");
            System.exit(2);
        }

        System.out.println("✓ Conexão com MongoDB estabelecida.");
        System.out.println();

        int passed = 0;
        int failed = 0;

        try {
            for (int config = 0; config < NUM_CONFIGURATIONS; config++) {
                // Clean up previous iteration data
                cleanup();
                insertedContractIds.clear();

                // Step 2: Insert N contracts with known installments
                int numContracts = 2 + random.nextInt(5); // 2 to 6 contracts
                int testYear = 2023 + random.nextInt(3);  // 2023, 2024, or 2025
                List<InstallmentTestData> allInstallments = new ArrayList<>();

                System.out.println("--- Configuração " + (config + 1) + ": " + numContracts 
                    + " contratos, ano=" + testYear + " ---");

                for (int c = 0; c < numContracts; c++) {
                    int contractId = insertTestContract(testYear, allInstallments);
                    if (contractId > 0) {
                        insertedContractIds.add(contractId);
                    }
                }

                System.out.println("  Contratos inseridos: " + insertedContractIds.size());
                System.out.println("  Parcelas totais: " + allInstallments.size());

                // Step 3: Call InstallmentDAO.getMonthlyCashFlowReport(year, 0) for all contracts
                InstallmentDAO installmentDAO = new InstallmentDAO();
                List<CashFlowReportDTO> report = installmentDAO.getMonthlyCashFlowReport(testYear, 0);

                // Step 4: For each month in the report: verify recebido + pendente >= 0
                boolean nonNegativeOk = true;
                for (CashFlowReportDTO dto : report) {
                    if (dto.getValorRecebido() < 0 || dto.getValorPendente() < 0 || dto.getValorEmAtraso() < 0) {
                        nonNegativeOk = false;
                        System.out.println("  FALHA: Valor negativo encontrado no mês " + dto.getMes());
                        break;
                    }
                }

                // Step 5: Cross-check — manually compute sums by month and compare with report
                // Compute expected sums per month from our known test data
                Map<Integer, Double> expectedRecebido = new HashMap<>();
                Map<Integer, Double> expectedPendente = new HashMap<>();
                Map<Integer, Double> expectedEmAtraso = new HashMap<>();

                LocalDate today = LocalDate.now();

                for (InstallmentTestData inst : allInstallments) {
                    if (inst.dtdue.getYear() != testYear) continue;
                    int month = inst.dtdue.getMonthValue();
                    double value = inst.effectiveValue();

                    if (inst.cdstatus == InstallmentStatus.PAGO.getCode()) {
                        expectedRecebido.merge(month, value, Double::sum);
                    } else if (inst.cdstatus == InstallmentStatus.PENDENTE.getCode()) {
                        expectedPendente.merge(month, value, Double::sum);
                        // emAtraso: pendente AND dtdue < today
                        if (inst.dtdue.isBefore(today)) {
                            expectedEmAtraso.merge(month, value, Double::sum);
                        }
                    }
                }

                // Compare report totals with expected
                boolean crossCheckOk = true;
                for (CashFlowReportDTO dto : report) {
                    int month = dto.getMes();
                    double expRec = expectedRecebido.getOrDefault(month, 0.0);
                    double expPen = expectedPendente.getOrDefault(month, 0.0);
                    double expAtr = expectedEmAtraso.getOrDefault(month, 0.0);

                    if (!roughlyEquals(dto.getValorRecebido(), expRec)) {
                        crossCheckOk = false;
                        System.out.println("  FALHA mês " + month + ": recebido esperado=" 
                            + String.format("%.2f", expRec) + " obtido=" 
                            + String.format("%.2f", dto.getValorRecebido()));
                        break;
                    }
                    if (!roughlyEquals(dto.getValorPendente(), expPen)) {
                        crossCheckOk = false;
                        System.out.println("  FALHA mês " + month + ": pendente esperado=" 
                            + String.format("%.2f", expPen) + " obtido=" 
                            + String.format("%.2f", dto.getValorPendente()));
                        break;
                    }
                    if (!roughlyEquals(dto.getValorEmAtraso(), expAtr)) {
                        crossCheckOk = false;
                        System.out.println("  FALHA mês " + month + ": emAtraso esperado=" 
                            + String.format("%.2f", expAtr) + " obtido=" 
                            + String.format("%.2f", dto.getValorEmAtraso()));
                        break;
                    }
                }

                // Also verify that all months with expected data appear in the report
                boolean completenessOk = true;
                for (int month : getAllMonthsWithData(expectedRecebido, expectedPendente)) {
                    boolean found = false;
                    for (CashFlowReportDTO dto : report) {
                        if (dto.getMes() == month) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        double expRec = expectedRecebido.getOrDefault(month, 0.0);
                        double expPen = expectedPendente.getOrDefault(month, 0.0);
                        // Only fail if there were actual values expected
                        if (expRec > 0.001 || expPen > 0.001) {
                            completenessOk = false;
                            System.out.println("  FALHA: Mês " + month + " com dados esperados não aparece no relatório.");
                            break;
                        }
                    }
                }

                if (nonNegativeOk && crossCheckOk && completenessOk) {
                    passed++;
                    System.out.println("  ✓ Configuração " + (config + 1) + " passou.");
                } else {
                    failed++;
                    System.out.println("  ✗ Configuração " + (config + 1) + " falhou.");
                }
                System.out.println();
            }

        } finally {
            // Step 7: Clean up test data
            cleanup();
        }

        // Step 8: Print results
        System.out.println("=== Resultado ===");
        System.out.println("Total de configurações: " + NUM_CONFIGURATIONS);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Agregação de fluxo de caixa preserva totais.");
            System.exit(0);
        } else {
            System.out.println("✗ PROPRIEDADE VIOLADA: " + failed + " configuração(ões) falharam.");
            System.exit(1);
        }
    }

    // ========== Helper: Insert test contract with installments ==========

    /**
     * Creates and inserts a contract with random installments for the given year.
     * Tracks installment data in allInstallments for cross-checking.
     *
     * @return The contract ID, or -1 on failure.
     */
    private static int insertTestContract(int testYear, List<InstallmentTestData> allInstallments) {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(CONTRACTS_COLLECTION);

            int contractId = SequenceGenerator.getNextSequence(CONTRACTS_COLLECTION);

            Contracts contract = new Contracts();
            contract.setCdcontract(contractId);
            contract.setDtcreation(LocalDate.of(testYear, 1, 1));
            contract.setDstitle("TestCashFlow-" + contractId);
            contract.setCdtemplate(1);
            contract.setCdproperty(0);
            contract.setCdindex(0);
            contract.setDtlimit(LocalDate.of(testYear, 12, 31));
            contract.setCdstatus(ContractStatus.ATIVO.getCode());

            // Generate 1-12 installments spread across months of the test year
            int numInstallments = 1 + random.nextInt(12);
            List<Installments> installments = new ArrayList<>();

            for (int i = 0; i < numInstallments; i++) {
                Installments inst = new Installments();
                int instId = SequenceGenerator.getNextSequence("installments");
                inst.setCdinstallment(instId);
                inst.setNrinstallment(i + 1);

                // Random month in test year
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28); // safe day range
                LocalDate dtdue = LocalDate.of(testYear, month, day);
                inst.setDtdue(dtdue);

                // Random base value between 500 and 5000, rounded to 2 decimals
                double vlbase = Math.round((500 + random.nextDouble() * 4500) * 100.0) / 100.0;
                inst.setVlbase(vlbase);

                // Sometimes set adjusted value (50% chance)
                double vladjusted = 0.0;
                if (random.nextBoolean()) {
                    vladjusted = Math.round((vlbase * (1 + random.nextDouble() * 0.1)) * 100.0) / 100.0;
                }
                inst.setVladjusted(vladjusted);

                // Random status: PAGO (2) or PENDENTE (1)
                int cdstatus;
                if (random.nextBoolean()) {
                    cdstatus = InstallmentStatus.PAGO.getCode();
                    inst.setDtpayment(dtdue.plusDays(random.nextInt(5)));
                } else {
                    cdstatus = InstallmentStatus.PENDENTE.getCode();
                    inst.setDtpayment(null);
                }
                inst.setCdstatus(cdstatus);

                inst.setVlpenalty(0.0);
                inst.setVlinterest(0.0);
                inst.setFk_Contracts_cdcontract(contractId);

                installments.add(inst);

                // Track for cross-checking
                allInstallments.add(new InstallmentTestData(dtdue, vlbase, vladjusted, cdstatus));
            }

            // Build and insert the contract document with embedded installments
            Document doc = ContractDAO.toDocument(contract, null, installments);
            collection.insertOne(doc);

            return contractId;
        } catch (Exception e) {
            System.err.println("Erro ao inserir contrato de teste: " + e.getMessage());
            return -1;
        }
    }

    // ========== Helper: Connection check ==========

    private static boolean checkMongoConnection() {
        try {
            com.mongodb.MongoClientSettings settings = com.mongodb.MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString("mongodb://localhost:27017"))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(3, TimeUnit.SECONDS))
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(3, TimeUnit.SECONDS))
                    .build();
            com.mongodb.client.MongoClient testClient = com.mongodb.client.MongoClients.create(settings);
            testClient.listDatabaseNames().first();
            testClient.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ========== Helper: Comparison with tolerance ==========

    /**
     * Compares two doubles with a small tolerance for floating point precision.
     */
    private static boolean roughlyEquals(double a, double b) {
        return Math.abs(a - b) < 0.01;
    }

    // ========== Helper: Get all months that have data ==========

    private static List<Integer> getAllMonthsWithData(Map<Integer, Double> recebido, Map<Integer, Double> pendente) {
        List<Integer> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            if (recebido.containsKey(m) || pendente.containsKey(m)) {
                months.add(m);
            }
        }
        return months;
    }

    // ========== Helper: Cleanup ==========

    private static void cleanup() {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(CONTRACTS_COLLECTION);
            int deleted = 0;
            for (int id : insertedContractIds) {
                if (collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }
            if (deleted > 0) {
                System.out.println("[Cleanup] " + deleted + " contrato(s) de teste removido(s).");
            }
        } catch (Exception e) {
            System.err.println("[Cleanup] Erro ao limpar dados de teste: " + e.getMessage());
        }
    }

    // ========== Inner class: Test data tracking ==========

    /**
     * Tracks the relevant data of an installment for cross-checking with the report.
     */
    private static class InstallmentTestData {
        final LocalDate dtdue;
        final double vlbase;
        final double vladjusted;
        final int cdstatus;

        InstallmentTestData(LocalDate dtdue, double vlbase, double vladjusted, int cdstatus) {
            this.dtdue = dtdue;
            this.vlbase = vlbase;
            this.vladjusted = vladjusted;
            this.cdstatus = cdstatus;
        }

        /**
         * Returns the effective value used by the aggregation pipeline:
         * if vladjusted > 0, use vladjusted; otherwise use vlbase.
         */
        double effectiveValue() {
            return vladjusted > 0 ? vladjusted : vlbase;
        }
    }
}
