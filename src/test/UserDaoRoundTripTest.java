package test;

import dao.Conexao;
import dao.UserDAO;
import dao.AddressDAO;
import model.Users;
import model.Addresses;
import model.Broker_Data;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade para persistência round-trip (UserDAO).
 *
 * Propriedade 2: Para qualquer objeto Users válido, inserir no MongoDB e depois
 * buscar pelo ID retornado deve produzir um objeto equivalente ao original
 * (preservando campos core: nmuser, document, fgdocument, nrcellphone, dtbirth, dsissuingbody).
 *
 * **Validates: Requisitos 3.1, 3.5**
 */
public class UserDaoRoundTripTest {

    private static final int NUM_ITERATIONS = 120;
    private static final Random random = new Random();
    private static final String TEST_COLLECTION = "users";

    // Track inserted IDs for cleanup
    private static final List<Integer> insertedIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: Persistência Round-Trip (UserDAO) ===");
        System.out.println("Propriedade 2: Inserção e recuperação por ID");
        System.out.println("Validates: Requisitos 3.1, 3.5");
        System.out.println("Iterações: " + NUM_ITERATIONS);
        System.out.println();

        // Step 1: Check MongoDB connection with 3 second timeout
        if (!checkMongoConnection()) {
            System.out.println("ERRO: MongoDB não está disponível na porta 27017.");
            System.out.println("Inicie o MongoDB antes de executar este teste:");
            System.out.println("  mongod --dbpath /data/db");
            System.out.println("  ou: docker run -d -p 27017:27017 mongo:7");
            System.exit(2);
        }

        System.out.println("✓ Conexão com MongoDB estabelecida.");
        System.out.println();

        UserDAO userDAO = new UserDAO();
        int passed = 0;
        int failed = 0;

        try {
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                // Step 2 & 3: Generate random Users with random Addresses
                Users originalUser = generateRandomUser();
                Addresses originalAddress = generateRandomAddress();
                String occupationName = generateRandomOccupation();
                Broker_Data broker = random.nextBoolean() ? generateRandomBroker() : null;

                // Step 4: Insert user directly using toDocument + insertOne
                // (avoids saveUser's internal lookups for address/occupation by ID)
                int newId = dao.SequenceGenerator.getNextSequence(TEST_COLLECTION);
                originalUser.setCduser(newId);
                insertedIds.add(newId);

                Document doc = UserDAO.toDocument(originalUser, originalAddress, occupationName, broker);
                Conexao.getCollection(TEST_COLLECTION).insertOne(doc);

                // Step 5: Retrieve via UserDAO.findById()
                Users retrieved = userDAO.findById(newId);

                // Step 6: Verify core fields match
                if (retrieved == null) {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ": findById retornou null para ID " + newId);
                    continue;
                }

                String mismatch = checkCoreFieldsMatch(originalUser, retrieved);
                if (mismatch == null) {
                    passed++;
                } else {
                    failed++;
                    System.out.println("FALHA na iteração " + (i + 1) + ": " + mismatch);
                    System.out.println("  Original:   nmuser=\"" + originalUser.getNmuser()
                            + "\", document=\"" + originalUser.getDocument()
                            + "\", fgdocument=" + originalUser.isFgdocument()
                            + ", nrcellphone=\"" + originalUser.getNrcellphone()
                            + "\", dtbirth=" + originalUser.getDtbirth()
                            + ", dsissuingbody=\"" + originalUser.getDsissuingbody() + "\"");
                    System.out.println("  Recuperado: nmuser=\"" + retrieved.getNmuser()
                            + "\", document=\"" + retrieved.getDocument()
                            + "\", fgdocument=" + retrieved.isFgdocument()
                            + ", nrcellphone=\"" + retrieved.getNrcellphone()
                            + "\", dtbirth=" + retrieved.getDtbirth()
                            + ", dsissuingbody=\"" + retrieved.getDsissuingbody() + "\"");
                }
            }
        } finally {
            // Step 8: Clean up test data
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
            System.out.println("✓ PROPRIEDADE SATISFEITA: Inserção e recuperação por ID preserva campos core.");
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
            // Create client with timeout settings
            com.mongodb.MongoClientSettings settings = com.mongodb.MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString("mongodb://localhost:27017"))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(3, TimeUnit.SECONDS))
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(3, TimeUnit.SECONDS))
                    .build();
            com.mongodb.client.MongoClient testClient = com.mongodb.client.MongoClients.create(settings);
            // Force connection by listing database names
            testClient.listDatabaseNames().first();
            testClient.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gera um objeto Users com dados aleatórios.
     * Cada iteração gera uma combinação única de documento para evitar conflitos de unique index.
     */
    private static Users generateRandomUser() {
        Users user = new Users();
        user.setNmuser(generateRandomName());
        // Generate unique document to avoid duplicate key conflicts
        user.setDocument(generateUniqueDocument());
        user.setFgdocument(random.nextBoolean());
        user.setNrcellphone(generateRandomPhone());
        user.setDtbirth(generateRandomDate());
        user.setDsissuingbody(generateRandomIssuingBody());
        user.setCdaddress(0);
        user.setCdoccupation(0);
        return user;
    }

    /**
     * Gera um objeto Addresses com dados aleatórios.
     */
    private static Addresses generateRandomAddress() {
        Addresses addr = new Addresses();
        addr.setCdzipcode(generateRandomZipcode());
        addr.setNmstreet(generateRandomStreet());
        addr.setNraddress(String.valueOf(random.nextInt(9999) + 1));
        addr.setDscomplement(random.nextBoolean() ? generateRandomComplement() : null);
        addr.setDistrict(generateRandomDistrict());
        addr.setCity(generateRandomCity());
        addr.setState(generateRandomState());
        addr.setCountry(generateRandomCountry());
        return addr;
    }

    /**
     * Gera dados aleatórios de corretor.
     */
    private static Broker_Data generateRandomBroker() {
        Broker_Data broker = new Broker_Data();
        broker.setNrcreci("CRECI-" + (random.nextInt(99999) + 10000));
        return broker;
    }

    /**
     * Verifica se os campos core do usuário original e do recuperado são equivalentes.
     * Retorna null se match, ou descrição do mismatch.
     */
    private static String checkCoreFieldsMatch(Users original, Users retrieved) {
        // nmuser
        if (!safeEquals(original.getNmuser(), retrieved.getNmuser())) {
            return "nmuser diverge: \"" + original.getNmuser() + "\" vs \"" + retrieved.getNmuser() + "\"";
        }
        // document
        if (!safeEquals(original.getDocument(), retrieved.getDocument())) {
            return "document diverge: \"" + original.getDocument() + "\" vs \"" + retrieved.getDocument() + "\"";
        }
        // fgdocument
        if (original.isFgdocument() != retrieved.isFgdocument()) {
            return "fgdocument diverge: " + original.isFgdocument() + " vs " + retrieved.isFgdocument();
        }
        // nrcellphone
        if (!safeEquals(original.getNrcellphone(), retrieved.getNrcellphone())) {
            return "nrcellphone diverge: \"" + original.getNrcellphone() + "\" vs \"" + retrieved.getNrcellphone() + "\"";
        }
        // dtbirth
        if (original.getDtbirth() == null && retrieved.getDtbirth() != null) {
            return "dtbirth diverge: null vs " + retrieved.getDtbirth();
        }
        if (original.getDtbirth() != null && !original.getDtbirth().equals(retrieved.getDtbirth())) {
            return "dtbirth diverge: " + original.getDtbirth() + " vs " + retrieved.getDtbirth();
        }
        // dsissuingbody
        if (!safeEquals(original.getDsissuingbody(), retrieved.getDsissuingbody())) {
            return "dsissuingbody diverge: \"" + original.getDsissuingbody() + "\" vs \"" + retrieved.getDsissuingbody() + "\"";
        }
        return null;
    }

    /**
     * Comparação null-safe de strings.
     */
    private static boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Limpa todos os documentos inseridos durante o teste.
     */
    private static void cleanup() {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(TEST_COLLECTION);
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

    // ===== Geradores de dados aleatórios =====

    private static int uniqueDocCounter = 0;

    private static String generateUniqueDocument() {
        uniqueDocCounter++;
        // Gera documento único combinando timestamp + contador + random
        long base = System.nanoTime();
        return String.format("TEST%d%05d%04d", base % 1000000000L, uniqueDocCounter, random.nextInt(10000));
    }

    private static String generateRandomName() {
        String[] firstNames = {"João", "Maria", "Carlos", "Ana", "Pedro", "Luiza", "Fernando",
                "Juliana", "Ricardo", "Patricia", "Roberto", "Camila", "Marcelo", "Isabela"};
        String[] lastNames = {"Silva", "Santos", "Oliveira", "Souza", "Rodrigues", "Ferreira",
                "Almeida", "Nascimento", "Lima", "Pereira", "Costa", "Gomes"};
        int choice = random.nextInt(10);
        switch (choice) {
            case 0: return ""; // edge case: empty name
            case 1: return generateRandomAlphanumeric(100); // long name
            case 2: return "Nome com acentuação: José Ñoño Müller";
            default:
                return firstNames[random.nextInt(firstNames.length)] + " "
                        + lastNames[random.nextInt(lastNames.length)];
        }
    }

    private static String generateRandomPhone() {
        int choice = random.nextInt(6);
        switch (choice) {
            case 0: return null;
            case 1: return "";
            case 2: return "47999" + String.format("%06d", random.nextInt(1000000));
            case 3: return "11988" + String.format("%06d", random.nextInt(1000000));
            case 4: return "+55479" + String.format("%08d", random.nextInt(100000000));
            default: return "4799" + String.format("%07d", random.nextInt(10000000));
        }
    }

    private static LocalDate generateRandomDate() {
        int choice = random.nextInt(8);
        switch (choice) {
            case 0: return null; // edge case: no birth date
            case 1: return LocalDate.of(1950, 1, 1); // old date
            case 2: return LocalDate.of(2000, 12, 31); // Y2K
            case 3: return LocalDate.of(2024, 2, 29); // leap year
            default:
                int year = 1940 + random.nextInt(80);
                int month = random.nextInt(12) + 1;
                int day = random.nextInt(28) + 1; // safe day range
                return LocalDate.of(year, month, day);
        }
    }

    private static String generateRandomIssuingBody() {
        String[] bodies = {"SSP", "SSP/SC", "SSP/SP", "SSP/RJ", "SSP/MG", "DETRAN",
                "IFP", "PC", "PM", null, ""};
        return bodies[random.nextInt(bodies.length)];
    }

    private static String generateRandomOccupation() {
        String[] occupations = {"Engenheiro", "Médico", "Analista de Sistemas", "Professor",
                "Advogado", "Contador", "Arquiteto", "Enfermeiro", null, ""};
        return occupations[random.nextInt(occupations.length)];
    }

    private static String generateRandomZipcode() {
        return String.format("%08d", random.nextInt(100000000));
    }

    private static String generateRandomStreet() {
        String[] streets = {"Rua XV de Novembro", "Av. Brasil", "Rua das Flores",
                "Rua São Paulo", "Av. Presidente Vargas", "Rua Principal"};
        return streets[random.nextInt(streets.length)];
    }

    private static String generateRandomComplement() {
        String[] complements = {"Apto 101", "Sala 2", "Casa B", "Bloco A", "Loja 3", "Fundos"};
        return complements[random.nextInt(complements.length)];
    }

    private static String generateRandomDistrict() {
        String[] districts = {"Centro", "Boa Vista", "Glória", "Itaum", "Bucarein",
                "Anita Garibaldi", "Saguaçu", "Floresta"};
        return districts[random.nextInt(districts.length)];
    }

    private static String generateRandomCity() {
        String[] cities = {"Joinville", "Florianópolis", "Blumenau", "São Paulo",
                "Curitiba", "Porto Alegre", "Rio de Janeiro"};
        return cities[random.nextInt(cities.length)];
    }

    private static String generateRandomState() {
        String[] states = {"SC", "SP", "PR", "RS", "RJ", "MG", "BA"};
        return states[random.nextInt(states.length)];
    }

    private static String generateRandomCountry() {
        String[] countries = {"Brasil", "Brazil"};
        return countries[random.nextInt(countries.length)];
    }

    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 áéíóúãõç";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
