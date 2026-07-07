package dao;

import model.Cities;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gerencia as operações de persistência para a entidade de Cidades.
 *
 * Na modelagem MongoDB, cidades são texto embarcado no campo "address.city"
 * das coleções "users" e "properties". Não existe coleção separada para cidades.
 * Os métodos extraem valores distintos do campo address.city para manter
 * compatibilidade com a camada de view.
 */
public class CityDAO {

    private static final String FIELD = "address.city";

    /**
     * Retorna todos os nomes de cidades distintas encontradas nos endereços embarcados,
     * combinando dados de users e properties, ordenados alfabeticamente.
     *
     * @return Lista ordenada de nomes de cidades distintas.
     */
    private List<String> getDistinctCities() {
        Set<String> cities = new LinkedHashSet<>();
        try {
            MongoCollection<Document> users = Conexao.getCollection("users");
            for (String val : users.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    cities.add(val);
                }
            }
            MongoCollection<Document> properties = Conexao.getCollection("properties");
            for (String val : properties.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    cities.add(val);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar cidades distintas: " + e.getMessage());
        }
        List<String> sorted = new ArrayList<>(cities);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Insere uma nova cidade.
     * Como cidades são texto embarcado nos endereços, esta operação apenas
     * exibe uma mensagem informativa. A cidade será efetivamente registrada
     * ao ser usada em um endereço de usuário ou imóvel.
     *
     * @param c Objeto contendo os dados da cidade.
     */
    public void insert(Cities c) {
        System.out.println("[INFO] No MongoDB, cidades são texto embarcado nos endereços de usuários e imóveis.");
        System.out.println("A cidade '" + c.getNmcity() + "' será registrada ao ser usada em um endereço.");
        List<String> existing = getDistinctCities();
        c.setCdcity(existing.size() + 1);
    }

    /**
     * Busca uma cidade pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de cidades distintas.
     *
     * @param id Identificador virtual da cidade.
     * @return Objeto Cities ou null.
     */
    public Cities findById(int id) {
        try {
            List<String> cities = getDistinctCities();
            if (id >= 1 && id <= cities.size()) {
                Cities c = new Cities();
                c.setCdcity(id);
                c.setNmcity(cities.get(id - 1));
                c.setCdstate(0);
                return c;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cidade por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas as cidades distintas encontradas nos endereços embarcados.
     *
     * @return Lista de objetos Cities com IDs virtuais.
     */
    public List<Cities> listAll() {
        List<Cities> list = new ArrayList<>();
        try {
            List<String> cities = getDistinctCities();
            for (int i = 0; i < cities.size(); i++) {
                Cities c = new Cities();
                c.setCdcity(i + 1);
                c.setNmcity(cities.get(i));
                c.setCdstate(0);
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar cidades: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de uma cidade em todos os endereços embarcados que a referenciam.
     *
     * @param c Objeto contendo o ID virtual e o novo nome da cidade.
     */
    public void update(Cities c) {
        try {
            List<String> cities = getDistinctCities();
            int idx = c.getCdcity() - 1;
            if (idx >= 0 && idx < cities.size()) {
                String oldName = cities.get(idx);
                String newName = c.getNmcity();

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

                System.out.println("Cidade atualizada com sucesso. Todos os endereços com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhuma cidade encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cidade: " + e.getMessage());
        }
    }

    /**
     * Exclui uma cidade dos endereços embarcados.
     * A exclusão é impedida se existirem endereços que ainda utilizam o valor.
     *
     * @param id Identificador virtual da cidade.
     */
    public void delete(int id) {
        try {
            List<String> cities = getDistinctCities();
            if (id >= 1 && id <= cities.size()) {
                String name = cities.get(id - 1);
                MongoCollection<Document> users = Conexao.getCollection("users");
                MongoCollection<Document> properties = Conexao.getCollection("properties");

                long count = users.countDocuments(new Document(FIELD, name))
                           + properties.countDocuments(new Document(FIELD, name));

                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. A cidade '" + name
                        + "' está em uso em " + count + " endereço(s).");
                } else {
                    System.out.println("Cidade '" + name + "' removida (não estava em uso).");
                }
            } else {
                System.out.println("Nenhuma cidade encontrada com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir cidade: " + e.getMessage());
        }
    }
}
