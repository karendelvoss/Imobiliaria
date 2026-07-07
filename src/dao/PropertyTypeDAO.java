package dao;

import model.Property_Types;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gerencia as operações de persistência para os tipos de imóvel (ex: Casa, Apartamento).
 *
 * Na modelagem MongoDB, tipos de imóvel são texto embarcado no campo "type"
 * da coleção "properties". Não existe coleção separada para tipos.
 * Os métodos extraem valores distintos do campo type para manter
 * compatibilidade com a camada de view.
 */
public class PropertyTypeDAO {

    private static final String COLLECTION_NAME = "properties";
    private static final String FIELD = "type";

    /**
     * Obtém a coleção MongoDB de imóveis (onde type está embarcado).
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Retorna todos os tipos de imóvel distintos encontrados na coleção properties,
     * ordenados alfabeticamente.
     *
     * @return Lista ordenada de nomes de tipos distintos.
     */
    private List<String> getDistinctTypes() {
        List<String> types = new ArrayList<>();
        try {
            for (String val : getCollection().distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    types.add(val);
                }
            }
            Collections.sort(types);
        } catch (Exception e) {
            System.err.println("Erro ao listar tipos de imóvel distintos: " + e.getMessage());
        }
        return types;
    }

    /**
     * Insere um novo tipo de imóvel.
     * Como tipos são texto embarcado nos imóveis, esta operação apenas
     * exibe uma mensagem informativa. O tipo será efetivamente registrado
     * ao ser usado em um imóvel.
     *
     * @param pt Objeto contendo os dados do tipo de imóvel.
     */
    public void insert(Property_Types pt) {
        System.out.println("[INFO] No MongoDB, tipos de imóvel são texto embarcado no cadastro do imóvel.");
        System.out.println("O tipo '" + pt.getNmtype() + "' será registrado ao ser usado em um imóvel.");
        List<String> existing = getDistinctTypes();
        pt.setCdtype(existing.size() + 1);
    }

    /**
     * Busca um tipo de imóvel pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de tipos distintos.
     *
     * @param id Identificador virtual do tipo.
     * @return Objeto Property_Types ou null.
     */
    public Property_Types findById(int id) {
        try {
            List<String> types = getDistinctTypes();
            if (id >= 1 && id <= types.size()) {
                Property_Types pt = new Property_Types();
                pt.setCdtype(id);
                pt.setNmtype(types.get(id - 1));
                return pt;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tipo de imóvel por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os tipos de imóvel distintos encontrados na coleção properties.
     *
     * @return Lista de objetos Property_Types com IDs virtuais.
     */
    public List<Property_Types> listAll() {
        List<Property_Types> list = new ArrayList<>();
        try {
            List<String> types = getDistinctTypes();
            for (int i = 0; i < types.size(); i++) {
                Property_Types pt = new Property_Types();
                pt.setCdtype(i + 1);
                pt.setNmtype(types.get(i));
                list.add(pt);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar tipos de imóvel: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de um tipo de imóvel em todos os imóveis que o utilizam.
     *
     * @param pt Objeto contendo o ID virtual e o novo nome do tipo.
     */
    public void update(Property_Types pt) {
        try {
            List<String> types = getDistinctTypes();
            int idx = pt.getCdtype() - 1;
            if (idx >= 0 && idx < types.size()) {
                String oldName = types.get(idx);
                String newName = pt.getNmtype();

                getCollection().updateMany(
                    Filters.eq(FIELD, oldName),
                    Updates.set(FIELD, newName)
                );

                System.out.println("Tipo de imóvel atualizado com sucesso. Todos os imóveis com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhum tipo de imóvel encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tipo de imóvel: " + e.getMessage());
        }
    }

    /**
     * Exclui um tipo de imóvel. A exclusão é impedida se existirem imóveis
     * que ainda utilizam o valor.
     *
     * @param id Identificador virtual do tipo.
     * @return true se o tipo não estava em uso (removível), false caso contrário.
     */
    public boolean delete(int id) {
        try {
            List<String> types = getDistinctTypes();
            if (id >= 1 && id <= types.size()) {
                String name = types.get(id - 1);
                long count = getCollection().countDocuments(Filters.eq(FIELD, name));
                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. O tipo '" + name
                        + "' está em uso em " + count + " imóvel(is).");
                    return false;
                } else {
                    System.out.println("Tipo '" + name + "' removido (não estava em uso).");
                    return true;
                }
            } else {
                System.out.println("Nenhum tipo de imóvel encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir tipo de imóvel: " + e.getMessage());
        }
        return false;
    }
}
