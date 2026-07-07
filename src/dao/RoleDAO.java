package dao;

import model.Roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerencia as operações de persistência para os papéis de usuário (Roles).
 *
 * Na modelagem MongoDB, papéis são texto embarcado no campo "nmrole" dentro
 * do array "participants" da coleção "contracts". Como papéis são um conjunto
 * pequeno e fixo, utiliza-se um mapa estático em memória para manter a
 * listagem disponível e compatibilidade com a camada de view.
 *
 * Papéis originais (do insert.sql):
 *   1 - Locatário
 *   2 - Locador
 *   3 - Testemunha
 *   4 - Representante Legal
 */
public class RoleDAO {

    /** Mapa estático de papéis disponíveis (id → nome). */
    private static final Map<Integer, String> ROLES = new LinkedHashMap<>();

    /** Próximo ID disponível para inserção de novos papéis. */
    private static int nextId;

    static {
        ROLES.put(1, "Locatário");
        ROLES.put(2, "Locador");
        ROLES.put(3, "Testemunha");
        ROLES.put(4, "Representante Legal");
        nextId = 5;
    }

    /**
     * Insere um novo papel na listagem de papéis disponíveis.
     * Como papéis são texto embarcado nos contratos, esta operação mantém
     * o papel disponível para seleção na interface.
     *
     * @param r Objeto contendo os dados do papel.
     */
    public void insert(Roles r) {
        r.setCdrole(nextId);
        ROLES.put(nextId, r.getNmrole());
        nextId++;
        System.out.println("[INFO] Papel '" + r.getNmrole() + "' registrado com ID " + r.getCdrole() + ".");
        System.out.println("No MongoDB, o nome do papel é embarcado diretamente nos participantes do contrato.");
    }

    /**
     * Busca um papel pelo seu identificador.
     *
     * @param id Identificador do papel.
     * @return Objeto Roles ou null se não encontrado.
     */
    public Roles findById(int id) {
        String name = ROLES.get(id);
        if (name != null) {
            Roles r = new Roles();
            r.setCdrole(id);
            r.setNmrole(name);
            return r;
        }
        return null;
    }

    /**
     * Lista todos os papéis disponíveis.
     *
     * @return Lista de objetos Roles.
     */
    public List<Roles> listAll() {
        List<Roles> list = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : ROLES.entrySet()) {
            Roles r = new Roles();
            r.setCdrole(entry.getKey());
            r.setNmrole(entry.getValue());
            list.add(r);
        }
        return list;
    }

    /**
     * Atualiza o nome de um papel existente.
     * Nota: papéis já embarcados em contratos existentes não são atualizados
     * automaticamente. Use ContractDAO para atualizar contratos específicos se necessário.
     *
     * @param r Objeto contendo o ID e o novo nome do papel.
     */
    public void update(Roles r) {
        if (ROLES.containsKey(r.getCdrole())) {
            String oldName = ROLES.get(r.getCdrole());
            ROLES.put(r.getCdrole(), r.getNmrole());
            System.out.println("Papel atualizado: '" + oldName + "' → '" + r.getNmrole() + "'.");
            System.out.println("[INFO] Contratos existentes mantêm o nome anterior embarcado.");
        } else {
            System.out.println("Nenhum papel encontrado com o ID " + r.getCdrole() + ".");
        }
    }

    /**
     * Exclui um papel da listagem de papéis disponíveis.
     * Papéis já embarcados em contratos existentes não são afetados.
     *
     * @param id Identificador do papel.
     * @return true se removido com sucesso, false caso contrário.
     */
    public boolean delete(int id) {
        if (ROLES.containsKey(id)) {
            String name = ROLES.remove(id);
            System.out.println("Papel '" + name + "' removido da listagem de papéis disponíveis.");
            System.out.println("[INFO] Contratos existentes que usam este papel não são afetados.");
            return true;
        } else {
            System.out.println("Nenhum papel encontrado com o ID " + id + ".");
            return false;
        }
    }

    /**
     * Retorna o nome do papel pelo ID. Útil para montagem do campo "nmrole"
     * ao inserir participantes em contratos.
     *
     * @param id Identificador do papel.
     * @return Nome do papel ou null se não encontrado.
     */
    public String getRoleName(int id) {
        return ROLES.get(id);
    }
}
