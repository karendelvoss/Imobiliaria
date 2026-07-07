package test;

import dao.Conexao;
import dao.IndexDAO;
import dao.SequenceGenerator;
import model.Indexes;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade para exclusão de documentos.
 *
 * Propriedade 5: Para qualquer documento existente, após a operação de exclusão
 * por ID, buscar pelo mesmo ID deve retornar null.
 *
 * **Validates: Requisito 3.4**
 */
public class DeletePropertyTest {

    private static final int NUM_ITERATIONS = 150;
    private static final Random random = new Random();

    // Track inserted IDs that were NOT deleted (in case of failure during test)
    private static final List<Integer> pendingCleanup = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: Exclusão torna documento irrecuperável ===");
        System.out.println("Propriedade 5: Exclusão torna documento irrecuperável");
        System.out.println("Validates: Requisito 3.4");
        System.out.println("Iterações: " + NUM_ITERATIONS);
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

        // Step 2: Use IndexDAO as test subject (simple: insert, delete, verify null)
        IndexDAO indexDAO = new IndexDAO();
        int passed = 0;
        int failed = 0;

        try {
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                // Step 3: Insert an index, get the ID
                Indexes original = generateRandomIndex();
                indexDAO.insert(original);
                int id = original.getCdindex();
                pendingCleanup.add(id);

                // Verify it was actually inserted (sanity check)
                Indexes beforeDelete = indexDAO.findById(id);
                if (beforeDelete == null) {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ": Documento não encontrado após inserção (ID: " + id + ")");
                    continue;
                }

                // Step 4: Delete via indexDAO.delete(id)
                boolean deleteResult = indexDAO.delete(id);

                if (!deleteResult) {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ": delete() retornou false para ID " + id);
                    continue;
                }

                // Step 5: Retrieve via indexDAO.findById(id)
                Indexes afterDelete = indexDAO.findById(id);

                // Step 6: Verify: result is null
                if (afterDelete == null) {
                    passed++;
                    // Remove from cleanup since delete already handled it
                    pendingCleanup.remove(Integer.valueOf(id));
                } else {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ": findById retornou não-null após delete!");
                    System.out.println("  ID: " + id);
                    System.out.println("  Resultado inesperado: cdindex=" + afterDelete.getCdindex()
                            + ", nmindex=\"" + afterDelete.getNmindex() + "\"");
                }
            }
        } finally {
            // Step 8: Clean up any remaining documents (in case of failures)
            cleanup();
        }

        // Step 9: Print results
        System.out.println();
        System.out.println("=== Resultado ===");
        System.out.println("Total:    " + NUM_ITERATIONS);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Exclusão torna documento irrecuperável.");
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
     * Gera um objeto Indexes com dados aleatórios para inserção.
     */
    private static Indexes generateRandomIndex() {
        Indexes idx = new Indexes();
        idx.setNmindex(generateRandomIndexName());
        return idx;
    }

    /**
     * Gera nome de índice aleatório com variação para cobrir edge cases.
     */
    private static String generateRandomIndexName() {
        int choice = random.nextInt(8);
        switch (choice) {
            case 0: return "";
            case 1: return "IPCA";
            case 2: return "IGP-M";
            case 3: return "INPC";
            case 4: return "Índice com acentos: ção, ñ, ü";
            case 5: return "Special!@#$%^&*()_+-=[]{}|;':\",./<>?";
            case 6: return generateRandomAlphanumeric(random.nextInt(100) + 1);
            case 7: return generateRandomAlphanumeric(200); // string longa
            default: return "TestIndex";
        }
    }

    /**
     * Gera uma string alfanumérica aleatória.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 áéíóúãõç-_";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Limpa quaisquer documentos remanescentes do teste.
     */
    private static void cleanup() {
        try {
            MongoCollection<Document> collection = Conexao.getCollection("indexes");
            int deleted = 0;
            for (int id : pendingCleanup) {
                if (collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }
            if (deleted > 0) {
                System.out.println();
                System.out.println("[Cleanup] " + deleted + " documento(s) remanescente(s) removido(s).");
            }
        } catch (Exception e) {
            System.err.println("[Cleanup] Erro ao limpar dados de teste: " + e.getMessage());
        } finally {
            Conexao.close();
        }
    }
}
