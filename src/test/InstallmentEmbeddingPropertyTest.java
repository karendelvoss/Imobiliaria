package dao;

import model.Contracts;
import model.Installments;
import model.User_Contract;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Teste de propriedade para parcelas embarcadas no contrato.
 *
 * Propriedade 8: Parcelas embarcadas no contrato.
 * Para qualquer contrato com N parcelas associadas, o documento BSON do contrato
 * deve conter um array "installments" com exatamente N elementos, cada um
 * preservando todos os campos da parcela original.
 *
 * **Validates: Requisitos 2.5, 4.2**
 */
public class InstallmentEmbeddingPropertyTest {

    private static final int NUM_ITERATIONS = 150;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade 8: Parcelas embarcadas no contrato ===");
        System.out.println("Iterações: " + NUM_ITERATIONS);
        System.out.println();

        int totalPassed = 0;
        int totalFailed = 0;

        // --- Parte 1: installmentToDocument → installmentFromDocument round-trip ---
        System.out.println("--- Parte 1: Round-trip installmentToDocument/installmentFromDocument ---");
        int part1Passed = 0;
        int part1Failed = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            int numInstallments = 1 + random.nextInt(24); // 1-24 parcelas
            int cdcontract = random.nextInt(10000) + 1;
            List<Installments> originals = generateRandomInstallments(numInstallments, cdcontract);

            // Converter cada parcela para Document e de volta
            List<Document> docs = new ArrayList<>();
            for (Installments inst : originals) {
                docs.add(ContractDAO.installmentToDocument(inst));
            }

            // Verificar tamanho do array
            if (docs.size() != numInstallments) {
                part1Failed++;
                if (part1Failed <= 5) {
                    System.out.println("FALHA (tamanho) na iteração " + (i + 1)
                            + ": esperado " + numInstallments + " documentos, obtido " + docs.size());
                }
                continue;
            }

            // Converter de volta e verificar preservação de campos
            boolean iterationPassed = true;
            for (int j = 0; j < numInstallments; j++) {
                Installments original = originals.get(j);
                Installments restored = ContractDAO.installmentFromDocument(docs.get(j), cdcontract);

                String diff = findInstallmentDifference(original, restored);
                if (diff != null) {
                    part1Failed++;
                    iterationPassed = false;
                    if (part1Failed <= 5) {
                        System.out.println("FALHA (round-trip) na iteração " + (i + 1)
                                + ", parcela " + (j + 1) + ": " + diff);
                        printInstallment("  Original", original);
                        printInstallment("  Restaurada", restored);
                    }
                    break;
                }
            }

            if (iterationPassed) {
                part1Passed++;
            }
        }

        System.out.println("Parte 1 - Passaram: " + part1Passed + " | Falharam: " + part1Failed);
        System.out.println();

        // --- Parte 2: Parcelas embarcadas via ContractDAO.toDocument ---
        System.out.println("--- Parte 2: Parcelas embarcadas no documento completo do contrato ---");
        int part2Passed = 0;
        int part2Failed = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            int numInstallments = 1 + random.nextInt(24); // 1-24 parcelas
            Contracts contract = generateRandomContract();
            List<Installments> installments = generateRandomInstallments(numInstallments, contract.getCdcontract());
            List<User_Contract> participants = generateRandomParticipants(contract.getCdcontract());

            // Criar documento completo do contrato
            Document contractDoc = ContractDAO.toDocument(contract, participants, installments);

            // Verificar que o array "installments" existe
            List<Document> embeddedInstallments = contractDoc.getList("installments", Document.class);
            if (embeddedInstallments == null) {
                part2Failed++;
                if (part2Failed <= 5) {
                    System.out.println("FALHA na iteração " + (i + 1)
                            + ": array 'installments' é null no documento do contrato");
                }
                continue;
            }

            // Verificar que o tamanho corresponde a N
            if (embeddedInstallments.size() != numInstallments) {
                part2Failed++;
                if (part2Failed <= 5) {
                    System.out.println("FALHA (tamanho) na iteração " + (i + 1)
                            + ": esperado " + numInstallments + " parcelas, obtido " + embeddedInstallments.size());
                }
                continue;
            }

            // Verificar preservação de campos em cada parcela embarcada
            boolean iterationPassed = true;
            for (int j = 0; j < numInstallments; j++) {
                Installments original = installments.get(j);
                Document instDoc = embeddedInstallments.get(j);

                // Verificar presença de todos os campos
                String missingField = checkInstallmentFieldsPresent(instDoc);
                if (missingField != null) {
                    part2Failed++;
                    iterationPassed = false;
                    if (part2Failed <= 5) {
                        System.out.println("FALHA (campos ausentes) na iteração " + (i + 1)
                                + ", parcela " + (j + 1) + ": " + missingField);
                    }
                    break;
                }

                // Converter de volta e verificar equivalência
                Installments restored = ContractDAO.installmentFromDocument(instDoc, contract.getCdcontract());
                String diff = findInstallmentDifference(original, restored);
                if (diff != null) {
                    part2Failed++;
                    iterationPassed = false;
                    if (part2Failed <= 5) {
                        System.out.println("FALHA (embedding round-trip) na iteração " + (i + 1)
                                + ", parcela " + (j + 1) + ": " + diff);
                        printInstallment("  Original", original);
                        printInstallment("  Restaurada", restored);
                    }
                    break;
                }
            }

            if (iterationPassed) {
                part2Passed++;
            }
        }

        System.out.println("Parte 2 - Passaram: " + part2Passed + " | Falharam: " + part2Failed);
        System.out.println();

        // --- Resultado Final ---
        totalPassed = part1Passed + part2Passed;
        totalFailed = part1Failed + part2Failed;
        int total = NUM_ITERATIONS * 2;

        System.out.println("=== Resultado Final ===");
        System.out.println("Total:    " + total);
        System.out.println("Passaram: " + totalPassed);
        System.out.println("Falharam: " + totalFailed);
        System.out.println();

        if (totalFailed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Parcelas embarcadas preservam todos os campos e tamanho correto.");
            System.exit(0);
        } else {
            System.out.println("✗ PROPRIEDADE VIOLADA: " + totalFailed + " caso(s) falharam.");
            System.exit(1);
        }
    }

    // ========== Verificação de campos ==========

    /**
     * Verifica se TODOS os campos esperados de uma parcela estão presentes no Document.
     * Retorna null se OK, ou descrição do campo ausente.
     */
    private static String checkInstallmentFieldsPresent(Document doc) {
        if (!doc.containsKey("cdinstallment"))
            return "campo 'cdinstallment' ausente no documento";
        if (!doc.containsKey("nrinstallment"))
            return "campo 'nrinstallment' ausente no documento";
        if (!doc.containsKey("dtdue"))
            return "campo 'dtdue' ausente no documento";
        if (!doc.containsKey("vlbase"))
            return "campo 'vlbase' ausente no documento";
        if (!doc.containsKey("vladjusted"))
            return "campo 'vladjusted' ausente no documento";
        if (!doc.containsKey("cdstatus"))
            return "campo 'cdstatus' ausente no documento";
        if (!doc.containsKey("dtpayment"))
            return "campo 'dtpayment' ausente no documento";
        if (!doc.containsKey("vlpenalty"))
            return "campo 'vlpenalty' ausente no documento";
        if (!doc.containsKey("vlinterest"))
            return "campo 'vlinterest' ausente no documento";
        return null;
    }

    /**
     * Compara dois objetos Installments campo a campo.
     * Retorna null se equivalentes, ou descrição da diferença encontrada.
     */
    private static String findInstallmentDifference(Installments a, Installments b) {
        if (a.getCdinstallment() != b.getCdinstallment())
            return "cdinstallment diverge: " + a.getCdinstallment() + " vs " + b.getCdinstallment();
        if (a.getNrinstallment() != b.getNrinstallment())
            return "nrinstallment diverge: " + a.getNrinstallment() + " vs " + b.getNrinstallment();
        if (!dateEquals(a.getDtdue(), b.getDtdue()))
            return "dtdue diverge: " + a.getDtdue() + " vs " + b.getDtdue();
        if (Double.compare(a.getVlbase(), b.getVlbase()) != 0)
            return "vlbase diverge: " + a.getVlbase() + " vs " + b.getVlbase();
        if (Double.compare(a.getVladjusted(), b.getVladjusted()) != 0)
            return "vladjusted diverge: " + a.getVladjusted() + " vs " + b.getVladjusted();
        if (a.getCdstatus() != b.getCdstatus())
            return "cdstatus diverge: " + a.getCdstatus() + " vs " + b.getCdstatus();
        if (!dateEquals(a.getDtpayment(), b.getDtpayment()))
            return "dtpayment diverge: " + a.getDtpayment() + " vs " + b.getDtpayment();
        if (Double.compare(a.getVlpenalty(), b.getVlpenalty()) != 0)
            return "vlpenalty diverge: " + a.getVlpenalty() + " vs " + b.getVlpenalty();
        if (Double.compare(a.getVlinterest(), b.getVlinterest()) != 0)
            return "vlinterest diverge: " + a.getVlinterest() + " vs " + b.getVlinterest();
        return null;
    }

    /**
     * Compara duas datas LocalDate, tratando null como iguais.
     */
    private static boolean dateEquals(LocalDate a, LocalDate b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Imprime os campos de uma parcela para debug.
     */
    private static void printInstallment(String prefix, Installments inst) {
        System.out.println(prefix + ": cdinstallment=" + inst.getCdinstallment()
                + ", nrinstallment=" + inst.getNrinstallment()
                + ", dtdue=" + inst.getDtdue()
                + ", vlbase=" + inst.getVlbase()
                + ", vladjusted=" + inst.getVladjusted()
                + ", cdstatus=" + inst.getCdstatus()
                + ", dtpayment=" + inst.getDtpayment()
                + ", vlpenalty=" + inst.getVlpenalty()
                + ", vlinterest=" + inst.getVlinterest());
    }

    // ========== Geradores de dados aleatórios ==========

    /**
     * Gera uma lista de N parcelas aleatórias para um contrato.
     */
    private static List<Installments> generateRandomInstallments(int count, int cdcontract) {
        List<Installments> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Installments inst = new Installments();
            inst.setCdinstallment(random.nextInt(10000) + 1);
            inst.setNrinstallment(i + 1);
            inst.setDtdue(generateRandomDate());
            inst.setVlbase(generateRandomMonetaryValue());
            inst.setVladjusted(generateRandomMonetaryValue());
            inst.setCdstatus(1 + random.nextInt(3)); // 1=pendente, 2=pago, 3=atrasado
            inst.setDtpayment(random.nextBoolean() ? generateRandomDate() : null);
            inst.setVlpenalty(random.nextBoolean() ? generateRandomMonetaryValue() : 0.0);
            inst.setVlinterest(random.nextBoolean() ? generateRandomMonetaryValue() : 0.0);
            inst.setFk_Contracts_cdcontract(cdcontract);
            list.add(inst);
        }
        return list;
    }

    /**
     * Gera um contrato aleatório.
     */
    private static Contracts generateRandomContract() {
        Contracts c = new Contracts();
        c.setCdcontract(random.nextInt(10000) + 1);
        c.setDtcreation(generateRandomDate());
        c.setDstitle(generateRandomTitle());
        c.setCdtemplate(1 + random.nextInt(5));
        c.setCdproperty(1 + random.nextInt(100));
        c.setCdindex(1 + random.nextInt(5));
        c.setDtlimit(generateRandomDate());
        c.setCdstatus(1 + random.nextInt(3));
        c.setCdnotary(random.nextBoolean() ? (1 + random.nextInt(10)) : 0);
        return c;
    }

    /**
     * Gera uma lista de participantes aleatórios para um contrato.
     */
    private static List<User_Contract> generateRandomParticipants(int cdcontract) {
        int numParticipants = 1 + random.nextInt(4);
        List<User_Contract> list = new ArrayList<>();
        for (int i = 0; i < numParticipants; i++) {
            User_Contract uc = new User_Contract();
            uc.setCdcontract(cdcontract);
            uc.setCduser(random.nextInt(100) + 1);
            uc.setCdrole(1 + random.nextInt(4));
            list.add(uc);
        }
        return list;
    }

    /**
     * Gera uma data aleatória entre 2020 e 2030.
     */
    private static LocalDate generateRandomDate() {
        int year = 2020 + random.nextInt(11);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return LocalDate.of(year, month, day);
    }

    /**
     * Gera um valor monetário aleatório com 2 casas decimais.
     */
    private static double generateRandomMonetaryValue() {
        return Math.round((random.nextDouble() * 10000.0) * 100.0) / 100.0;
    }

    /**
     * Gera um título aleatório para contrato.
     */
    private static String generateRandomTitle() {
        String[] titles = {
            "Contrato de Locação",
            "Contrato de Venda",
            "Locação Residencial",
            "Venda Comercial",
            "Aluguel Temporário",
            "Cessão de Direitos",
            "Contrato Misto"
        };
        return titles[random.nextInt(titles.length)] + " #" + (random.nextInt(1000) + 1);
    }
}
