package dao;

import model.Users;
import model.Addresses;
import model.Broker_Data;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.MongoWriteException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os usuários e corretores.
 *
 * No modelo MongoDB, o documento de usuário contém:
 * <ul>
 *   <li>Endereço embarcado como subdocumento {@code address}</li>
 *   <li>Profissão embarcada como campo texto {@code occupation}</li>
 *   <li>Contas bancárias como array {@code bank_accounts}</li>
 *   <li>Dados de corretor (nrcreci) como campo direto no documento</li>
 * </ul>
 */
public class UserDAO {

    private static final String COLLECTION_NAME = "users";
    private static final String PROPERTIES_COLLECTION = "properties";
    private static final String CONTRACTS_COLLECTION = "contracts";

    /**
     * Obtém a coleção MongoDB de usuários.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Users para Document BSON.
     * Inclui subdocumento de endereço e campo de profissão.
     *
     * @param user Objeto do modelo.
     * @param address Objeto de endereço para embarcar (pode ser null).
     * @param occupationName Nome da profissão para embarcar (pode ser null).
     * @param broker Dados de corretor (pode ser null).
     * @return Document BSON correspondente.
     */
    public static Document toDocument(Users user, Addresses address, String occupationName, Broker_Data broker) {
        Document doc = new Document();
        doc.append("_id", user.getCduser());
        doc.append("nmuser", user.getNmuser());
        doc.append("dtbirth", user.getDtbirth() != null ? user.getDtbirth().toString() : null);
        doc.append("fgdocument", user.isFgdocument());
        doc.append("document", user.getDocument());
        doc.append("nrcellphone", user.getNrcellphone());
        doc.append("dsissuingbody", user.getDsissuingbody());
        doc.append("address", AddressDAO.toDocument(address));
        doc.append("occupation", occupationName);
        doc.append("bank_accounts", new ArrayList<>());
        if (broker != null) {
            doc.append("nrcreci", broker.getNrcreci());
        }
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Users.
     * Extrai o endereço do subdocumento embarcado.
     *
     * @param doc Document BSON.
     * @return Objeto Users, ou null se doc for null.
     */
    public static Users fromDocument(Document doc) {
        if (doc == null) return null;
        Users u = new Users();
        u.setCduser(doc.getInteger("_id"));
        u.setNmuser(doc.getString("nmuser"));
        String dtbirth = doc.getString("dtbirth");
        if (dtbirth != null) {
            u.setDtbirth(java.time.LocalDate.parse(dtbirth));
        }
        u.setFgdocument(doc.getBoolean("fgdocument", false));
        u.setDocument(doc.getString("document"));
        u.setNrcellphone(doc.getString("nrcellphone"));
        u.setDsissuingbody(doc.getString("dsissuingbody"));
        // cdaddress stores the user's own _id (address is embedded in the user document)
        u.setCdaddress(doc.getInteger("_id"));
        // cdoccupation is no longer a FK; set to 0 as placeholder
        u.setCdoccupation(0);
        return u;
    }

    /**
     * Busca um usuário pelo seu identificador.
     *
     * @param id Identificador do usuário.
     * @return Objeto Users ou null.
     */
    public Users findById(int id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", id)).first();
            return fromDocument(doc);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param u Objeto contendo os dados atualizados do usuário.
     */
    public void update(Users u) {
        try {
            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.set("nmuser", u.getNmuser()));
            updates.add(Updates.set("document", u.getDocument()));
            updates.add(Updates.set("fgdocument", u.isFgdocument()));
            updates.add(Updates.set("nrcellphone", u.getNrcellphone()));
            updates.add(Updates.set("dtbirth", u.getDtbirth() != null ? u.getDtbirth().toString() : null));
            updates.add(Updates.set("dsissuingbody", u.getDsissuingbody()));

            getCollection().updateOne(
                    Filters.eq("_id", u.getCduser()),
                    Updates.combine(updates));
        } catch (MongoWriteException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                System.err.println("ERRO: Já existe um usuário cadastrado com este documento.");
            } else {
                System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    /**
     * Verifica se um usuário possui vínculos ativos no sistema (imóveis, contratos).
     * Consulta a coleção properties (array owners) e contracts (array participants).
     *
     * @param id Identificador do usuário.
     * @return Descrição do vínculo ou null caso não possua.
     */
    public String verificarVinculos(int id) {
        try {
            // Verificar se o usuário é proprietário de algum imóvel (properties.owners contém o ID)
            MongoCollection<Document> properties = Conexao.getCollection(PROPERTIES_COLLECTION);
            Document propDoc = properties.find(Filters.in("owners", id)).first();
            if (propDoc != null) {
                return "PROPRIETÁRIO VINCULADO A IMÓVEL";
            }

            // Verificar se o usuário é participante de algum contrato (contracts.participants[].cduser)
            MongoCollection<Document> contracts = Conexao.getCollection(CONTRACTS_COLLECTION);
            Document contDoc = contracts.find(Filters.eq("participants.cduser", id)).first();
            if (contDoc != null) {
                return "CLIENTE COM CONTRATO ATIVO";
            }

            // Verificar se o usuário possui contas bancárias embarcadas
            Document userDoc = getCollection().find(Filters.eq("_id", id)).first();
            if (userDoc != null) {
                List<?> bankAccounts = userDoc.getList("bank_accounts", Document.class);
                if (bankAccounts != null && !bankAccounts.isEmpty()) {
                    return "CONTA BANCÁRIA VINCULADA";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO AO VERIFICAR VÍNCULOS";
        }
        return null;
    }

    /**
     * Exclui um usuário pelo seu identificador.
     *
     * @param id Identificador do usuário.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        try {
            return getCollection().deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Salva um novo usuário e seus dados de corretor (se houver) em uma inserção atômica.
     * O endereço e a profissão são embarcados no documento do usuário.
     *
     * @param user Objeto contendo os dados do usuário.
     * @param broker Objeto contendo os dados do corretor ou null.
     */
    public void saveUser(Users user, Broker_Data broker) {
        try {
            int newId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            user.setCduser(newId);

            // Buscar endereço para embarcar (se cdaddress foi definido)
            Addresses address = null;
            if (user.getCdaddress() > 0) {
                AddressDAO addressDAO = new AddressDAO();
                address = addressDAO.findById(user.getCdaddress());
            }

            // Buscar nome da profissão para embarcar (se cdoccupation foi definido)
            String occupationName = null;
            if (user.getCdoccupation() > 0) {
                OccupationDAO occupationDAO = new OccupationDAO();
                model.Occupations occ = occupationDAO.findById(user.getCdoccupation());
                if (occ != null) {
                    occupationName = occ.getNmoccupation();
                }
            }

            Document doc = toDocument(user, address, occupationName, broker);
            getCollection().insertOne(doc);
        } catch (MongoWriteException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                System.err.println("ERRO: Já existe um usuário cadastrado com este documento.");
            } else {
                System.err.println("Erro ao salvar usuário: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    /**
     * Lista todos os usuários cadastrados de forma simplificada.
     *
     * @return Lista de Strings formatadas com ID e Nome.
     */
    public List<String> getAllUsersList() {
        List<String> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find()
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add("ID: " + doc.getInteger("_id") + " | Nome: " + doc.getString("nmuser"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lista apenas os usuários que podem ser excluídos por não possuírem vínculos ativos.
     * Exclui usuários que são proprietários de imóveis, participantes de contratos
     * ou que possuem contas bancárias embarcadas.
     *
     * @return Lista de Strings formatadas com ID e Nome.
     */
    public List<String> getDeletableUsers() {
        List<String> list = new ArrayList<>();
        try {
            // Coletar IDs de usuários com vínculos em properties (owners)
            MongoCollection<Document> properties = Conexao.getCollection(PROPERTIES_COLLECTION);
            List<Integer> ownerIds = new ArrayList<>();
            try (MongoCursor<Document> cursor = properties.find(Filters.exists("owners")).iterator()) {
                while (cursor.hasNext()) {
                    Document propDoc = cursor.next();
                    List<Integer> owners = propDoc.getList("owners", Integer.class);
                    if (owners != null) {
                        ownerIds.addAll(owners);
                    }
                }
            }

            // Coletar IDs de usuários com vínculos em contracts (participants)
            MongoCollection<Document> contracts = Conexao.getCollection(CONTRACTS_COLLECTION);
            List<Integer> participantIds = new ArrayList<>();
            try (MongoCursor<Document> cursor = contracts.find(Filters.exists("participants")).iterator()) {
                while (cursor.hasNext()) {
                    Document contDoc = cursor.next();
                    List<Document> participants = contDoc.getList("participants", Document.class);
                    if (participants != null) {
                        for (Document p : participants) {
                            participantIds.add(p.getInteger("cduser"));
                        }
                    }
                }
            }

            // Listar usuários que NÃO estão em nenhuma das listas acima e NÃO possuem bank_accounts
            try (MongoCursor<Document> cursor = getCollection()
                    .find()
                    .sort(new Document("_id", 1))
                    .iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    int userId = doc.getInteger("_id");
                    if (ownerIds.contains(userId)) continue;
                    if (participantIds.contains(userId)) continue;
                    List<Document> bankAccounts = doc.getList("bank_accounts", Document.class);
                    if (bankAccounts != null && !bankAccounts.isEmpty()) continue;
                    list.add("ID: " + userId + " | Nome: " + doc.getString("nmuser"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
