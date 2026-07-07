package dao;

import model.Topics;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os tópicos de contratos.
 * Tópicos são documentos embarcados dentro da coleção "contract_templates".
 */
public class TopicDAO {

    private static final String COLLECTION_NAME = "contract_templates";

    /**
     * Obtém a coleção MongoDB de modelos de contrato.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Topics para Document BSON.
     */
    private Document toDocument(Topics t) {
        Document doc = new Document();
        doc.append("cdtopic", t.getCdtopic());
        doc.append("nmtopic", t.getNmtopic());
        doc.append("nrorder", t.getNrorder());
        doc.append("clauses", new ArrayList<Document>());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Topics.
     */
    private Topics fromDocument(Document doc) {
        Topics t = new Topics();
        t.setCdtopic(doc.getInteger("cdtopic"));
        t.setNmtopic(doc.getString("nmtopic"));
        t.setNrorder(doc.getInteger("nrorder"));
        return t;
    }

    /**
     * Insere um novo tópico embarcado em um template.
     * Gera ID sequencial via SequenceGenerator.
     * Nota: requer que o template já exista. Insere no primeiro template se nenhum for especificado.
     *
     * @param t Objeto contendo os dados do tópico.
     */
    public void insert(Topics t) {
        try {
            int id = SequenceGenerator.getNextSequence("topics");
            t.setCdtopic(id);
            // Insere no primeiro template encontrado (para manter compatibilidade com o CRUD genérico)
            Document template = getCollection().find().first();
            if (template != null) {
                getCollection().updateOne(
                    Filters.eq("_id", template.getInteger("_id")),
                    Updates.push("topics", toDocument(t))
                );
                System.out.println("Tópico inserido com sucesso! (ID: " + t.getCdtopic() + ")");
            } else {
                System.err.println("Nenhum template encontrado para inserir o tópico.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inserir tópico: " + e.getMessage());
        }
    }

    /**
     * Insere um novo tópico embarcado em um template específico.
     *
     * @param t Objeto contendo os dados do tópico.
     * @param templateId ID do template onde inserir o tópico.
     */
    public void insert(Topics t, int templateId) {
        try {
            int id = SequenceGenerator.getNextSequence("topics");
            t.setCdtopic(id);
            getCollection().updateOne(
                Filters.eq("_id", templateId),
                Updates.push("topics", toDocument(t))
            );
            System.out.println("Tópico inserido com sucesso! (ID: " + t.getCdtopic() + ")");
        } catch (Exception e) {
            System.err.println("Erro ao inserir tópico: " + e.getMessage());
        }
    }

    /**
     * Busca um tópico pelo seu identificador, pesquisando em todos os templates.
     *
     * @param id Identificador do tópico.
     * @return Objeto Topics ou null.
     */
    @SuppressWarnings("unchecked")
    public Topics findById(int id) {
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document templateDoc = cursor.next();
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        if (topicDoc.getInteger("cdtopic") == id) {
                            return fromDocument(topicDoc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tópico por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca os tópicos vinculados a um modelo de contrato (template).
     * Extrai o array de tópicos do documento do template.
     *
     * @param templateId Identificador do modelo de contrato.
     * @return Lista de tópicos vinculados, ordenados por nrorder.
     */
    @SuppressWarnings("unchecked")
    public List<Topics> findByTemplateId(int templateId) {
        List<Topics> list = new ArrayList<>();
        try {
            Document templateDoc = getCollection().find(Filters.eq("_id", templateId)).first();
            if (templateDoc != null) {
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        list.add(fromDocument(topicDoc));
                    }
                    // Ordena por nrorder
                    list.sort((a, b) -> Integer.compare(a.getNrorder(), b.getNrorder()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tópicos por template: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista todos os tópicos de todos os templates, ordenados pela ordem definida.
     *
     * @return Lista de objetos Topics.
     */
    @SuppressWarnings("unchecked")
    public List<Topics> listAll() {
        List<Topics> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document templateDoc = cursor.next();
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        list.add(fromDocument(topicDoc));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar tópicos: " + e.getMessage());
        }
        list.sort((a, b) -> {
            int cmp = Integer.compare(a.getNrorder(), b.getNrorder());
            return cmp != 0 ? cmp : Integer.compare(a.getCdtopic(), b.getCdtopic());
        });
        return list;
    }

    /**
     * Atualiza os dados de um tópico existente usando array filters positional operator.
     *
     * @param t Objeto contendo os dados atualizados do tópico.
     */
    @SuppressWarnings("unchecked")
    public void update(Topics t) {
        try {
            // Encontra o template que contém esse tópico
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext()) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (int i = 0; i < topics.size(); i++) {
                            if (topics.get(i).getInteger("cdtopic") == t.getCdtopic()) {
                                // Atualiza usando $set com dot notation posicional
                                getCollection().updateOne(
                                    Filters.and(
                                        Filters.eq("_id", templateDoc.getInteger("_id")),
                                        Filters.eq("topics.cdtopic", t.getCdtopic())
                                    ),
                                    Updates.combine(
                                        Updates.set("topics.$.nmtopic", t.getNmtopic()),
                                        Updates.set("topics.$.nrorder", t.getNrorder())
                                    )
                                );
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tópico: " + e.getMessage());
        }
    }

    /**
     * Exclui um tópico pelo seu identificador usando $pull no array de topics.
     *
     * @param id Identificador do tópico.
     * @return true se excluído com sucesso.
     */
    @SuppressWarnings("unchecked")
    public boolean delete(int id) {
        try {
            // Encontra o template que contém esse tópico e remove com $pull
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext()) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (Document topicDoc : topics) {
                            if (topicDoc.getInteger("cdtopic") == id) {
                                getCollection().updateOne(
                                    Filters.eq("_id", templateDoc.getInteger("_id")),
                                    Updates.pull("topics", new Document("cdtopic", id))
                                );
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir tópico: " + e.getMessage());
        }
        return false;
    }
}
