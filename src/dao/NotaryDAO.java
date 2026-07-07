package dao;

import model.Notaries;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os dados notariais (Notaries).
 *
 * No modelo MongoDB, os dados notariais são embarcados como subdocumento "notary"
 * dentro do documento de contrato na coleção "contracts".
 *
 * Estrutura esperada:
 * <pre>
 * {
 *   "_id": 9,
 *   "notary": {
 *     "cdnotary": 1,
 *     "cdcity": 5,
 *     "book": "Livro A",
 *     "leaf": "Folha 12",
 *     "dt": "2025-04-25",
 *     "nrnotary": 100
 *   }
 * }
 * </pre>
 */
public class NotaryDAO {

    private static final String COLLECTION_NAME = "contracts";
    private static final String NOTARY_FIELD = "notary";

    /**
     * Obtém a coleção MongoDB de contratos (onde notary está embarcado).
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    // ========== Conversão Model ↔ BSON ==========

    /**
     * Converte um objeto Notaries para Document BSON (subdocumento).
     *
     * @param notary Objeto do modelo.
     * @return Document BSON correspondente.
     */
    private Document toDocument(Notaries notary) {
        Document doc = new Document();
        doc.append("cdnotary", notary.getCdnotary());
        doc.append("cdcity", notary.getCdcity());
        doc.append("book", notary.getBook());
        doc.append("leaf", notary.getLeaf());
        doc.append("dt", notary.getDt() != null ? notary.getDt().toString() : null);
        doc.append("nrnotary", notary.getNrnotary());
        return doc;
    }

    /**
     * Converte um Document BSON (subdocumento) para objeto Notaries.
     *
     * @param doc Document BSON do subdocumento notary.
     * @return Objeto Notaries, ou null se doc for null.
     */
    private Notaries fromDocument(Document doc) {
        if (doc == null) return null;
        Notaries n = new Notaries();
        n.setCdnotary(doc.getInteger("cdnotary", 0));
        n.setCdcity(doc.getInteger("cdcity", 0));
        n.setBook(doc.getString("book"));
        n.setLeaf(doc.getString("leaf"));
        String dt = doc.getString("dt");
        if (dt != null && !dt.isEmpty()) {
            n.setDt(LocalDate.parse(dt));
        }
        n.setNrnotary(doc.getInteger("nrnotary", 0));
        return n;
    }

    // ========== Operações CRUD ==========

    /**
     * Insere um novo registro notarial gerando um ID sequencial.
     * Retorna o ID gerado para que o contrato possa ser vinculado via updateContract.
     *
     * Nota: No modelo MongoDB, os dados notariais completos serão embarcados no contrato
     * quando {@link #insert(Notaries, int)} for chamado ou quando o contrato for atualizado.
     *
     * @param notary Objeto contendo os dados notariais.
     * @return O ID gerado para o registro ou -1 em caso de erro.
     */
    public int insertNotary(Notaries notary) {
        try {
            int newId = SequenceGenerator.getNextSequence("notaries");
            notary.setCdnotary(newId);
            return newId;
        } catch (Exception e) {
            System.err.println("Erro ao gerar ID para Notary: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Insere os dados notariais como subdocumento no contrato especificado.
     * Gera um ID sequencial via SequenceGenerator e embarca o notary completo no contrato.
     *
     * @param notary Objeto contendo os dados notariais.
     * @param contractId Identificador do contrato onde embarcar o notary.
     * @return O ID gerado para o registro ou -1 em caso de erro.
     */
    public int insert(Notaries notary, int contractId) {
        try {
            int newId = SequenceGenerator.getNextSequence("notaries");
            notary.setCdnotary(newId);

            Document notaryDoc = toDocument(notary);
            getCollection().updateOne(
                Filters.eq("_id", contractId),
                Updates.set(NOTARY_FIELD, notaryDoc)
            );
            return newId;
        } catch (Exception e) {
            System.err.println("Erro ao inserir Notary no contrato: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Busca um registro notarial pelo seu identificador (cdnotary).
     * Pesquisa em todos os contratos pelo subdocumento notary com o cdnotary correspondente.
     *
     * @param cdnotary Identificador do registro notarial.
     * @return Objeto Notaries ou null se não encontrado.
     */
    public Notaries findById(int cdnotary) {
        try {
            Bson filter = Filters.eq("notary.cdnotary", cdnotary);
            Document contractDoc = getCollection().find(filter).first();
            if (contractDoc != null) {
                Document notaryDoc = contractDoc.get(NOTARY_FIELD, Document.class);
                return fromDocument(notaryDoc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar Notary por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca os dados notariais de um contrato específico.
     *
     * @param contractId Identificador do contrato.
     * @return Objeto Notaries ou null se o contrato não tiver notary.
     */
    public Notaries findByContractId(int contractId) {
        try {
            Document contractDoc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (contractDoc != null) {
                Document notaryDoc = contractDoc.get(NOTARY_FIELD, Document.class);
                return fromDocument(notaryDoc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar Notary por contrato: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os registros notariais cadastrados (extraídos de todos os contratos).
     *
     * @return Lista de objetos Notaries.
     */
    public List<Notaries> listAll() {
        List<Notaries> list = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                Filters.exists(NOTARY_FIELD, true),
                Filters.ne(NOTARY_FIELD, null)
            );
            try (MongoCursor<Document> cursor = getCollection().find(filter).iterator()) {
                while (cursor.hasNext()) {
                    Document contractDoc = cursor.next();
                    Document notaryDoc = contractDoc.get(NOTARY_FIELD, Document.class);
                    if (notaryDoc != null) {
                        list.add(fromDocument(notaryDoc));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar Notaries: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza os dados notariais no contrato onde o notary está embarcado.
     * Localiza o contrato pelo cdnotary e atualiza os campos do subdocumento.
     *
     * @param notary Objeto contendo os dados atualizados.
     */
    public void update(Notaries notary) {
        try {
            Document notaryDoc = toDocument(notary);
            getCollection().updateOne(
                Filters.eq("notary.cdnotary", notary.getCdnotary()),
                Updates.set(NOTARY_FIELD, notaryDoc)
            );
        } catch (Exception e) {
            System.err.println("Erro ao atualizar Notary: " + e.getMessage());
        }
    }

    /**
     * Remove os dados notariais (unset) do contrato que contém o cdnotary especificado.
     *
     * @param cdnotary Identificador do registro notarial a remover.
     * @return true se a operação modificou algum documento.
     */
    public boolean delete(int cdnotary) {
        try {
            long modified = getCollection().updateOne(
                Filters.eq("notary.cdnotary", cdnotary),
                Updates.set(NOTARY_FIELD, null)
            ).getModifiedCount();
            return modified > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir Notary: " + e.getMessage());
        }
        return false;
    }
}
