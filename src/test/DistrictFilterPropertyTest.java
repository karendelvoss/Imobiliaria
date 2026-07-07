package test;

import dao.AddressDAO;
import dao.Conexao;
import dao.PropertyDAO;
import dao.SequenceGenerator;
import model.Addresses;
import model.Properties;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Teste de propriedade para filtro por bairro.
 *
 * Propriedade 9: Filtro por bairro retorna somente imóveis correspondentes.
 * Para qualquer conjunto de imóveis com bairros variados e qualquer string de filtro,
 * a consulta deve retornar somente imóveis cujo campo address.district contenha
 * a string de filtro (case-insensitive).
 *
 * **Validates: Requisito 4.1**
 */
public class DistrictFilterPropertyTest {

    private static final int NUM_PROPERTIES = 50;
    private static final int NUM_FILTER_ITERATIONS = 30;
    private static final Random random = new Random();
    private static final String COLLECTION_NAME = "properties";

    // Track inserted IDs for cleanup
    private static final List<Integer> insertedIds = new ArrayList<>();

    // District names used for generating test data
    private static final String[] DISTRICTS = {
        "Centro", "Vila Madalena", "Boa Vista", "Itaum", "Bucarein",
        "Anita Garibaldi", "Saguaçu", "Floresta", "Glória", "América",
        "Santo Antônio", "São João", "Jardim das Flores", "Parque Industrial",
        "Vila Nova", "Bom Retiro", "Costa e Silva", "Guanabara",
        "Pirabeiraba", "Aventureiro", "Fátima", "Iririú", "Jarivatuba",
        "Comasa", "Paranaguamirim", "Nova Brasília", "Petrópolis",
        "Atiradores", "Zona Industrial Norte", "Vila Cubatão"
    };

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade 9: Filtro por bairro retorna somente imóveis correspondentes ===");
        System.out.println("Validates: Requisito 4.1");
        System.out.println("Imóveis inseridos: " + NUM_PROPERTIES);
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
            // Step 2: Insert N properties with various district names
            List<Document> insertedDocs = insertTestProperties();
            System.out.println("✓ " + insertedDocs.size() + " imóveis de teste inseridos.");
            System.out.println();

            // Step 3-7: Pick random district substrings as filters and verify
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);

            for (int i = 0; i < NUM_FILTER_ITERATIONS; i++) {
                // Pick a random filter string (substring of a known district or random)
                String filter = generateFilterString();

                // Query using the same regex pattern as PropertyDAO.relatorioCompletoImoveis()
                Bson regexFilter = Filters.regex("address.district",
                        Pattern.compile(Pattern.quote(filter), Pattern.CASE_INSENSITIVE));

                List<Document> results = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        Filters.and(
                            Filters.in("_id", insertedIds),
                            regexFilter
                        )).iterator()) {
                    while (cursor.hasNext()) {
                        results.add(cursor.next());
                    }
                }

                // Verify property 1: ALL returned results contain the filter string (case-insensitive)
                boolean allMatch = true;
                String offendingDistrict = null;
                for (Document doc : results) {
                    Document addrDoc = doc.get("address", Document.class);
                    String district = (addrDoc != null) ? addrDoc.getString("district") : null;
                    if (district == null || !district.toLowerCase().contains(filter.toLowerCase())) {
                        allMatch = false;
                        offendingDistrict = district;
                        break;
                    }
                }

                // Verify property 2: NO property that should match is missed
                // Cross-check with our inserted data
                int expectedCount = 0;
                for (Document doc : insertedDocs) {
                    Document addrDoc = doc.get("address", Document.class);
                    String district = (addrDoc != null) ? addrDoc.getString("district") : null;
                    if (district != null && district.toLowerCase().contains(filter.toLowerCase())) {
                        expectedCount++;
                    }
                }

                boolean countMatches = (results.size() == expectedCount);

                if (allMatch && countMatches) {
                    passed++;
                } else {
                    failed++;
                    if (failed <= 5) {
                        System.out.println("FALHA na iteração " + (i + 1) + " (filtro=\"" + filter + "\"):");
                        if (!allMatch) {
                            System.out.println("  Resultado contém imóvel cujo district não corresponde: \"" + offendingDistrict + "\"");
                        }
                        if (!countMatches) {
                            System.out.println("  Esperava " + expectedCount + " resultado(s), obteve " + results.size());
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
            System.out.println("✓ PROPRIEDADE SATISFEITA: Filtro por bairro retorna somente imóveis correspondentes.");
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
     * Inserts test properties with various districts into MongoDB.
     * Returns the list of inserted documents for cross-checking.
     */
    private static List<Document> insertTestProperties() {
        List<Document> docs = new ArrayList<>();
        MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);

        for (int i = 0; i < NUM_PROPERTIES; i++) {
            int newId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            insertedIds.add(newId);

            Addresses addr = generateRandomAddress();
            Properties prop = new Properties();
            prop.setCdproperty(newId);
            prop.setNrregistration("TEST-DIST-" + newId + "-" + System.nanoTime());
            prop.setDsdescription("Test property " + i);
            prop.setVltotalarea(50.0 + random.nextDouble() * 200.0);

            Document doc = PropertyDAO.toDocument(prop, addr, "Casa", "Residencial", "Disponível", new ArrayList<>());
            collection.insertOne(doc);
            docs.add(doc);
        }

        return docs;
    }

    /**
     * Generates a filter string — either a substring of a known district
     * or a random string that may or may not match.
     */
    private static String generateFilterString() {
        int choice = random.nextInt(10);
        switch (choice) {
            case 0:
                // Full district name
                return DISTRICTS[random.nextInt(DISTRICTS.length)];
            case 1:
                // Random short substring that likely won't match
                return generateRandomAlphanumeric(random.nextInt(3) + 2);
            case 2:
                // Common prefix
                return "Vila";
            case 3:
                // Common prefix lowercase
                return "centro";
            case 4:
                // Common suffix
                return "eira";
            case 5:
                // Mixed case substring
                return "bOa";
            case 6:
                // Accented characters
                return "Antônio";
            case 7:
                // Single character
                return "a";
            default:
                // Substring from a random district
                String district = DISTRICTS[random.nextInt(DISTRICTS.length)];
                if (district.length() <= 2) return district;
                int start = random.nextInt(district.length() - 2);
                int end = start + 2 + random.nextInt(Math.min(5, district.length() - start - 2));
                return district.substring(start, Math.min(end, district.length()));
        }
    }

    /**
     * Generates a random Addresses object with a district from our known set.
     */
    private static Addresses generateRandomAddress() {
        Addresses addr = new Addresses();
        addr.setCdzipcode(String.format("%08d", random.nextInt(100000000)));
        addr.setNmstreet("Rua Teste " + random.nextInt(1000));
        addr.setNraddress(String.valueOf(random.nextInt(9999) + 1));
        addr.setDscomplement(random.nextBoolean() ? "Sala " + random.nextInt(100) : null);
        addr.setDistrict(DISTRICTS[random.nextInt(DISTRICTS.length)]);
        addr.setCity("Joinville");
        addr.setState("SC");
        addr.setCountry("Brasil");
        return addr;
    }

    /**
     * Generates a random alphanumeric string of the given length.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
