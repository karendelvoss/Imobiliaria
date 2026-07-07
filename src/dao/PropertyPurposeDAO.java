package dao;

import model.Property_Purposes;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gerencia as operações de persistência para as finalidades de imóvel (ex: Residencial, Comercial).
 *
 * Na modelagem MongoDB, finalidades de imóvel são texto embarcado no campo "purpose"
 * da coleção "properties". Não existe coleção separada para finalidades.
 * Os métodos extraem valores distintos do campo purpose para manter
 * compatibilidade com a camada de view.
 */
public class PropertyPurposeDAO {

    private static final String COLLECTION_NAME = "properties";
    private static final String FIELD = "purpose";

    /**
     * Obtém a coleção MongoDB de imóveis (onde purpose está embarcado).
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Retorna todas as finalidades de imóvel distintas encontradas na coleção properties,
     * ordenadas alfabeticamente.
     *
     * @return Lista ordenada de nomes de finalidades distintas.
     */
    private List<String> getDistinctPurposes() {
        List<String> purposes = new ArrayList<>();
        try {
            for (String val : getCollection().distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    purposes.add(val);
                }
            }
            Collections.sort(purposes);
        } catch (Exception e) {
            System.err.println("Erro ao listar finalidades de imóvel distintas: " + e.getMessage());
        }
        return purposes;
    }

    /**
     * Insere uma nova finalidade de imóvel.
     * Como finalidades são texto embarcado nos imóveis, esta operação apenas
     * exibe uma mensagem informativa. A finalidade será efetivamente registrada
     * ao ser usada em um imóvel.
     *
     * @param pp Objeto contendo os dados da finalidade.
     */
    public void insert(Property_Purposes pp) {
        System.out.println("[INFO] No MongoDB, finalidades de imóvel são texto embarcado no cadastro do imóvel.");
        System.out.println("A finalidade '" + pp.getNmpurpose() + "' será registrada ao ser usada em um imóvel.");
        List<String> existing = getDistinctPurposes();
        pp.setCdpurpose(existing.size() + 1);
    }

    /**
     * Busca uma finalidade de imóvel pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de finalidades distintas.
     *
     * @param id Identificador virtual da finalidade.
     * @return Objeto Property_Purposes ou null.
     */
    public Property_Purposes findById(int id) {
        try {
            List<String> purposes = getDistinctPurposes();
            if (id >= 1 && id <= purposes.size()) {
                Property_Purposes pp = new Property_Purposes();
                pp.setCdpurpose(id);
                pp.setNmpurpose(purposes.get(id - 1));
                return pp;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar finalidade de imóvel por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas as finalidades de imóvel distintas encontradas na coleção properties.
     *
     * @return Lista de objetos Property_Purposes com IDs virtuais.
     */
    public List<Property_Purposes> listAll() {
        List<Property_Purposes> list = new ArrayList<>();
        try {
            List<String> purposes = getDistinctPurposes();
            for (int i = 0; i < purposes.size(); i++) {
                Property_Purposes pp = new Property_Purposes();
                pp.setCdpurpose(i + 1);
                pp.setNmpurpose(purposes.get(i));
                list.add(pp);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar finalidades de imóvel: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de uma finalidade de imóvel em todos os imóveis que a utilizam.
     *
     * @param pp Objeto contendo o ID virtual e o novo nome da finalidade.
     */
    public void update(Property_Purposes pp) {
        try {
            List<String> purposes = getDistinctPurposes();
            int idx = pp.getCdpurpose() - 1;
            if (idx >= 0 && idx < purposes.size()) {
                String oldName = purposes.get(idx);
                String newName = pp.getNmpurpose();

                getCollection().updateMany(
                    Filters.eq(FIELD, oldName),
                    Updates.set(FIELD, newName)
                );

                System.out.println("Finalidade de imóvel atualizada com sucesso. Todos os imóveis com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhuma finalidade de imóvel encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar finalidade de imóvel: " + e.getMessage());
        }
    }

    /**
     * Exclui uma finalidade de imóvel. A exclusão é impedida se existirem imóveis
     * que ainda utilizam o valor.
     *
     * @param id Identificador virtual da finalidade.
     * @return true se a finalidade não estava em uso (removível), false caso contrário.
     */
    public boolean delete(int id) {
        try {
            List<String> purposes = getDistinctPurposes();
            if (id >= 1 && id <= purposes.size()) {
                String name = purposes.get(id - 1);
                long count = getCollection().countDocuments(Filters.eq(FIELD, name));
                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. A finalidade '" + name
                        + "' está em uso em " + count + " imóvel(is).");
                    return false;
                } else {
                    System.out.println("Finalidade '" + name + "' removida (não estava em uso).");
                    return true;
                }
            } else {
                System.out.println("Nenhuma finalidade de imóvel encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir finalidade de imóvel: " + e.getMessage());
        }
        return false;
    }
}
