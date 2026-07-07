package dao;

import model.ReadjustmentLog;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Gerencia as operações de persistência para os logs de reajuste financeiro.
 * Coleção: readjustment_logs (append-only).
 */
public class ReadjustmentLogDAO {

    private static final String COLLECTION_NAME = "readjustment_logs";

    /**
     * Obtém a coleção MongoDB de logs de reajuste.
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Insere um novo log de reajuste no banco de dados.
     * Gera um ID sequencial via SequenceGenerator.
     *
     * @param log Objeto contendo os detalhes do reajuste realizado.
     */
    public void insert(ReadjustmentLog log) {
        try {
            int id = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            log.setCdlog(id);

            Document doc = new Document("_id", id)
                    .append("cdcontract", log.getCdcontract())
                    .append("cdinstallment", log.getCdinstallment())
                    .append("cdindex", log.getCdindex())
                    .append("vlold", log.getVlold())
                    .append("vlnew", log.getVlnew())
                    .append("dtreadjustment", log.getDtreadjustment() != null
                            ? log.getDtreadjustment().toString() : null);

            getCollection().insertOne(doc);
        } catch (Exception e) {
            System.err.println("Erro ao inserir log de reajuste: " + e.getMessage());
        }
    }
}
