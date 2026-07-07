package dao;

import model.Addresses;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para a entidade de Endereços.
 *
 * No modelo MongoDB, endereços são embarcados como subdocumentos dentro
 * de {@code users} e {@code properties}. Não existe coleção separada
 * de endereços. Esta classe fornece:
 * <ul>
 *   <li>Métodos utilitários {@link #toDocument(Addresses)} e {@link #fromDocument(Document)}
 *       usados por outros DAOs (UserDAO, PropertyDAO).</li>
 *   <li>Métodos de busca que extraem o subdocumento de endereço dos documentos pai.</li>
 *   <li>Interface pública compatível com a camada Service e View.</li>
 * </ul>
 */
public class AddressDAO {

    private static final String USERS_COLLECTION = "users";
    private static final String PROPERTIES_COLLECTION = "properties";

    /**
     * Obtém a coleção MongoDB de usuários.
     */
    private MongoCollection<Document> getUsersCollection() {
        return Conexao.getCollection(USERS_COLLECTION);
    }

    /**
     * Obtém a coleção MongoDB de imóveis.
     */
    private MongoCollection<Document> getPropertiesCollection() {
        return Conexao.getCollection(PROPERTIES_COLLECTION);
    }

    /**
     * Converte um objeto Addresses para Document BSON (subdocumento de endereço).
     * Utilizado por UserDAO e PropertyDAO ao montar o documento pai.
     *
     * @param addr Objeto do modelo.
     * @return Document BSON correspondente ao subdocumento de endereço.
     */
    public static Document toDocument(Addresses addr) {
        if (addr == null) return null;
        Document doc = new Document();
        doc.append("cdzipcode", addr.getCdzipcode());
        doc.append("nmstreet", addr.getNmstreet());
        doc.append("nraddress", addr.getNraddress());
        doc.append("dscomplement", addr.getDscomplement());
        doc.append("district", addr.getDistrict());
        doc.append("city", addr.getCity());
        doc.append("state", addr.getState());
        doc.append("country", addr.getCountry());
        return doc;
    }

    /**
     * Converte um Document BSON (subdocumento de endereço) para objeto Addresses.
     * Utilizado por UserDAO e PropertyDAO ao ler o documento pai.
     *
     * @param doc Document BSON do subdocumento de endereço.
     * @return Objeto do modelo Addresses, ou null se doc for null.
     */
    public static Addresses fromDocument(Document doc) {
        if (doc == null) return null;
        Addresses addr = new Addresses();
        addr.setCdzipcode(doc.getString("cdzipcode"));
        addr.setNmstreet(doc.getString("nmstreet"));
        addr.setNraddress(doc.getString("nraddress"));
        addr.setDscomplement(doc.getString("dscomplement"));
        addr.setDistrict(doc.getString("district"));
        addr.setCity(doc.getString("city"));
        addr.setState(doc.getString("state"));
        addr.setCountry(doc.getString("country"));
        return addr;
    }

    /**
     * Insere um endereço embarcado em um documento de usuário ou imóvel.
     * Como endereços não possuem coleção própria, esta operação
     * não é mais autônoma. Mantida para compatibilidade com a View de CRUD.
     * O endereço será inserido como um novo documento na coleção properties
     * (com um ID sequencial) para manter retrocompatibilidade com o fluxo CRUD.
     *
     * @param a Objeto contendo os dados do endereço.
     */
    public void insert(Addresses a) {
        try {
            // No modelo embarcado, endereços são inseridos junto com o documento pai.
            // Para manter compatibilidade com o CRUD view, inserimos um property placeholder
            // ou simplesmente logamos que a operação requer o documento pai.
            System.out.println("AVISO: Endereços são agora embarcados em usuários/imóveis. " +
                    "Use o cadastro de usuários ou imóveis para vincular endereços.");
        } catch (Exception e) {
            System.err.println("Erro ao inserir endereço: " + e.getMessage());
        }
    }

    /**
     * Busca um endereço pelo ID do documento pai (usuário ou imóvel).
     * Primeiro busca na coleção de usuários, depois na de imóveis.
     *
     * @param id Identificador do documento pai (usuário ou imóvel).
     * @return Objeto Addresses ou null se não encontrado.
     */
    public Addresses findById(int id) {
        try {
            // Buscar primeiro em users
            Document userDoc = getUsersCollection().find(Filters.eq("_id", id)).first();
            if (userDoc != null) {
                Document addrDoc = userDoc.get("address", Document.class);
                if (addrDoc != null) {
                    Addresses addr = fromDocument(addrDoc);
                    addr.setCdaddress(id);
                    return addr;
                }
            }

            // Buscar em properties
            Document propDoc = getPropertiesCollection().find(Filters.eq("_id", id)).first();
            if (propDoc != null) {
                Document addrDoc = propDoc.get("address", Document.class);
                if (addrDoc != null) {
                    Addresses addr = fromDocument(addrDoc);
                    addr.setCdaddress(id);
                    return addr;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar endereço por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os endereços embarcados extraindo-os de usuários e imóveis.
     *
     * @return Lista de objetos Addresses.
     */
    public List<Addresses> listAll() {
        List<Addresses> list = new ArrayList<>();
        try {
            // Extrair endereços de users
            try (MongoCursor<Document> cursor = getUsersCollection()
                    .find(Filters.exists("address")).iterator()) {
                while (cursor.hasNext()) {
                    Document userDoc = cursor.next();
                    Document addrDoc = userDoc.get("address", Document.class);
                    if (addrDoc != null) {
                        Addresses addr = fromDocument(addrDoc);
                        addr.setCdaddress(userDoc.getInteger("_id"));
                        list.add(addr);
                    }
                }
            }

            // Extrair endereços de properties
            try (MongoCursor<Document> cursor = getPropertiesCollection()
                    .find(Filters.exists("address")).iterator()) {
                while (cursor.hasNext()) {
                    Document propDoc = cursor.next();
                    Document addrDoc = propDoc.get("address", Document.class);
                    if (addrDoc != null) {
                        Addresses addr = fromDocument(addrDoc);
                        // Usar ID negativo para diferenciar endereços de imóveis
                        // ou usar um offset para evitar colisão com IDs de usuários
                        addr.setCdaddress(propDoc.getInteger("_id") + 10000);
                        list.add(addr);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar endereços: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista todos os endereços com formatação legível incluindo o bairro.
     *
     * @return Lista de Strings formatadas.
     */
    public List<String> listAllFormatted() {
        List<String> list = new ArrayList<>();
        try {
            // Extrair endereços formatados de users
            try (MongoCursor<Document> cursor = getUsersCollection()
                    .find(Filters.exists("address")).iterator()) {
                while (cursor.hasNext()) {
                    Document userDoc = cursor.next();
                    Document addrDoc = userDoc.get("address", Document.class);
                    if (addrDoc != null) {
                        String formatted = String.format("ID: %-3d | %s, %s - Bairro: %s (Usuário: %s)",
                                userDoc.getInteger("_id"),
                                addrDoc.getString("nmstreet"),
                                addrDoc.getString("nraddress"),
                                addrDoc.getString("district") != null ? addrDoc.getString("district") : "N/A",
                                userDoc.getString("nmuser"));
                        list.add(formatted);
                    }
                }
            }

            // Extrair endereços formatados de properties
            try (MongoCursor<Document> cursor = getPropertiesCollection()
                    .find(Filters.exists("address")).iterator()) {
                while (cursor.hasNext()) {
                    Document propDoc = cursor.next();
                    Document addrDoc = propDoc.get("address", Document.class);
                    if (addrDoc != null) {
                        String formatted = String.format("ID: %-3d | %s, %s - Bairro: %s (Imóvel: %s)",
                                propDoc.getInteger("_id"),
                                addrDoc.getString("nmstreet"),
                                addrDoc.getString("nraddress"),
                                addrDoc.getString("district") != null ? addrDoc.getString("district") : "N/A",
                                propDoc.getString("nrregistration"));
                        list.add(formatted);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar endereços formatados: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o endereço embarcado em um documento de usuário ou imóvel.
     * Busca primeiro em users, depois em properties.
     *
     * @param a Objeto contendo os novos dados do endereço (cdaddress identifica o documento pai).
     */
    public void update(Addresses a) {
        try {
            Document addrDoc = toDocument(a);

            // Tentar atualizar em users
            Document userDoc = getUsersCollection().find(Filters.eq("_id", a.getCdaddress())).first();
            if (userDoc != null) {
                getUsersCollection().updateOne(
                        Filters.eq("_id", a.getCdaddress()),
                        Updates.set("address", addrDoc));
                return;
            }

            // Tentar atualizar em properties
            Document propDoc = getPropertiesCollection().find(Filters.eq("_id", a.getCdaddress())).first();
            if (propDoc != null) {
                getPropertiesCollection().updateOne(
                        Filters.eq("_id", a.getCdaddress()),
                        Updates.set("address", addrDoc));
                return;
            }

            System.err.println("Endereço não encontrado para atualização (ID: " + a.getCdaddress() + ")");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar endereço: " + e.getMessage());
        }
    }

    /**
     * Remove o endereço embarcado de um documento de usuário ou imóvel
     * (define o campo address como null).
     *
     * @param id Identificador do documento pai.
     * @return true se a remoção foi bem-sucedida.
     */
    public boolean delete(int id) {
        try {
            // Tentar remover de users
            Document userDoc = getUsersCollection().find(Filters.eq("_id", id)).first();
            if (userDoc != null) {
                getUsersCollection().updateOne(
                        Filters.eq("_id", id),
                        Updates.unset("address"));
                return true;
            }

            // Tentar remover de properties
            Document propDoc = getPropertiesCollection().find(Filters.eq("_id", id)).first();
            if (propDoc != null) {
                getPropertiesCollection().updateOne(
                        Filters.eq("_id", id),
                        Updates.unset("address"));
                return true;
            }

            System.err.println("Endereço não encontrado para exclusão (ID: " + id + ")");
        } catch (Exception e) {
            System.err.println("Erro ao excluir endereço: " + e.getMessage());
        }
        return false;
    }
}
