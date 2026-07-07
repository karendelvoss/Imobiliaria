package dao;

import model.Occupations;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gerencia as operações de persistência para Profissões (Occupations).
 *
 * Na modelagem MongoDB, profissões são texto embarcado no campo "occupation"
 * da coleção "users". Não existe coleção separada para profissões.
 * Os métodos extraem valores distintos do campo occupation para manter
 * compatibilidade com a camada de view.
 */
public class OccupationDAO {

    private static final String COLLECTION_NAME = "users";

    /**
     * Obtém a coleção MongoDB de usuários (onde occupation está embarcado).
     *
     * @return MongoCollection de Documents.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Retorna todas as profissões distintas cadastradas nos usuários, ordenadas alfabeticamente.
     *
     * @return Lista ordenada de nomes de profissões distintas.
     */
    private List<String> getDistinctOccupations() {
        List<String> occupations = new ArrayList<>();
        try {
            for (String occ : getCollection().distinct("occupation", String.class)) {
                if (occ != null && !occ.isBlank()) {
                    occupations.add(occ);
                }
            }
            Collections.sort(occupations);
        } catch (Exception e) {
            System.err.println("Erro ao listar profissões distintas: " + e.getMessage());
        }
        return occupations;
    }

    /**
     * Insere/registra uma nova profissão.
     * Como profissões são texto embarcado em usuários, esta operação apenas
     * exibe uma mensagem informativa. A profissão será efetivamente criada
     * quando vinculada a um usuário.
     *
     * @param occ Objeto contendo os dados da profissão.
     */
    public void save(Occupations occ) {
        System.out.println("[INFO] No MongoDB, profissões são texto embarcado no cadastro do usuário.");
        System.out.println("A profissão '" + occ.getNmoccupation() + "' será registrada ao vincular a um usuário.");
        // Atribui um ID virtual baseado na posição na lista de distintas
        List<String> existing = getDistinctOccupations();
        occ.setCdoccupation(existing.size() + 1);
    }

    /**
     * Busca uma profissão pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de profissões distintas.
     *
     * @param id Identificador virtual da profissão.
     * @return Objeto Occupations ou null.
     */
    public Occupations findById(int id) {
        try {
            List<String> occupations = getDistinctOccupations();
            if (id >= 1 && id <= occupations.size()) {
                Occupations occ = new Occupations();
                occ.setCdoccupation(id);
                occ.setNmoccupation(occupations.get(id - 1));
                return occ;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissão por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca uma profissão pelo nome exato.
     *
     * @param name Nome da profissão.
     * @return Objeto Occupations ou null.
     */
    public Occupations findByName(String name) {
        try {
            List<String> occupations = getDistinctOccupations();
            for (int i = 0; i < occupations.size(); i++) {
                if (occupations.get(i).equalsIgnoreCase(name)) {
                    Occupations occ = new Occupations();
                    occ.setCdoccupation(i + 1);
                    occ.setNmoccupation(occupations.get(i));
                    return occ;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissão por nome: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas as profissões distintas cadastradas de forma formatada.
     *
     * @return Lista de Strings contendo ID virtual e Nome das profissões.
     */
    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        try {
            List<String> occupations = getDistinctOccupations();
            for (int i = 0; i < occupations.size(); i++) {
                list.add("ID: " + (i + 1) + " | Nome: " + occupations.get(i));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar profissões: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de uma profissão em todos os usuários que a possuem.
     *
     * @param occ Objeto contendo o ID virtual e o novo nome da profissão.
     */
    public void update(Occupations occ) {
        try {
            List<String> occupations = getDistinctOccupations();
            int idx = occ.getCdoccupation() - 1;
            if (idx >= 0 && idx < occupations.size()) {
                String oldName = occupations.get(idx);
                getCollection().updateMany(
                    Filters.eq("occupation", oldName),
                    Updates.set("occupation", occ.getNmoccupation())
                );
                System.out.println("Profissão atualizada com sucesso. Todos os usuários com '" 
                    + oldName + "' foram atualizados para '" + occ.getNmoccupation() + "'.");
            } else {
                System.out.println("Nenhuma profissão encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar profissão: " + e.getMessage());
        }
    }

    /**
     * Exclui uma profissão (limpa o campo occupation) de todos os usuários que a possuem.
     *
     * @param id Identificador virtual da profissão.
     */
    public void delete(int id) {
        try {
            List<String> occupations = getDistinctOccupations();
            if (id >= 1 && id <= occupations.size()) {
                String name = occupations.get(id - 1);
                long count = getCollection().countDocuments(Filters.eq("occupation", name));
                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. A profissão '" + name 
                        + "' está em uso por " + count + " usuário(s).");
                } else {
                    System.out.println("Profissão '" + name + "' removida (não estava em uso).");
                }
            } else {
                System.out.println("Nenhuma profissão encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir profissão: " + e.getMessage());
        }
    }
}
