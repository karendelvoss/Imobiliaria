package test;

import dao.Conexao;
import dao.ContractDAO;
import dao.SequenceGenerator;
import model.Contracts;
import model.ContractStatus;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade para filtro por data de expiração.
 *
 * Propriedade 10: Filtro por data de expiração retorna contratos corretos.
 * Para qualquer conjunto de contratos com datas limite variadas e qualquer mês/ano alvo,
 * a consulta deve retornar somente contratos cuja dtlimit esteja no mês/ano especificado.
 *
 * **Validates: Requisito 4.5**
 */
public class ExpirationDateFilterPropertyTest {

    private static final int NUM_CONTRACTS = 30;
    private static final int NUM_FILTER_ITERATIONS = 20;
    private static final Random random = new Random();
    private static final String COLLECTION_NAME = "contracts";

    // Track inserted IDs for cleanup
    private static final List<Integer> insertedIds = new ArrayList<>();

    // Store inserted documents for cross-checking
    private static final List<Document> insertedDocs = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade 10: Filtro por data de expiração retorna contratos corretos ===");
        System.out.println("Validates: Requisito 4.5");
        System.out.println("Contratos inseridos: " + NUM_CONTRACTS);
        System.out.println("Iterações de filtro: " + NUM_FILTER_ITERATIONS);
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
            // Step 2: Insert N contracts with various dtlimit dates spread across different months/years
            insertTestContracts();
            System.out.println("✓ " + insertedDocs.size() + " contratos de teste inseridos.");
            System.out.println();

            ContractDAO contractDAO = new ContractDAO();

            // Step 3-7: Pick random month/year targets and verify filter results
            for (int i = 0; i < NUM_FILTER_ITERATIONS; i++) {
                // Pick a random month/year target
                int targetMonth = random.nextInt(12) + 1; // 1..12
                int targetYear = 2024 + random.nextInt(4); // 2024..2027

                // Step 4: Query using ContractDAO.findExpiringContracts(month, year)
                List<Contracts> results = contractDAO.findExpiringContracts(targetMonth, targetYear);

                // Calculate expected start/end of the target month
                LocalDate monthStart = LocalDate.of(targetYear, targetMonth, 1);
                LocalDate monthEnd = monthStart.plusMonths(1);

                // Step 5: Verify ALL returned contracts have dtlimit in the target month/year
                boolean allMatch = true;
                String offendingDate = null;
                int offendingId = -1;
                for (Contracts c : results) {
                    // Only check contracts we inserted (filter by our IDs)
                    if (!insertedIds.contains(c.getCdcontract())) {
                        continue;
                    }
                    LocalDate dtlimit = c.getDtlimit();
                    if (dtlimit == null || dtlimit.isBefore(monthStart) || !dtlimit.isBefore(monthEnd)) {
                        allMatch = false;
                        offendingDate = (dtlimit != null) ? dtlimit.toString() : "null";
                        offendingId = c.getCdcontract();
                        break;
                    }
                }

                // Step 6: Verify NO contract that should match is missed (cross-check with inserted data)
                int expectedCount = 0;
                for (Document doc : insertedDocs) {
                    String dtlimitStr = doc.getString("dtlimit");
                    if (dtlimitStr == null || dtlimitStr.isEmpty()) continue;
                    LocalDate dtlimit = LocalDate.parse(dtlimitStr);
                    int status = doc.getInteger("cdstatus", 0);
                    // findExpiringContracts excludes FINALIZADO contracts
                    if (status == ContractStatus.FINALIZADO.getCode()) continue;
                    if (!dtlimit.isBefore(monthStart) && dtlimit.isBefore(monthEnd)) {
                        expectedCount++;
                    }
                }

                // Count only our test contracts in the results
                int ourResultCount = 0;
                for (Contracts c : results) {
                    if (insertedIds.contains(c.getCdcontract())) {
                        ourResultCount++;
                    }
                }

                boolean countMatches = (ourResultCount == expectedCount);

                if (allMatch && countMatches) {
                    passed++;
                } else {
                    failed++;
                    if (failed <= 5) {
                        System.out.println("FALHA na iteração " + (i + 1) + " (mês=" + targetMonth + ", ano=" + targetYear + "):");
                        if (!allMatch) {
                            System.out.println("  Contrato #" + offendingId + " retornado com dtlimit=\"" + offendingDate + "\" fora do período.");
                        }
                        if (!countMatches) {
                            System.out.println("  Esperava " + expectedCount + " contrato(s), obteve " + ourResultCount);
                        }
                    }
                }
            }

        } finally {
            // Step 8: Clean up test data
            cleanup();
        }

        // Step 9: Print results
        System.out.println();
        System.out.println("=== Resultado ===");
        System.out.println("Total de iterações de filtro: " + NUM_FILTER_ITERATIONS);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Filtro por data de expiração retorna contratos corretos.");
            System.exit(0);
        } else {
            System.out.println("✗ PROPRIEDADE VIOLADA: " + failed + " caso(s) falharam.");
            System.exit(1);
        }
    }

    /**
     * Verifica a conexão com MongoDB com timeout de 3 segundos.
     */
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

    /**
     * Inserts test contracts with varied dtlimit dates into MongoDB.
     * Dates are spread across different months and years (2024-2027).
     * Some contracts are FINALIZADO to test exclusion.
     */
    private static void insertTestContracts() {
        MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);

        for (int i = 0; i < NUM_CONTRACTS; i++) {
            int newId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            insertedIds.add(newId);

            Contracts contract = new Contracts();
            contract.setCdcontract(newId);
            contract.setDtcreation(LocalDate.now());
            contract.setDstitle("Test Contract Expiring " + newId);
            contract.setCdtemplate(1);
            contract.setCdproperty(0);
            contract.setCdindex(0);

            // Generate random dtlimit spread across 2024-2027
            int year = 2024 + random.nextInt(4);
            int month = random.nextInt(12) + 1;
            int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
            int day = random.nextInt(maxDay) + 1;
            LocalDate dtlimit = LocalDate.of(year, month, day);
            contract.setDtlimit(dtlimit);

            // Most contracts are ATIVO, some are FINALIZADO to test exclusion
            int status;
            if (i < NUM_CONTRACTS - 5) {
                status = ContractStatus.ATIVO.getCode();
            } else {
                status = ContractStatus.FINALIZADO.getCode();
            }
            contract.setCdstatus(status);

            Document doc = ContractDAO.toDocument(contract, null, null);
            collection.insertOne(doc);
            insertedDocs.add(doc);
        }
    }

    /**
     * Cleans up all test documents inserted during the test.
     */
    private static void cleanup() {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            int deleted = 0;
            for (int id : insertedIds) {
                if (collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }
            System.out.println();
            System.out.println("[Cleanup] " + deleted + " documento(s) de teste removido(s).");
        } catch (Exception e) {
            System.err.println("[Cleanup] Erro ao limpar dados de teste: " + e.getMessage());
        } finally {
            Conexao.close();
        }
    }
}
