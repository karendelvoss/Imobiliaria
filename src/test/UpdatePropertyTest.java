package test;

import dao.Conexao;
import dao.IndexDAO;
import dao.SequenceGenerator;
import model.Indexes;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade para atualização reflete mudanças.
 *
 * Propriedade 4: Para qualquer documento existente e qualquer conjunto válido
 * de campos atualizados, buscar o documento por ID após a atualização deve
 * retornar os valores novos nos campos alterados.
 *
 * **Validates: Requisito 3.3**
 *
 * Requer uma instância MongoDB em execução em localhost:27017.
 */
public class UpdatePropertyTest {

    private static final int NUM_ITERATIONS = 120;
    private static final Random random = new Random();
    private static final String COLLECTION_NAME = "indexes";

    // Track inserted IDs for cleanup
    private static final List<Integer> insertedIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: Atualização Reflete Mudanças ===");
        System.out.println("Propriedade 4: Atualização reflete mudanças");
        System.out.println("Validates: Requisito 3.3");
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

        // Step 2: Use IndexDAO as test subject
        IndexDAO indexDAO = new IndexDAO();
        int passed = 0;
        int failed = 0;

        try {
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                // Step 3: Insert an index with initial random name
                Indexes original = new Indexes();
                original.setNmindex(generateRandomIndexName());

                indexDAO.insert(original);
                int insertedId = original.getCdindex();
                insertedIds.add(insertedId);

                // Step 4: Generate random new name for nmindex
                String newName = generateRandomIndexName();
                // Ensure the new name is different from the original
                while (newName.equals(original.getNmindex())) {
                    newName = generateRandomIndexName();
                }

                // Step 5: Update via indexDAO.update()
                Indexes toUpdate = new Indexes();
                toUpdate.setCdindex(insertedId);
                toUpdate.setNmindex(newName);
                indexDAO.update(toUpdate);

                // Step 6: Retrieve via indexDAO.findById()
                Indexes retrieved = indexDAO.findById(insertedId);

                // Step 7: Verify: retrieved.getNmindex() equals the new name
                if (retrieved == null) {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1)
                            + ": findById retornou null para ID " + insertedId);
                    continue;
                }

                if (newName.equals(retrieved.getNmindex())) {
                    passed++;
                } else {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ":");
                    System.out.println("  ID:          " + insertedId);
                    System.out.println("  Nome orig:   \"" + original.getNmindex() + "\"");
                    System.out.println("  Nome novo:   \"" + newName + "\"");
                    System.out.println("  Recuperado:  \"" + retrieved.getNmindex() + "\"");
                }
            }
        } finally {
            // Step 9: Clean up test data
            cleanup();
        }

        // Step 10: Print results
        System.out.println();
        System.out.println("=== Resultado ===");
        System.out.println("Total:    " + NUM_ITERATIONS);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Atualização reflete mudanças corretamente.");
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
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(3, TimeUnit.SECONDS))
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(3, TimeUnit.SECONDS))
                    .build();

            try (MongoClient testClient = MongoClients.create(settings)) {
                testClient.getDatabase("imobiliaria").runCommand(new Document("ping", 1));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gera um nome aleatório para um índice financeiro.
     * Varia entre nomes comuns, strings com caracteres especiais e strings longas.
     */
    private static String generateRandomIndexName() {
        int choice = random.nextInt(10);
        switch (choice) {
            case 0: return "IPCA";
            case 1: return "IGP-M";
            case 2: return "INPC";
            case 3: return "SELIC";
            case 4: return "CDI";
            case 5: return "Índice " + random.nextInt(10000);
            case 6: return generateRandomAlphanumeric(random.nextInt(40) + 1);
            case 7: return "Taxa com acentos: ção, ñ, ü #" + random.nextInt(1000);
            case 8: return "Special!@#$%^&*()_+-=" + random.nextInt(1000);
            case 9: return generateRandomAlphanumeric(random.nextInt(100) + 50);
            default: return "Index-" + random.nextInt(99999);
        }
    }

    /**
     * Gera uma string alfanumérica aleatória com o comprimento dado.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 -_áéíóúãõç";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Limpa todos os documentos de índice inseridos durante o teste.
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
