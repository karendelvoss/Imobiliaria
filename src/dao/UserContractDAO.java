package dao;

import dto.ParticipantDTO;
import model.User_Contract;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os participantes de contratos.
 *
 * No modelo MongoDB, os participantes são embarcados como array {@code participants}
 * dentro do documento de contrato na coleção {@code contracts}:
 * <pre>
 * "participants": [
 *     { "cduser": 1, "cdrole": 2, "nmrole": "Locador" },
 *     { "cduser": 2, "cdrole": 1, "nmrole": "Locatário" }
 * ]
 * </pre>
 */
public class UserContractDAO {

    private static final String CONTRACTS_COLLECTION = "contracts";
    private static final String USERS_COLLECTION = "users";

    /**
     * Obtém a coleção MongoDB de contratos.
     */
    private MongoCollection<Document> getContractsCollection() {
        return Conexao.getCollection(CONTRACTS_COLLECTION);
    }

    /**
     * Obtém a coleção MongoDB de usuários.
     */
    private MongoCollection<Document> getUsersCollection() {
        return Conexao.getCollection(USERS_COLLECTION);
    }

    /**
     * Resolve o nome do papel (role) a partir do código.
     */
    private static String resolveRoleName(int cdrole) {
        switch (cdrole) {
            case 1: return "Locatário";
            case 2: return "Locador";
            case 3: return "Testemunha";
            case 4: return "Representante Legal";
            default: return "Outro";
        }
    }

    /**
     * Vincula um usuário a um contrato com um determinado papel.
     * Utiliza o cdcontract armazenado no próprio objeto User_Contract.
     *
     * @param uc Objeto contendo o vínculo (contrato, usuário e papel).
     */
    public void insert(User_Contract uc) {
        insert(uc.getCdcontract(), uc);
    }

    /**
     * Vincula um usuário a um contrato com um determinado papel.
     * Utiliza operador $push para adicionar ao array de participantes embarcado no contrato.
     *
     * @param contractId Identificador do contrato.
     * @param uc Objeto contendo o vínculo (usuário e papel).
     */
    public void insert(int contractId, User_Contract uc) {
        try {
            Document participantDoc = new Document();
            participantDoc.append("cduser", uc.getCduser());
            participantDoc.append("cdrole", uc.getCdrole());
            participantDoc.append("nmrole", resolveRoleName(uc.getCdrole()));

            getContractsCollection().updateOne(
                Filters.eq("_id", contractId),
                Updates.push("participants", participantDoc)
            );
        } catch (Exception e) {
            System.err.println("Erro ao inserir participante no contrato: " + e.getMessage());
        }
    }

    /**
     * Busca todos os vínculos de usuários associados a um contrato.
     * Extrai o array {@code participants} do documento do contrato.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de objetos User_Contract.
     */
    public List<User_Contract> findByContractId(int contractId) {
        List<User_Contract> list = new ArrayList<>();
        try {
            Document contractDoc = getContractsCollection()
                .find(Filters.eq("_id", contractId))
                .first();

            if (contractDoc == null) {
                return list;
            }

            List<Document> participants = contractDoc.getList("participants", Document.class);
            if (participants == null) {
                return list;
            }

            for (Document pDoc : participants) {
                User_Contract uc = new User_Contract();
                uc.setCdcontract(contractId);
                uc.setCduser(pDoc.getInteger("cduser", 0));
                uc.setCdrole(pDoc.getInteger("cdrole", 0));
                list.add(uc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar participantes por contrato: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca os participantes de um contrato e consolida suas informações em um DTO para relatórios.
     * Extrai o array de participantes do contrato e faz lookup na coleção de usuários
     * para obter nome, documento e telefone.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de ParticipantDTO.
     */
    public List<ParticipantDTO> findParticipantsByContractId(int contractId) {
        List<ParticipantDTO> participants = new ArrayList<>();
        try {
            Document contractDoc = getContractsCollection()
                .find(Filters.eq("_id", contractId))
                .first();

            if (contractDoc == null) {
                return participants;
            }

            List<Document> participantDocs = contractDoc.getList("participants", Document.class);
            if (participantDocs == null) {
                return participants;
            }

            for (Document pDoc : participantDocs) {
                int cduser = pDoc.getInteger("cduser", 0);
                String nmrole = pDoc.getString("nmrole");

                // Lookup no usuário para obter nome, documento e telefone
                Document userDoc = getUsersCollection()
                    .find(Filters.eq("_id", cduser))
                    .first();

                ParticipantDTO dto = new ParticipantDTO();
                if (userDoc != null) {
                    dto.setNomeRazaoSocial(userDoc.getString("nmuser"));
                    dto.setCpfCnpj(userDoc.getString("document"));
                    dto.setContatoPrincipal(userDoc.getString("nrcellphone"));
                } else {
                    dto.setNomeRazaoSocial("Usuário não encontrado");
                    dto.setCpfCnpj("");
                    dto.setContatoPrincipal("");
                }
                dto.setPapelRole(nmrole != null ? nmrole : resolveRoleName(pDoc.getInteger("cdrole", 0)));
                participants.add(dto);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar participantes do contrato: " + e.getMessage());
        }
        return participants;
    }

    /**
     * Remove um participante de um contrato.
     * Utiliza operador $pull para remover do array de participantes embarcado no contrato.
     *
     * @param contractId Identificador do contrato.
     * @param userId Identificador do usuário a remover.
     */
    public void delete(int contractId, int userId) {
        try {
            getContractsCollection().updateOne(
                Filters.eq("_id", contractId),
                Updates.pull("participants", new Document("cduser", userId))
            );
        } catch (Exception e) {
            System.err.println("Erro ao remover participante do contrato: " + e.getMessage());
        }
    }
}
