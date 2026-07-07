package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.bson.Document;

/**
 * Gerador de IDs sequenciais usando a coleção "counters" do MongoDB.
 * Garante atomicidade na geração de IDs via findOneAndUpdate com upsert.
 */
public class SequenceGenerator {

    private static final String COUNTERS_COLLECTION = "counters";

    /**
     * Obtém o próximo ID sequencial para a coleção especificada.
     * Usa findOneAndUpdate com upsert para garantir atomicidade —
     * mesmo com múltiplos acessos simultâneos, cada chamada retorna um valor único.
     *
     * @param collectionName Nome da coleção para a qual gerar o próximo ID.
     * @return Próximo valor inteiro sequencial.
     */
    public static int getNextSequence(String collectionName) {
        MongoCollection<Document> counters = Conexao.getCollection(COUNTERS_COLLECTION);
        Document result = counters.findOneAndUpdate(
            Filters.eq("_id", collectionName),
            Updates.inc("seq", 1),
            new FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.AFTER)
                .upsert(true)
        );
        return result.getInteger("seq");
    }
}
