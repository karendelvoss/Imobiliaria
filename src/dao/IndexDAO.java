package dao;

import model.Indexes;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os índices financeiros.
 * Usa a coleção "indexes" no MongoDB com IDs sequenciais via SequenceGenerator.
 */
public class IndexDAO {

    private static final String COLLECTION_NAME = "indexes";

    /**
     * Obtém a coleção MongoDB de índices.
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Indexes para Document BSON.
     *
     * @param obj Objeto do modelo.
     * @return Document BSON correspondente.
     */
    Document toDocument(Indexes obj) {
        Document doc = new Document();
        doc.append("_id", obj.getCdindex());
        doc.append("nmindex", obj.getNmindex());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Indexes.
     *
     * @param doc Document BSON.
     * @return Objeto do modelo Indexes.
     */
    Indexes fromDocument(Document doc) {
        Indexes idx = new Indexes();
        idx.setCdindex(doc.getInteger("_id"));
        idx.setNmindex(doc.getString("nmindex"));
        return idx;
    }

    /**
     * Insere um novo índice financeiro.
     * Gera um ID sequencial via SequenceGenerator e atribui ao objeto.
     *
     * @param obj Objeto contendo os dados do índice.
     */
    public void insert(Indexes obj) {
        try {
            int id = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            obj.setCdindex(id);
            getCollection().insertOne(toDocument(obj));
            System.out.println("Índice inserido com sucesso! (ID: " + obj.getCdindex() + ")");
        } catch (Exception e) {
            System.err.println("Erro ao inserir índice: " + e.getMessage());
        }
    }

    /**
     * Busca um índice financeiro pelo ID.
     *
     * @param id Identificador do índice.
     * @return Objeto Indexes ou null se não encontrado.
     */
    public Indexes findById(int id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", id)).first();
            if (doc != null) {
                return fromDocument(doc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar índice por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os índices financeiros cadastrados.
     *
     * @return Lista de objetos Indexes.
     */
    public List<Indexes> listAll() {
        List<Indexes> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                list.add(fromDocument(cursor.next()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar índices: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza os dados de um índice financeiro.
     *
     * @param obj Objeto contendo os dados atualizados.
     */
    public void update(Indexes obj) {
        try {
            Document updateDoc = new Document("$set", new Document("nmindex", obj.getNmindex()));
            getCollection().updateOne(Filters.eq("_id", obj.getCdindex()), updateDoc);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar índice: " + e.getMessage());
        }
    }

    /**
     * Exclui um índice financeiro pelo seu identificador.
     *
     * @param id Identificador do índice.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        try {
            DeleteResult result = getCollection().deleteOne(Filters.eq("_id", id));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir índice: " + e.getMessage());
            return false;
        }
    }
}
