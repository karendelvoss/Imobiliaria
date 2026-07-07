package dao;

import model.States;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gerencia as operações de persistência para a entidade de Estados.
 *
 * Na modelagem MongoDB, estados são texto embarcado no campo "address.state"
 * das coleções "users" e "properties". Não existe coleção separada para estados.
 * Os métodos extraem valores distintos do campo address.state para manter
 * compatibilidade com a camada de view.
 */
public class StateDAO {

    private static final String FIELD = "address.state";

    /**
     * Retorna todos os nomes/siglas de estados distintos encontrados nos endereços embarcados,
     * combinando dados de users e properties, ordenados alfabeticamente.
     *
     * @return Lista ordenada de estados distintos.
     */
    private List<String> getDistinctStates() {
        Set<String> states = new LinkedHashSet<>();
        try {
            MongoCollection<Document> users = Conexao.getCollection("users");
            for (String val : users.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    states.add(val);
                }
            }
            MongoCollection<Document> properties = Conexao.getCollection("properties");
            for (String val : properties.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    states.add(val);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar estados distintos: " + e.getMessage());
        }
        List<String> sorted = new ArrayList<>(states);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Insere um novo estado.
     * Como estados são texto embarcado nos endereços, esta operação apenas
     * exibe uma mensagem informativa. O estado será efetivamente registrado
     * ao ser usado em um endereço de usuário ou imóvel.
     *
     * @param s Objeto contendo os dados do estado.
     */
    public void insert(States s) {
        System.out.println("[INFO] No MongoDB, estados são texto embarcado nos endereços de usuários e imóveis.");
        System.out.println("O estado '" + s.getNmstate() + "' será registrado ao ser usado em um endereço.");
        List<String> existing = getDistinctStates();
        s.setCdstate(existing.size() + 1);
    }

    /**
     * Busca um estado pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de estados distintos.
     *
     * @param id Identificador virtual do estado.
     * @return Objeto States ou null.
     */
    public States findById(int id) {
        try {
            List<String> states = getDistinctStates();
            if (id >= 1 && id <= states.size()) {
                States s = new States();
                s.setCdstate(id);
                s.setNmstate(states.get(id - 1));
                s.setSgstate(states.get(id - 1));
                s.setCdcountry(0);
                return s;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar estado por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os estados distintos encontrados nos endereços embarcados.
     *
     * @return Lista de objetos States com IDs virtuais.
     */
    public List<States> listAll() {
        List<States> list = new ArrayList<>();
        try {
            List<String> states = getDistinctStates();
            for (int i = 0; i < states.size(); i++) {
                States s = new States();
                s.setCdstate(i + 1);
                s.setNmstate(states.get(i));
                s.setSgstate(states.get(i));
                s.setCdcountry(0);
                list.add(s);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar estados: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de um estado em todos os endereços embarcados que o referenciam.
     *
     * @param s Objeto contendo o ID virtual e o novo nome do estado.
     */
    public void update(States s) {
        try {
            List<String> states = getDistinctStates();
            int idx = s.getCdstate() - 1;
            if (idx >= 0 && idx < states.size()) {
                String oldName = states.get(idx);
                String newName = s.getNmstate();

                MongoCollection<Document> users = Conexao.getCollection("users");
                users.updateMany(
                    new Document(FIELD, oldName),
                    new Document("$set", new Document(FIELD, newName))
                );

                MongoCollection<Document> properties = Conexao.getCollection("properties");
                properties.updateMany(
                    new Document(FIELD, oldName),
                    new Document("$set", new Document(FIELD, newName))
                );

                System.out.println("Estado atualizado com sucesso. Todos os endereços com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhum estado encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar estado: " + e.getMessage());
        }
    }

    /**
     * Exclui um estado dos endereços embarcados.
     * A exclusão é impedida se existirem endereços que ainda utilizam o valor.
     *
     * @param id Identificador virtual do estado.
     * @return true se a operação foi processada (mesmo que impedida por uso).
     */
    public boolean delete(int id) {
        try {
            List<String> states = getDistinctStates();
            if (id >= 1 && id <= states.size()) {
                String name = states.get(id - 1);
                MongoCollection<Document> users = Conexao.getCollection("users");
                MongoCollection<Document> properties = Conexao.getCollection("properties");

                long count = users.countDocuments(new Document(FIELD, name))
                           + properties.countDocuments(new Document(FIELD, name));

                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. O estado '" + name
                        + "' está em uso em " + count + " endereço(s).");
                } else {
                    System.out.println("Estado '" + name + "' removido (não estava em uso).");
                }
                return true;
            } else {
                System.out.println("Nenhum estado encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir estado: " + e.getMessage());
        }
        return false;
    }
}
