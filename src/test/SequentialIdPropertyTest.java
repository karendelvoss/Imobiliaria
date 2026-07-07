package test;

import dao.SequenceGenerator;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Teste de propriedade: IDs sequenciais são únicos e consecutivos.
 *
 * Propriedade 6: Para qualquer sequência de N inserções em uma mesma coleção,
 * os IDs gerados devem ser inteiros consecutivos (cada ID = anterior + 1)
 * e todos devem ser únicos.
 *
 * **Validates: Requirements 10.1, 10.3**
 *
 * Requer uma instância MongoDB em execução em localhost:27017.
 */
public class SequentialIdPropertyTest {

    private static final int NUM_ITERATIONS = 100;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade: IDs Sequenciais Únicos e Consecutivos ===");
        System.out.println("Propriedade 6: IDs sequenciais são únicos e consecutivos");
        System.out.println("Validates: Requisitos 10.1, 10.3");
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

        boolean allPassed = true;
        int totalRuns = 5; // Executar a propriedade com diferentes coleções de teste
        int passedRuns = 0;

        for (int run = 1; run <= totalRuns; run++) {
            // Gerar nome de coleção aleatório para evitar conflitos entre runs
            String testCollection = "test_seq_prop_" + System.currentTimeMillis() + "_" + RANDOM.nextInt(10000);
            int iterations = NUM_ITERATIONS;

            System.out.println("--- Run " + run + "/" + totalRuns + " ---");
            System.out.println("Coleção de teste: " + testCollection);
            System.out.println("Iterações: " + iterations);

            boolean runPassed = testSequentialProperty(testCollection, iterations);

            if (runPassed) {
                passedRuns++;
                System.out.println("Resultado: PASSOU ✓");
            } else {
                allPassed = false;
                System.out.println("Resultado: FALHOU ✗");
            }

            // Limpar o counter de teste após cada run
            cleanup(testCollection);
            System.out.println();
        }

        System.out.println("=== Resumo ===");
        System.out.println("Runs executados: " + totalRuns);
        System.out.println("Runs com sucesso: " + passedRuns);
        System.out.println("Runs com falha: " + (totalRuns - passedRuns));
        System.out.println();

        if (allPassed) {
            System.out.println("[PASSOU] Propriedade validada: IDs sequenciais são únicos e consecutivos.");
        } else {
            System.out.println("[FALHOU] Propriedade violada: IDs NÃO são únicos e/ou consecutivos.");
        }

        dao.Conexao.close();
        System.exit(allPassed ? 0 : 1);
    }

    /**
     * Testa a propriedade de que N chamadas consecutivas a getNextSequence
     * produzem IDs consecutivos (cada um = anterior + 1) e todos únicos.
     */
    private static boolean testSequentialProperty(String collectionName, int iterations) {
        Set<Integer> ids = new HashSet<>();
        int[] generatedIds = new int[iterations];

        // Gerar N IDs sequenciais
        for (int i = 0; i < iterations; i++) {
            generatedIds[i] = SequenceGenerator.getNextSequence(collectionName);
            ids.add(generatedIds[i]);
        }

        // Verificar propriedade 1: Todos os IDs são únicos
        boolean uniqueProperty = (ids.size() == iterations);
        if (!uniqueProperty) {
            System.out.println("  FALHA: IDs não são únicos. Esperado " + iterations +
                " IDs únicos, obteve " + ids.size());
            return false;
        }

        // Verificar propriedade 2: IDs são consecutivos (cada um = anterior + 1)
        boolean consecutiveProperty = true;
        int baseId = generatedIds[0];
        for (int i = 1; i < iterations; i++) {
            int expected = baseId + i;
            if (generatedIds[i] != expected) {
                System.out.println("  FALHA: ID não consecutivo na posição " + i +
                    ". Esperado " + expected + ", obteve " + generatedIds[i]);
                consecutiveProperty = false;
                break;
            }
        }

        if (!consecutiveProperty) {
            return false;
        }

        // Estatísticas
        System.out.println("  IDs gerados: " + iterations);
        System.out.println("  IDs únicos: " + ids.size());
        System.out.println("  Primeiro ID: " + generatedIds[0]);
        System.out.println("  Último ID: " + generatedIds[iterations - 1]);
        System.out.println("  Incremento verificado: cada ID = anterior + 1");

        return true;
    }

    /**
     * Verifica se MongoDB está acessível com timeout curto.
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
                // Forçar uma operação real para verificar conexão
                testClient.getDatabase("imobiliaria").runCommand(new Document("ping", 1));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove o documento counter de teste para não poluir o banco.
     */
    private static void cleanup(String collectionName) {
        try {
            dao.Conexao.getCollection("counters").deleteOne(
                Filters.eq("_id", collectionName)
            );
        } catch (Exception e) {
            System.out.println("  Aviso: Não foi possível limpar counter de teste: " + e.getMessage());
        }
    }
}
