package test;

import dao.IndexDAO;
import dao.Conexao;
import model.Indexes;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.Document;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade: Listagem retorna todos os documentos inseridos.
 *
 * Propriedade 3: Para qualquer conjunto de N objetos válidos inseridos em uma coleção,
 * a operação listAll deve retornar exatamente N objetos.
 *
 * **Validates: Requisito 3.2**
 *
 * Requer uma instância MongoDB em execução em localhost:27017.
 */
public class ListAllPropertyTest {

    private static final int NUM_ITERATIONS = 10;
    private static final int MIN_OBJECTS = 5;
    private static final int MAX_OBJECTS = 50;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: Listagem Retorna Todos os Documentos Inseridos ===");
        System.out.println("Propriedade 3: listAll() retorna exatamente N objetos após N inserções");
        System.out.println("Validates: Requisito 3.2");
        System.out.println();

        // Verificar conexão com MongoDB usando timeout curto
        if (!checkMongoConnection()) {
            System.out.println("[ERRO] Não foi possível conectar ao MongoDB.");
            System.out.println("Este teste requer uma instância MongoDB em execução em localhost:27017.");
            System.out.println("Inicie o MongoDB e tente novamente.");
            System.out.println();
            System.out.println("Para iniciar MongoDB via Docker:");
            System.out.println("  docker run -d -p 27017:27017 --name mongo-test mongo:7");
            System.exit(1);
        }

        int passedIterations = 0;
        int failedIterations = 0;

        for (int i = 1; i <= NUM_ITERATIONS; i++) {
            int n = MIN_OBJECTS + RANDOM.nextInt(MAX_OBJECTS - MIN_OBJECTS + 1);

            System.out.println("--- Iteração " + i + "/" + NUM_ITERATIONS + " (N=" + n + ") ---");

            boolean result = testListAllProperty(n);

            if (result) {
                passedIterations++;
                System.out.println("  Resultado: PASSOU ✓");
            } else {
                failedIterations++;
                System.out.println("  Resultado: FALHOU ✗");
            }
            System.out.println();
        }

        System.out.println("=== Resumo ===");
        System.out.println("Iterações executadas: " + NUM_ITERATIONS);
        System.out.println("Iterações com sucesso: " + passedIterations);
        System.out.println("Iterações com falha: " + failedIterations);
        System.out.println();

        if (failedIterations == 0) {
            System.out.println("[PASSOU] Propriedade validada: listAll() retorna exatamente N objetos inseridos.");
        } else {
            System.out.println("[FALHOU] Propriedade violada: listAll() NÃO retornou o número correto de objetos.");
        }

        Conexao.close();
        System.exit(failedIterations == 0 ? 0 : 1);
    }

    /**
     * Testa a propriedade: após inserir N objetos em uma coleção limpa,
     * listAll() deve retornar exatamente N objetos.
     *
     * @param n Número de objetos a inserir.
     * @return true se a propriedade foi satisfeita.
     */
    private static boolean testListAllProperty(int n) {
        IndexDAO dao = new IndexDAO();

        // 1. Limpar a coleção indexes para garantir estado conhecido
        clearCollection();

        // 2. Inserir N objetos com nomes aleatórios
        for (int j = 0; j < n; j++) {
            Indexes idx = new Indexes();
            idx.setNmindex(generateRandomIndexName());
            dao.insert(idx);
        }

        // 3. Chamar listAll e verificar que retorna exatamente N
        List<Indexes> result = dao.listAll();
        int actual = result.size();

        if (actual != n) {
            System.out.println("  FALHA: Esperado " + n + " objetos, listAll() retornou " + actual);
            // Limpar após falha
            clearCollection();
            resetCounter();
            return false;
        }

        System.out.println("  Inseridos: " + n + " | listAll().size(): " + actual);

        // 4. Limpar para próxima iteração
        clearCollection();
        resetCounter();

        return true;
    }

    /**
     * Limpa a coleção indexes removendo todos os documentos.
     */
    private static void clearCollection() {
        try {
            Conexao.getCollection("indexes").deleteMany(new Document());
        } catch (Exception e) {
            System.err.println("  Aviso: Não foi possível limpar coleção: " + e.getMessage());
        }
    }

    /**
     * Reseta o counter de IDs sequenciais para a coleção indexes.
     */
    private static void resetCounter() {
        try {
            Conexao.getCollection("counters").deleteOne(
                new Document("_id", "indexes")
            );
        } catch (Exception e) {
            System.err.println("  Aviso: Não foi possível resetar counter: " + e.getMessage());
        }
    }

    /**
     * Gera um nome de índice aleatório para simular dados variados.
     */
    private static String generateRandomIndexName() {
        int choice = RANDOM.nextInt(7);
        switch (choice) {
            case 0: return "IPCA";
            case 1: return "IGP-M";
            case 2: return "INPC";
            case 3: return "SELIC";
            case 4: return "CDI";
            case 5: return generateRandomAlphanumeric(RANDOM.nextInt(30) + 3);
            case 6: return "Índice-" + RANDOM.nextInt(10000);
            default: return "TestIndex";
        }
    }

    /**
     * Gera uma string alfanumérica aleatória com o comprimento dado.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Verifica se MongoDB está acessível com timeout curto (3 segundos).
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
}
