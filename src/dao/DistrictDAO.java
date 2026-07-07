package dao;

import model.Districts;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gerencia as operações de persistência para a entidade de Bairros.
 *
 * Na modelagem MongoDB, bairros são texto embarcado no campo "address.district"
 * das coleções "users" e "properties". Não existe coleção separada para bairros.
 * Os métodos extraem valores distintos do campo address.district para manter
 * compatibilidade com a camada de view.
 */
public class DistrictDAO {

    private static final String FIELD = "address.district";

    /**
     * Retorna todos os nomes de bairros distintos encontrados nos endereços embarcados,
     * combinando dados de users e properties, ordenados alfabeticamente.
     *
     * @return Lista ordenada de nomes de bairros distintos.
     */
    private List<String> getDistinctDistricts() {
        Set<String> districts = new LinkedHashSet<>();
        try {
            MongoCollection<Document> users = Conexao.getCollection("users");
            for (String val : users.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    districts.add(val);
                }
            }
            MongoCollection<Document> properties = Conexao.getCollection("properties");
            for (String val : properties.distinct(FIELD, String.class)) {
                if (val != null && !val.isBlank()) {
                    districts.add(val);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar bairros distintos: " + e.getMessage());
        }
        List<String> sorted = new ArrayList<>(districts);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Insere um novo bairro.
     * Como bairros são texto embarcado nos endereços, esta operação apenas
     * exibe uma mensagem informativa. O bairro será efetivamente registrado
     * ao ser usado em um endereço de usuário ou imóvel.
     *
     * @param d Objeto contendo os dados do bairro.
     */
    public void insert(Districts d) {
        System.out.println("[INFO] No MongoDB, bairros são texto embarcado nos endereços de usuários e imóveis.");
        System.out.println("O bairro '" + d.getNmdistrict() + "' será registrado ao ser usado em um endereço.");
        List<String> existing = getDistinctDistricts();
        d.setCddistrict(existing.size() + 1);
    }

    /**
     * Busca um bairro pelo seu identificador virtual.
     * O ID corresponde à posição (1-based) na lista alfabética de bairros distintos.
     *
     * @param id Identificador virtual do bairro.
     * @return Objeto Districts ou null.
     */
    public Districts findById(int id) {
        try {
            List<String> districts = getDistinctDistricts();
            if (id >= 1 && id <= districts.size()) {
                Districts d = new Districts();
                d.setCddistrict(id);
                d.setNmdistrict(districts.get(id - 1));
                d.setCdcity(0);
                return d;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar bairro por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os bairros distintos encontrados nos endereços embarcados.
     *
     * @return Lista de objetos Districts com IDs virtuais.
     */
    public List<Districts> listAll() {
        List<Districts> list = new ArrayList<>();
        try {
            List<String> districts = getDistinctDistricts();
            for (int i = 0; i < districts.size(); i++) {
                Districts d = new Districts();
                d.setCddistrict(i + 1);
                d.setNmdistrict(districts.get(i));
                d.setCdcity(0);
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar bairros: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de um bairro em todos os endereços embarcados que o referenciam.
     *
     * @param d Objeto contendo o ID virtual e o novo nome do bairro.
     */
    public void update(Districts d) {
        try {
            List<String> districts = getDistinctDistricts();
            int idx = d.getCddistrict() - 1;
            if (idx >= 0 && idx < districts.size()) {
                String oldName = districts.get(idx);
                String newName = d.getNmdistrict();

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

                System.out.println("Bairro atualizado com sucesso. Todos os endereços com '"
                    + oldName + "' foram atualizados para '" + newName + "'.");
            } else {
                System.out.println("Nenhum bairro encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar bairro: " + e.getMessage());
        }
    }

    /**
     * Exclui um bairro dos endereços embarcados.
     * A exclusão é impedida se existirem endereços que ainda utilizam o valor.
     *
     * @param id Identificador virtual do bairro.
     */
    public void delete(int id) {
        try {
            List<String> districts = getDistinctDistricts();
            if (id >= 1 && id <= districts.size()) {
                String name = districts.get(id - 1);
                MongoCollection<Document> users = Conexao.getCollection("users");
                MongoCollection<Document> properties = Conexao.getCollection("properties");

                long count = users.countDocuments(new Document(FIELD, name))
                           + properties.countDocuments(new Document(FIELD, name));

                if (count > 0) {
                    System.err.println("ERRO: Impossível excluir. O bairro '" + name
                        + "' está em uso em " + count + " endereço(s).");
                } else {
                    System.out.println("Bairro '" + name + "' removido (não estava em uso).");
                }
            } else {
                System.out.println("Nenhum bairro encontrado com o ID informado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir bairro: " + e.getMessage());
        }
    }
}
