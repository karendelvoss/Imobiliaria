package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Fornece conexão com o banco de dados MongoDB.
 * Implementa padrão Singleton com sincronização.
 */
public class Conexao {

    private static final String DATABASE = "imobiliaria";
    private static final String CONNECTION_STRING =
            "mongodb+srv://delvossribas:AemBsxTH1hEuFrHT@imobiliaria.f7x4ou9.mongodb.net/?appName=imobiliaria";

    private static MongoClient client;

    private Conexao() {}

    /**
     * Obtém a instância única do MongoClient (Singleton sincronizado).
     *
     * @return MongoClient conectado ao servidor configurado.
     */
    public static synchronized MongoClient getClient() {
        if (client == null) {
            client = MongoClients.create(CONNECTION_STRING);
        }
        return client;
    }

    /**
     * Obtém o banco de dados configurado.
     *
     * @return MongoDatabase da aplicação.
     */
    public static MongoDatabase getDatabase() {
        return getClient().getDatabase(DATABASE);
    }

    /**
     * Obtém uma coleção pelo nome.
     *
     * @param name Nome da coleção.
     * @return MongoCollection de Documents.
     */
    public static MongoCollection<Document> getCollection(String name) {
        return getDatabase().getCollection(name);
    }

    /**
     * Fecha a conexão com o MongoDB para shutdown gracioso.
     * Após chamada, uma nova conexão será criada na próxima utilização.
     */
    public static synchronized void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
