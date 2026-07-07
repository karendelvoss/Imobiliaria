package dao;

import model.Bank_Accounts;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para contas bancárias.
 *
 * No modelo MongoDB, contas bancárias são embarcadas como array
 * {@code bank_accounts} dentro do documento do usuário na coleção {@code users}.
 * Cada conta possui um campo {@code cdaccount} gerado via {@link SequenceGenerator}.
 *
 * Operações usam {@code $push}, {@code $pull} e {@code $set} com operador
 * posicional {@code $} para manipular o array embarcado.
 */
public class BankAccountDAO {

    private static final String USERS_COLLECTION = "users";

    /**
     * Obtém a coleção MongoDB de usuários.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(USERS_COLLECTION);
    }

    /**
     * Converte um objeto Bank_Accounts para Document BSON (subdocumento de conta bancária).
     *
     * @param ba Objeto do modelo.
     * @return Document BSON correspondente ao subdocumento.
     */
    public static Document toDocument(Bank_Accounts ba) {
        if (ba == null) return null;
        Document doc = new Document();
        doc.append("cdaccount", ba.getCdbankaccount());
        doc.append("nragency", ba.getNragency());
        doc.append("nraccount", ba.getNraccount());
        doc.append("nrpixkey", ba.getNrpixkey());
        return doc;
    }

    /**
     * Converte um Document BSON (subdocumento de conta bancária) para objeto Bank_Accounts.
     *
     * @param doc Document BSON do subdocumento.
     * @param userId ID do usuário proprietário da conta.
     * @return Objeto do modelo Bank_Accounts, ou null se doc for null.
     */
    public static Bank_Accounts fromDocument(Document doc, int userId) {
        if (doc == null) return null;
        Bank_Accounts ba = new Bank_Accounts();
        ba.setCdbankaccount(doc.getInteger("cdaccount"));
        ba.setNragency(doc.getString("nragency"));
        ba.setNraccount(doc.getString("nraccount"));
        ba.setNrpixkey(doc.getString("nrpixkey"));
        ba.setCduser(userId);
        return ba;
    }

    /**
     * Insere uma nova conta bancária no array {@code bank_accounts} do usuário.
     * Gera o ID sequencial via {@link SequenceGenerator}.
     *
     * @param ba Objeto contendo os dados da conta (cduser deve estar preenchido).
     */
    public void insert(Bank_Accounts ba) {
        try {
            int newId = SequenceGenerator.getNextSequence("bank_accounts");
            ba.setCdbankaccount(newId);

            Document accountDoc = toDocument(ba);

            UpdateResult result = getCollection().updateOne(
                    Filters.eq("_id", ba.getCduser()),
                    Updates.push("bank_accounts", accountDoc));

            if (result.getModifiedCount() > 0) {
                System.out.println("Conta bancária inserida com sucesso! (ID: " + newId + ")");
            } else {
                System.err.println("Erro ao inserir conta bancária: usuário não encontrado (ID: " + ba.getCduser() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inserir conta bancária: " + e.getMessage());
        }
    }

    /**
     * Busca uma conta bancária pelo seu ID (cdaccount).
     * Percorre todos os usuários para encontrar a conta no array embarcado.
     *
     * @param cdaccount Identificador da conta.
     * @return Objeto Bank_Accounts ou null se não encontrado.
     */
    public Bank_Accounts findById(int cdaccount) {
        try {
            Document userDoc = getCollection().find(
                    Filters.elemMatch("bank_accounts", Filters.eq("cdaccount", cdaccount))
            ).first();

            if (userDoc != null) {
                int userId = userDoc.getInteger("_id");
                List<Document> accounts = userDoc.getList("bank_accounts", Document.class);
                if (accounts != null) {
                    for (Document accDoc : accounts) {
                        if (accDoc.getInteger("cdaccount") == cdaccount) {
                            return fromDocument(accDoc, userId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar conta bancária por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as contas bancárias vinculadas a um usuário.
     *
     * @param userId Identificador do usuário.
     * @return Lista de objetos Bank_Accounts do usuário, ou lista vazia.
     */
    public List<Bank_Accounts> findByUserId(int userId) {
        List<Bank_Accounts> list = new ArrayList<>();
        try {
            Document userDoc = getCollection().find(Filters.eq("_id", userId)).first();
            if (userDoc != null) {
                List<Document> accounts = userDoc.getList("bank_accounts", Document.class);
                if (accounts != null) {
                    for (Document accDoc : accounts) {
                        list.add(fromDocument(accDoc, userId));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contas por usuário: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista todas as contas bancárias cadastradas no sistema.
     * Percorre todos os usuários e extrai as contas do array embarcado.
     *
     * @return Lista de contas bancárias.
     */
    public List<Bank_Accounts> listAll() {
        List<Bank_Accounts> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.exists("bank_accounts")).iterator()) {
            while (cursor.hasNext()) {
                Document userDoc = cursor.next();
                int userId = userDoc.getInteger("_id");
                List<Document> accounts = userDoc.getList("bank_accounts", Document.class);
                if (accounts != null) {
                    for (Document accDoc : accounts) {
                        list.add(fromDocument(accDoc, userId));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar contas bancárias: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza uma conta bancária existente no array embarcado.
     * Usa o operador posicional {@code $} para atualizar o elemento correspondente.
     *
     * @param ba Objeto contendo os dados atualizados (cduser e cdbankaccount devem estar preenchidos).
     */
    public void update(Bank_Accounts ba) {
        try {
            UpdateResult result = getCollection().updateOne(
                    Filters.and(
                            Filters.eq("_id", ba.getCduser()),
                            Filters.elemMatch("bank_accounts", Filters.eq("cdaccount", ba.getCdbankaccount()))
                    ),
                    Updates.combine(
                            Updates.set("bank_accounts.$.nragency", ba.getNragency()),
                            Updates.set("bank_accounts.$.nraccount", ba.getNraccount()),
                            Updates.set("bank_accounts.$.nrpixkey", ba.getNrpixkey())
                    ));

            if (result.getModifiedCount() == 0) {
                System.err.println("Conta bancária não encontrada para atualização (ID: " + ba.getCdbankaccount() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar conta bancária: " + e.getMessage());
        }
    }

    /**
     * Exclui uma conta bancária do array embarcado usando {@code $pull}.
     * Busca em todos os usuários qual contém a conta com o ID especificado.
     *
     * @param cdaccount Identificador da conta.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int cdaccount) {
        try {
            UpdateResult result = getCollection().updateOne(
                    Filters.elemMatch("bank_accounts", Filters.eq("cdaccount", cdaccount)),
                    Updates.pull("bank_accounts", new Document("cdaccount", cdaccount)));

            if (result.getModifiedCount() > 0) {
                return true;
            } else {
                System.err.println("Conta bancária não encontrada para exclusão (ID: " + cdaccount + ")");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir conta bancária: " + e.getMessage());
        }
        return false;
    }
}
