package dao;

import model.Countries;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gerencia as operações de persistência para a entidade de Países.
 *
 * Na modelagem MongoDB, países são texto embarcado no campo "address.country"
 * das coleções "users" e "properties". Não existe coleção separada para países.
 * Os métodos extraem valores distintos do campo address.country para manter
 * compatibilidade com a camada de view.
 */
public class CountryDAO {

    private static final String FIELD = "address.country";

    /**
     * Retorna todos os nomes de países distintos encontrados nos endereços embarcados,
     * combinando dados de users e properties, ordenados alfabeticamente.
     *
     * @return Lista ordenada de nomes de países distintos.
     */
    private List<String> getDistinctCountries() {
        Set<String> countries = new LinkedHashSet<>();
        try {
            MongoCollection<Document> users = Conexao.getCollection("users");
            for (String val : users.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    countries.add(val);
                }
            }
            MongoCollection<Document> properties = Conexao.getCollection("properties");
            for (String val : properties.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    countries.add(val);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar países distintos: " + e.getMessage());
        }
        List<String> sorted = new ArrayList<>(countries);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Insere um novo país.
     * Como países são texto embarcado nos endereços, esta operação apenas
     * exibe uma mensagem informativa. O país será efetivamente registrado
     * ao ser usado em um endereço de usuário ou imóvel.
     *
     * @param c Objeto contendo os dados do país.
     */
    public void insert(Countries c) {
        System.out.println("[INFO] No MongoDB, países são texto embarcado nos endereços de usuários e imóveis.");
        System.out.println("O país '" + c.getNmcountry() + "' será registrado ao ser usado em um endereço.");
        List<String> existing = getDistinctCountries();
        c.setCdcountry(existing.size() + 1);
    }

    /**
     * Busca um país pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de países distintos.
     *
     * @param id Identificador virtual do país.
     * @return Objeto Countries ou null.
     */
    public Countries findById(int id) {
        try {
            List<String> countries = getDistinctCountries();
            if (id >= 1 && id <= countries.size()) {
                Countries c = new Countries();
                c.setCdcountry(id);
                c.setNmcountry(countries.get(id - 1));
                c.setSgcountry("");
                return c;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar país por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os países distintos encontrados nos endereços embarcados.
     *
     * @return Lista de objetos Countries com IDs virtuais.
     */
    public List<Countries> listAll() {
        List<Countries> list = new ArrayList<>();
        try {
            List<String> countries = getDistinctCountries();
            for (int i = 0; i < countries.size(); i++) {
                Countries c = new Countries();
                c.setCdcountry(i + 1);
                c.setNmcountry(countries.get(i));
                c.setSgcountry("");
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar países: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de um país em todos os endereços embarcados que o referenciam.
     *
     * @param c Objeto contendo o ID virtual e o novo nome do país.
     */
    public void update(Countries c) {
        try {
            List<String> countries = getDistinctCountries();
            int idx = c.getCdcountry() - 1;
            if (idx >= 0 && idx < countries.size()) {
                String oldName = countries.get(idx);
                String newName = c.getNmcountry();

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

                System.out.println("País atualizado com sucesso. Todos os endereços com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhum país encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar país: " + e.getMessage());
        }
    }

    /**
     * Exclui um país (limpa o campo country) de todos os endereços que o referenciam.
     * A exclusão é impedida se existirem endereços que ainda utilizam o valor.
     *
     * @param id Identificador virtual do país.
     */
    public void delete(int id) {
        try {
            List<String> countries = getDistinctCountries();
            if (id >= 1 && id <= countries.size()) {
                String name = countries.get(id - 1);
                MongoCollection<Document> users = Conexao.getCollection("users");
                MongoCollection<Document> properties = Conexao.getCollection("properties");

                long count = users.countDocuments(new Document(FIELD, name))
                           + properties.countDocuments(new Document(FIELD, name));

                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. O país '" + name
                        + "' está em uso em " + count + " endereço(s).");
                } else {
                    System.out.println("País '" + name + "' removido (não estava em uso).");
                }
            } else {
                System.out.println("Nenhum país encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir país: " + e.getMessage());
        }
    }
}
