package dao;

import model.Broker_Data;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os dados de corretores.
 * No MongoDB, os dados do corretor (nrcreci) são embarcados diretamente
 * no documento do usuário na coleção "users".
 */
public class BrokerDataDAO {

    private static final String COLLECTION_NAME = "users";

    /**
     * Obtém a coleção MongoDB de usuários.
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Insere dados de corretor no documento do usuário via $set.
     * Adiciona o campo nrcreci ao documento do usuário correspondente.
     *
     * @param b Objeto contendo os dados do corretor.
     */
    public void insert(Broker_Data b) {
        try {
            UpdateResult result = getCollection().updateOne(
                Filters.eq("_id", b.getCduser()),
                Updates.set("nrcreci", b.getNrcreci())
            );
            if (result.getMatchedCount() > 0) {
                System.out.println("Dados de corretor inseridos com sucesso!");
            } else {
                System.err.println("Erro ao inserir corretor: usuário não encontrado (ID: " + b.getCduser() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inserir corretor: " + e.getMessage());
        }
    }

    /**
     * Busca os dados de um corretor pelo ID do usuário.
     * Extrai o campo nrcreci do documento do usuário.
     *
     * @param userId Identificador do usuário.
     * @return Objeto Broker_Data ou null se o usuário não for corretor.
     */
    public Broker_Data findByUserId(int userId) {
        try {
            Document doc = getCollection().find(
                Filters.and(
                    Filters.eq("_id", userId),
                    Filters.exists("nrcreci")
                )
            ).first();
            if (doc != null && doc.getString("nrcreci") != null) {
                Broker_Data b = new Broker_Data();
                b.setCduser(doc.getInteger("_id"));
                b.setNrcreci(doc.getString("nrcreci"));
                return b;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar dados de corretor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca os dados de um corretor pelo ID do usuário.
     * Equivalente a findByUserId pois nrcreci está embarcado no documento do usuário.
     *
     * @param cduser Identificador do usuário.
     * @return Objeto Broker_Data ou null.
     */
    public Broker_Data findById(int cduser) {
        return findByUserId(cduser);
    }

    /**
     * Lista todos os usuários que possuem dados de corretor (campo nrcreci definido).
     *
     * @return Lista de dados de corretores.
     */
    public List<Broker_Data> listAll() {
        List<Broker_Data> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find(
                Filters.exists("nrcreci")).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Broker_Data b = new Broker_Data();
                b.setCduser(doc.getInteger("_id"));
                b.setNrcreci(doc.getString("nrcreci"));
                list.add(b);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar dados de corretores: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza os dados de um corretor via $set no documento do usuário.
     *
     * @param b Objeto contendo os dados atualizados.
     */
    public void update(Broker_Data b) {
        try {
            getCollection().updateOne(
                Filters.eq("_id", b.getCduser()),
                Updates.set("nrcreci", b.getNrcreci())
            );
        } catch (Exception e) {
            System.err.println("Erro ao atualizar dados de corretor: " + e.getMessage());
        }
    }

    /**
     * Exclui os dados de corretor de um usuário via $unset (remove o campo nrcreci).
     *
     * @param userId Identificador do usuário.
     * @return true se o campo foi removido com sucesso.
     */
    public boolean delete(int userId) {
        try {
            UpdateResult result = getCollection().updateOne(
                Filters.eq("_id", userId),
                Updates.unset("nrcreci")
            );
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir dados de corretor: " + e.getMessage());
            return false;
        }
    }
}
