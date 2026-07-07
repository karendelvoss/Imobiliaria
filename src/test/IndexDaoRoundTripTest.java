package dao;

import model.Indexes;
import org.bson.Document;

import java.util.Random;

/**
 * Teste de propriedade para round-trip de conversão Model ↔ BSON no IndexDAO.
 *
 * Propriedade: Para qualquer objeto Indexes válido, converter para Document BSON
 * via toDocument e depois converter de volta via fromDocument deve produzir um
 * objeto equivalente ao original (preservando todos os campos).
 *
 * **Validates: Requirements 3.6**
 */
public class IndexDaoRoundTripTest {

    private static final int NUM_ITERATIONS = 200;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: Round-trip Model ↔ BSON (IndexDAO) ===");
        System.out.println("Iterações: " + NUM_ITERATIONS);
        System.out.println();

        IndexDAO dao = new IndexDAO();
        int passed = 0;
        int failed = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            Indexes original = generateRandomIndexes();

            // Converter Model → BSON
            Document doc = dao.toDocument(original);

            // Converter BSON → Model
            Indexes restored = dao.fromDocument(doc);

            // Verificar equivalência
            if (isEquivalent(original, restored)) {
                passed++;
            } else {
                failed++;
                System.out.println("FALHA na iteração " + (i + 1) + ":");
                System.out.println("  Original:   cdindex=" + original.getCdindex()
                        + ", nmindex=\"" + original.getNmindex() + "\"");
                System.out.println("  Restaurado: cdindex=" + restored.getCdindex()
                        + ", nmindex=\"" + restored.getNmindex() + "\"");
            }
        }

        System.out.println();
        System.out.println("=== Resultado ===");
        System.out.println("Total:    " + NUM_ITERATIONS);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Round-trip Model ↔ BSON preserva todos os campos.");
            System.exit(0);
        } else {
            System.out.println("✗ PROPRIEDADE VIOLADA: " + failed + " caso(s) falharam.");
            System.exit(1);
        }
    }

    /**
     * Gera um objeto Indexes com dados aleatórios.
     * Varia IDs (positivos, zero, negativos, max int) e strings (vazias, longas, com caracteres especiais).
     */
    private static Indexes generateRandomIndexes() {
        Indexes idx = new Indexes();
        idx.setCdindex(generateRandomId());
        idx.setNmindex(generateRandomString());
        return idx;
    }

    /**
     * Gera um ID aleatório cobrindo vários cenários:
     * - IDs positivos comuns
     * - Zero
     * - IDs negativos (edge case)
     * - Valores grandes
     */
    private static int generateRandomId() {
        int choice = random.nextInt(10);
        switch (choice) {
            case 0: return 0;
            case 1: return Integer.MAX_VALUE;
            case 2: return Integer.MIN_VALUE;
            case 3: return -random.nextInt(1000);
            default: return random.nextInt(100000) + 1;
        }
    }

    /**
     * Gera uma string aleatória cobrindo vários cenários:
     * - Strings vazias
     * - Strings curtas (nomes comuns de índices)
     * - Strings longas
     * - Strings com caracteres especiais e Unicode
     */
    private static String generateRandomString() {
        int choice = random.nextInt(8);
        switch (choice) {
            case 0: return "";
            case 1: return "IPCA";
            case 2: return "IGP-M";
            case 3: return "INPC";
            case 4: return generateRandomAlphanumeric(random.nextInt(50) + 1);
            case 5: return "Índice com acentos: ção, ñ, ü";
            case 6: return "Special!@#$%^&*()_+-=[]{}|;':\",./<>?";
            case 7: return generateRandomAlphanumeric(200); // string longa
            default: return "Default";
        }
    }

    /**
     * Gera uma string alfanumérica aleatória com o comprimento dado.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 -_";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Verifica se dois objetos Indexes são equivalentes (mesmos valores de campos).
     */
    private static boolean isEquivalent(Indexes a, Indexes b) {
        if (a.getCdindex() != b.getCdindex()) {
            return false;
        }
        if (a.getNmindex() == null && b.getNmindex() == null) {
            return true;
        }
        if (a.getNmindex() == null || b.getNmindex() == null) {
            return false;
        }
        return a.getNmindex().equals(b.getNmindex());
    }
}
