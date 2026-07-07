package dao;

import model.Clauses;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as cláusulas contratuais.
 * Cláusulas são documentos embarcados dentro de tópicos, que por sua vez
 * estão embarcados na coleção "contract_templates".
 */
public class ClauseDAO {

    private static final String COLLECTION_NAME = "contract_templates";

    /**
     * Obtém a coleção MongoDB de modelos de contrato.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Clauses para Document BSON.
     */
    private Document toDocument(Clauses c) {
        Document doc = new Document();
        doc.append("cdclause", c.getCdclause());
        doc.append("dstext", c.getDstext());
        doc.append("nrorder", c.getNrorder());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Clauses.
     * Seta o cdtopic a partir do tópico pai quando disponível.
     */
    private Clauses fromDocument(Document doc, int cdtopic) {
        Clauses c = new Clauses();
        c.setCdclause(doc.getInteger("cdclause"));
        c.setDstext(doc.getString("dstext"));
        c.setCdtopic(cdtopic);
        c.setNrorder(doc.getInteger("nrorder"));
        return c;
    }

    /**
     * Insere uma nova cláusula embarcada em um tópico.
     * Busca o tópico pelo cdtopic da cláusula em todos os templates.
     * Gera ID sequencial via SequenceGenerator.
     *
     * @param c Objeto contendo os dados da cláusula (cdtopic deve estar preenchido).
     */
    @SuppressWarnings("unchecked")
    public void insert(Clauses c) {
        try {
            int id = SequenceGenerator.getNextSequence("clauses");
            c.setCdclause(id);

            // Encontra o template que contém o tópico alvo
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext()) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (int i = 0; i < topics.size(); i++) {
                            if (topics.get(i).getInteger("cdtopic") == c.getCdtopic()) {
                                // Usa $push com dot notation para adicionar cláusula ao tópico
                                String arrayPath = "topics." + i + ".clauses";
                                getCollection().updateOne(
                                    Filters.eq("_id", templateDoc.getInteger("_id")),
                                    Updates.push(arrayPath, toDocument(c))
                                );
                                System.out.println("Cláusula inserida com sucesso! (ID: " + c.getCdclause() + ")");
                                return;
                            }
                        }
                    }
                }
            }
            System.err.println("Tópico não encontrado com ID: " + c.getCdtopic());
        } catch (Exception e) {
            System.err.println("Erro ao inserir cláusula: " + e.getMessage());
        }
    }

    /**
     * Insere uma nova cláusula em um tópico de um template específico.
     *
     * @param c Objeto contendo os dados da cláusula.
     * @param templateId ID do template.
     */
    @SuppressWarnings("unchecked")
    public void insert(Clauses c, int templateId) {
        try {
            int id = SequenceGenerator.getNextSequence("clauses");
            c.setCdclause(id);

            Document templateDoc = getCollection().find(Filters.eq("_id", templateId)).first();
            if (templateDoc != null) {
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (int i = 0; i < topics.size(); i++) {
                        if (topics.get(i).getInteger("cdtopic") == c.getCdtopic()) {
                            String arrayPath = "topics." + i + ".clauses";
                            getCollection().updateOne(
                                Filters.eq("_id", templateId),
                                Updates.push(arrayPath, toDocument(c))
                            );
                            System.out.println("Cláusula inserida com sucesso! (ID: " + c.getCdclause() + ")");
                            return;
                        }
                    }
                }
            }
            System.err.println("Tópico não encontrado com ID: " + c.getCdtopic() + " no template: " + templateId);
        } catch (Exception e) {
            System.err.println("Erro ao inserir cláusula: " + e.getMessage());
        }
    }

    /**
     * Busca uma cláusula pelo seu identificador, pesquisando em todos os templates/tópicos.
     *
     * @param id Identificador da cláusula.
     * @return Objeto Clauses ou null.
     */
    @SuppressWarnings("unchecked")
    public Clauses findById(int id) {
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document templateDoc = cursor.next();
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        int cdtopic = topicDoc.getInteger("cdtopic");
                        List<Document> clauses = (List<Document>) topicDoc.get("clauses");
                        if (clauses != null) {
                            for (Document clauseDoc : clauses) {
                                if (clauseDoc.getInteger("cdclause") == id) {
                                    return fromDocument(clauseDoc, cdtopic);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cláusula por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as cláusulas vinculadas a um tópico específico.
     * Pesquisa em todos os templates para encontrar o tópico.
     *
     * @param topicId Identificador do tópico.
     * @return Lista de objetos Clauses ordenadas por nrorder.
     */
    @SuppressWarnings("unchecked")
    public List<Clauses> findByTopicId(int topicId) {
        List<Clauses> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document templateDoc = cursor.next();
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        if (topicDoc.getInteger("cdtopic") == topicId) {
                            List<Document> clauses = (List<Document>) topicDoc.get("clauses");
                            if (clauses != null) {
                                for (Document clauseDoc : clauses) {
                                    list.add(fromDocument(clauseDoc, topicId));
                                }
                            }
                            // Ordena por nrorder
                            list.sort((a, b) -> Integer.compare(a.getNrorder(), b.getNrorder()));
                            return list;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cláusulas por tópico: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca cláusulas de um tópico dentro de um template específico.
     *
     * @param topicId Identificador do tópico.
     * @param templateId Identificador do template.
     * @return Lista de objetos Clauses ordenadas por nrorder.
     */
    @SuppressWarnings("unchecked")
    public List<Clauses> findByTopicId(int topicId, int templateId) {
        List<Clauses> list = new ArrayList<>();
        try {
            Document templateDoc = getCollection().find(Filters.eq("_id", templateId)).first();
            if (templateDoc != null) {
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        if (topicDoc.getInteger("cdtopic") == topicId) {
                            List<Document> clauses = (List<Document>) topicDoc.get("clauses");
                            if (clauses != null) {
                                for (Document clauseDoc : clauses) {
                                    list.add(fromDocument(clauseDoc, topicId));
                                }
                            }
                            list.sort((a, b) -> Integer.compare(a.getNrorder(), b.getNrorder()));
                            return list;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cláusulas por tópico e template: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista todas as cláusulas de todos os templates/tópicos.
     *
     * @return Lista de objetos Clauses.
     */
    @SuppressWarnings("unchecked")
    public List<Clauses> listAll() {
        List<Clauses> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document templateDoc = cursor.next();
                List<Document> topics = (List<Document>) templateDoc.get("topics");
                if (topics != null) {
                    for (Document topicDoc : topics) {
                        int cdtopic = topicDoc.getInteger("cdtopic");
                        List<Document> clauses = (List<Document>) topicDoc.get("clauses");
                        if (clauses != null) {
                            for (Document clauseDoc : clauses) {
                                list.add(fromDocument(clauseDoc, cdtopic));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar cláusulas: " + e.getMessage());
        }
        list.sort((a, b) -> {
            int cmp = Integer.compare(a.getCdtopic(), b.getCdtopic());
            return cmp != 0 ? cmp : Integer.compare(a.getNrorder(), b.getNrorder());
        });
        return list;
    }

    /**
     * Atualiza os dados de uma cláusula existente.
     * Busca em todos os templates/tópicos para encontrar e atualizar.
     *
     * @param c Objeto contendo os dados atualizados.
     */
    @SuppressWarnings("unchecked")
    public void update(Clauses c) {
        try {
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext()) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (int i = 0; i < topics.size(); i++) {
                            List<Document> clauses = (List<Document>) topics.get(i).get("clauses");
                            if (clauses != null) {
                                for (int j = 0; j < clauses.size(); j++) {
                                    if (clauses.get(j).getInteger("cdclause") == c.getCdclause()) {
                                        // Atualiza usando $set com dot notation
                                        String basePath = "topics." + i + ".clauses." + j;
                                        getCollection().updateOne(
                                            Filters.eq("_id", templateDoc.getInteger("_id")),
                                            Updates.combine(
                                                Updates.set(basePath + ".dstext", c.getDstext()),
                                                Updates.set(basePath + ".nrorder", c.getNrorder())
                                            )
                                        );
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cláusula: " + e.getMessage());
        }
    }

    /**
     * Exclui uma cláusula pelo seu identificador.
     * Busca em todos os templates/tópicos e remove com $pull.
     *
     * @param id Identificador da cláusula.
     * @return true se excluída com sucesso.
     */
    @SuppressWarnings("unchecked")
    public boolean delete(int id) {
        try {
            try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
                while (cursor.hasNext()) {
                    Document templateDoc = cursor.next();
                    List<Document> topics = (List<Document>) templateDoc.get("topics");
                    if (topics != null) {
                        for (int i = 0; i < topics.size(); i++) {
                            List<Document> clauses = (List<Document>) topics.get(i).get("clauses");
                            if (clauses != null) {
                                for (Document clauseDoc : clauses) {
                                    if (clauseDoc.getInteger("cdclause") == id) {
                                        // Remove a cláusula com $pull no array do tópico
                                        String arrayPath = "topics." + i + ".clauses";
                                        getCollection().updateOne(
                                            Filters.eq("_id", templateDoc.getInteger("_id")),
                                            Updates.pull(arrayPath, new Document("cdclause", id))
                                        );
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir cláusula: " + e.getMessage());
        }
        return false;
    }
}
