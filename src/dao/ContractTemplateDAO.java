package dao;

import model.Contract_Templates;
import model.Topics;
import model.Clauses;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os modelos de contrato.
 * Usa a coleção "contract_templates" no MongoDB com tópicos e cláusulas embarcados.
 */
public class ContractTemplateDAO {

    private static final String COLLECTION_NAME = "contract_templates";

    /**
     * Obtém a coleção MongoDB de modelos de contrato.
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Clauses para Document BSON.
     *
     * @param clause Objeto do modelo Clauses.
     * @return Document BSON correspondente.
     */
    private Document clauseToDocument(Clauses clause) {
        Document doc = new Document();
        doc.append("cdclause", clause.getCdclause());
        doc.append("dstext", clause.getDstext());
        doc.append("nrorder", clause.getNrorder());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Clauses.
     *
     * @param doc Document BSON.
     * @return Objeto do modelo Clauses.
     */
    private Clauses clauseFromDocument(Document doc) {
        Clauses clause = new Clauses();
        clause.setCdclause(doc.getInteger("cdclause"));
        clause.setDstext(doc.getString("dstext"));
        clause.setNrorder(doc.getInteger("nrorder"));
        return clause;
    }

    /**
     * Converte um objeto Topics (com suas cláusulas) para Document BSON.
     *
     * @param topic Objeto do modelo Topics.
     * @param clauses Lista de cláusulas do tópico.
     * @return Document BSON correspondente.
     */
    private Document topicToDocument(Topics topic, List<Clauses> clauses) {
        Document doc = new Document();
        doc.append("cdtopic", topic.getCdtopic());
        doc.append("nmtopic", topic.getNmtopic());
        doc.append("nrorder", topic.getNrorder());

        List<Document> clauseDocs = new ArrayList<>();
        if (clauses != null) {
            for (Clauses clause : clauses) {
                clauseDocs.add(clauseToDocument(clause));
            }
        }
        doc.append("clauses", clauseDocs);
        return doc;
    }

    /**
     * Converte um Document BSON de tópico para objeto Topics.
     *
     * @param doc Document BSON do tópico.
     * @return Objeto do modelo Topics.
     */
    private Topics topicFromDocument(Document doc) {
        Topics topic = new Topics();
        topic.setCdtopic(doc.getInteger("cdtopic"));
        topic.setNmtopic(doc.getString("nmtopic"));
        topic.setNrorder(doc.getInteger("nrorder"));
        return topic;
    }

    /**
     * Converte um objeto Contract_Templates para Document BSON.
     * Inclui array de tópicos embarcados (sem cláusulas no nível do template,
     * pois o linkTopic adiciona tópicos individuais).
     *
     * @param obj Objeto do modelo.
     * @return Document BSON correspondente.
     */
    public Document toDocument(Contract_Templates obj) {
        Document doc = new Document();
        doc.append("_id", obj.getCdtemplate());
        doc.append("nmtemplate", obj.getNmtemplate());
        doc.append("dsversion", obj.getDsversion());
        doc.append("fgactive", obj.isFgactive());
        doc.append("topics", new ArrayList<Document>());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Contract_Templates.
     *
     * @param doc Document BSON.
     * @return Objeto do modelo Contract_Templates.
     */
    public Contract_Templates fromDocument(Document doc) {
        Contract_Templates ct = new Contract_Templates();
        ct.setCdtemplate(doc.getInteger("_id"));
        ct.setNmtemplate(doc.getString("nmtemplate"));
        ct.setDsversion(doc.getString("dsversion"));
        ct.setFgactive(doc.getBoolean("fgactive"));
        return ct;
    }

    /**
     * Extrai a lista de tópicos embarcados de um Document de template.
     *
     * @param doc Document BSON do template.
     * @return Lista de objetos Topics.
     */
    @SuppressWarnings("unchecked")
    public List<Topics> getTopicsFromDocument(Document doc) {
        List<Topics> topics = new ArrayList<>();
        List<Document> topicDocs = (List<Document>) doc.get("topics");
        if (topicDocs != null) {
            for (Document topicDoc : topicDocs) {
                topics.add(topicFromDocument(topicDoc));
            }
        }
        return topics;
    }

    /**
     * Extrai a lista de cláusulas de um Document de tópico embarcado.
     *
     * @param topicDoc Document BSON do tópico.
     * @return Lista de objetos Clauses.
     */
    @SuppressWarnings("unchecked")
    public List<Clauses> getClausesFromTopicDocument(Document topicDoc) {
        List<Clauses> clauses = new ArrayList<>();
        List<Document> clauseDocs = (List<Document>) topicDoc.get("clauses");
        if (clauseDocs != null) {
            for (Document clauseDoc : clauseDocs) {
                clauses.add(clauseFromDocument(clauseDoc));
            }
        }
        return clauses;
    }

    /**
     * Insere um novo modelo de contrato.
     * Gera um ID sequencial via SequenceGenerator e atribui ao objeto.
     *
     * @param obj Objeto contendo os dados do modelo.
     */
    public void insert(Contract_Templates obj) {
        try {
            int id = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            obj.setCdtemplate(id);
            getCollection().insertOne(toDocument(obj));
            System.out.println("Modelo de contrato inserido com sucesso! (ID: " + obj.getCdtemplate() + ")");
        } catch (Exception e) {
            System.err.println("Erro ao inserir modelo de contrato: " + e.getMessage());
        }
    }

    /**
     * Busca um modelo de contrato pelo ID.
     *
     * @param id Identificador do modelo.
     * @return Objeto Contract_Templates ou null se não encontrado.
     */
    public Contract_Templates findById(int id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", id)).first();
            if (doc != null) {
                return fromDocument(doc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar modelo de contrato por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os modelos de contrato cadastrados.
     *
     * @return Lista de modelos de contrato.
     */
    public List<Contract_Templates> listAll() {
        List<Contract_Templates> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                list.add(fromDocument(cursor.next()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar modelos de contrato: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza um modelo de contrato.
     *
     * @param obj Objeto contendo os dados atualizados.
     */
    public void update(Contract_Templates obj) {
        try {
            Document updateDoc = new Document("$set", new Document()
                .append("nmtemplate", obj.getNmtemplate())
                .append("dsversion", obj.getDsversion())
                .append("fgactive", obj.isFgactive()));
            getCollection().updateOne(Filters.eq("_id", obj.getCdtemplate()), updateDoc);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar modelo de contrato: " + e.getMessage());
        }
    }

    /**
     * Exclui um modelo de contrato.
     *
     * @param id Identificador do modelo.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        try {
            DeleteResult result = getCollection().deleteOne(Filters.eq("_id", id));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir modelo de contrato (verifique se está sendo usado): " + e.getMessage());
            return false;
        }
    }

    /**
     * Vincula um tópico a um modelo de contrato usando $push no array de topics.
     * Recebe o objeto Topics e suas cláusulas para embarcá-los no documento do template.
     *
     * @param templateId Identificador do modelo.
     * @param topic Objeto Topics a vincular.
     * @param clauses Lista de cláusulas do tópico.
     */
    public void linkTopic(int templateId, Topics topic, List<Clauses> clauses) {
        try {
            if (topic == null) {
                System.err.println("Tópico não pode ser nulo.");
                return;
            }

            Document topicDoc = topicToDocument(topic, clauses);

            getCollection().updateOne(
                Filters.eq("_id", templateId),
                Updates.push("topics", topicDoc)
            );
            System.out.println("Tópico vinculado ao modelo com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao vincular tópico (pode já estar vinculado): " + e.getMessage());
        }
    }

    /**
     * Vincula um tópico a um modelo de contrato pelo ID do tópico.
     * Busca o tópico nos dados embarcados de outros templates para reutilização.
     *
     * @param templateId Identificador do modelo.
     * @param topicId Identificador do tópico.
     */
    @SuppressWarnings("unchecked")
    public void linkTopic(int templateId, int topicId) {
        try {
            // Busca o tópico nos templates existentes (tópicos embarcados)
            Document topicDoc = null;
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext() && topicDoc == null) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (Document t : topics) {
                            if (t.getInteger("cdtopic") == topicId) {
                                topicDoc = t;
                                break;
                            }
                        }
                    }
                }
            }

            if (topicDoc == null) {
                System.err.println("Tópico não encontrado com ID: " + topicId);
                return;
            }

            getCollection().updateOne(
                Filters.eq("_id", templateId),
                Updates.push("topics", topicDoc)
            );
            System.out.println("Tópico vinculado ao modelo com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao vincular tópico (pode já estar vinculado): " + e.getMessage());
        }
    }

    /**
     * Busca o documento completo (raw) de um template pelo ID.
     * Útil para acessar os tópicos e cláusulas embarcados.
     *
     * @param id Identificador do modelo.
     * @return Document BSON completo ou null.
     */
    public Document findDocumentById(int id) {
        try {
            return getCollection().find(Filters.eq("_id", id)).first();
        } catch (Exception e) {
            System.err.println("Erro ao buscar documento do template: " + e.getMessage());
        }
        return null;
    }
}
