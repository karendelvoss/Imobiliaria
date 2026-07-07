package dao;

import model.Property_Status;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gerencia as operações de persistência para os status de imóvel (ex: Disponível, Alugado).
 *
 * Na modelagem MongoDB, status de imóvel são texto embarcado no campo "status"
 * da coleção "properties". Não existe coleção separada para status.
 * Os métodos extraem valores distintos do campo status para manter
 * compatibilidade com a camada de view.
 */
public class PropertyStatusDAO {

    private static final String COLLECTION_NAME = "properties";
    private static final String FIELD = "status";

    /**
     * Obtém a coleção MongoDB de imóveis (onde status está embarcado).
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Retorna todos os status de imóvel distintos encontrados na coleção properties,
     * ordenados alfabeticamente.
     *
     * @return Lista ordenada de nomes de status distintos.
     */
    private List<String> getDistinctStatuses() {
        List<String> statuses = new ArrayList<>();
        try {
            for (String val : getCollection().distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    statuses.add(val);
                }
            }
            Collections.sort(statuses);
        } catch (Exception e) {
            System.err.println("Erro ao listar status de imóvel distintos: " + e.getMessage());
        }
        return statuses;
    }

    /**
     * Insere um novo status de imóvel.
     * Como status são texto embarcado nos imóveis, esta operação apenas
     * exibe uma mensagem informativa. O status será efetivamente registrado
     * ao ser usado em um imóvel.
     *
     * @param psObj Objeto contendo os dados do status.
     */
    public void insert(Property_Status psObj) {
        System.out.println("[INFO] No MongoDB, status de imóvel são texto embarcado no cadastro do imóvel.");
        System.out.println("O status '" + psObj.getNmstatus() + "' será registrado ao ser usado em um imóvel.");
        List<String> existing = getDistinctStatuses();
        psObj.setCdstatus(existing.size() + 1);
    }

    /**
     * Busca um status de imóvel pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de status distintos.
     *
     * @param id Identificador virtual do status.
     * @return Objeto Property_Status ou null.
     */
    public Property_Status findById(int id) {
        try {
            List<String> statuses = getDistinctStatuses();
            if (id >= 1 && id <= statuses.size()) {
                Property_Status ps = new Property_Status();
                ps.setCdstatus(id);
                ps.setNmstatus(statuses.get(id - 1));
                return ps;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar status de imóvel por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os status de imóvel distintos encontrados na coleção properties.
     *
     * @return Lista de objetos Property_Status com IDs virtuais.
     */
    public List<Property_Status> listAll() {
        List<Property_Status> list = new ArrayList<>();
        try {
            List<String> statuses = getDistinctStatuses();
            for (int i = 0; i < statuses.size(); i++) {
                Property_Status ps = new Property_Status();
                ps.setCdstatus(i + 1);
                ps.setNmstatus(statuses.get(i));
                list.add(ps);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar status de imóvel: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de um status de imóvel em todos os imóveis que o utilizam.
     *
     * @param psObj Objeto contendo o ID virtual e o novo nome do status.
     */
    public void update(Property_Status psObj) {
        try {
            List<String> statuses = getDistinctStatuses();
            int idx = psObj.getCdstatus() - 1;
            if (idx >= 0 && idx < statuses.size()) {
                String oldName = statuses.get(idx);
                String newName = psObj.getNmstatus();

                getCollection().updateMany(
                    Filters.eq(FIELD, oldName),
                    Updates.set(FIELD, newName)
                );

                System.out.println("Status de imóvel atualizado com sucesso. Todos os imóveis com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhum status de imóvel encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status de imóvel: " + e.getMessage());
        }
    }

    /**
     * Exclui um status de imóvel. A exclusão é impedida se existirem imóveis
     * que ainda utilizam o valor.
     *
     * @param id Identificador virtual do status.
     * @return true se o status não estava em uso (removível), false caso contrário.
     */
    public boolean delete(int id) {
        try {
            List<String> statuses = getDistinctStatuses();
            if (id >= 1 && id <= statuses.size()) {
                String name = statuses.get(id - 1);
                long count = getCollection().countDocuments(Filters.eq(FIELD, name));
                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. O status '" + name
                        + "' está em uso em " + count + " imóvel(is).");
                    return false;
                } else {
                    System.out.println("Status '" + name + "' removido (não estava em uso).");
                    return true;
                }
            } else {
                System.out.println("Nenhum status de imóvel encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir status de imóvel: " + e.getMessage());
        }
        return false;
    }
}
