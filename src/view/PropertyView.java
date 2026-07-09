package view;

import dao.PropertyDAO;
import dao.PropertyTypeDAO;
import dao.PropertyPurposeDAO;
import dao.PropertyStatusDAO;
import model.Properties;
import dao.UserDAO;

import static view.ConsoleIO.*;

/**
 * Interface de console para gestão de imóveis.
 */
public class PropertyView {

    private final PropertyDAO propertyDAO;
    private final UserDAO userDAO;
    private final UserView userView;

    public PropertyView(PropertyDAO propertyDAO, UserDAO userDAO, UserView userView) {
        this.propertyDAO = propertyDAO;
        this.userDAO = userDAO;
        this.userView = userView;
    }

    /**
     * Menu principal para operações com imóveis.
     */
    public void menu() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- SUBMENU: IMÓVEIS ---");
            System.out.println("1. Cadastrar 2. Listar 3. Consultar 4. Atualizar 5. Excluir 0. Voltar");
            op = lerIntSeguro("Escolha: ");
            switch (op) {
                case 1: cadastrar(); break;
                case 2: listarTodos(); break;
                case 3: consultar(); break;
                case 4: atualizar(); break;
                case 5: excluir(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void listarTodos() {
        java.util.List<String> imoveis = propertyDAO.listAllFormatted();
        if (imoveis.isEmpty()) {
            System.out.println("\nNenhum imóvel cadastrado.");
            if (confirmar("Deseja cadastrar um novo? (s/n): ")) { cadastrar(); }
        } else {
            System.out.println("\n--- LISTA DE IMÓVEIS ---");
            imoveis.forEach(System.out::println);
        }
    }

    /**
     * Fluxo de cadastro de um novo imóvel.
     * 
     * @return ID do imóvel criado ou -1 se cancelado.
     */
    public int cadastrar() {
        System.out.println("\n--- CADASTRAR IMÓVEL ---");
        Properties p = new Properties();
        p.setNrregistration(ler("Matrícula: "));
        p.setDsdescription(ler("Descrição: "));
        p.setVltotalarea(lerDouble("Área Total (m²): "));

        // Endereço embarcado diretamente no imóvel
        System.out.println("\n--- ENDEREÇO DO IMÓVEL ---");
        model.Addresses endereco = UserView.lerEndereco();

        int idType = lerIdValido("ID Tipo", id -> checkExists("Property_Types", "cdtype", id) ? id : null, this::listPropertyTypes);
        if (idType == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdtype(resolveTypeName(idType));

        int idPurpose = lerIdValido("ID Finalidade", id -> checkExists("Property_Purposes", "cdpurpose", id) ? id : null, this::listPropertyPurposes);
        if (idPurpose == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdpurpose(resolvePurposeName(idPurpose));

        int idStatus = lerIdValido("ID Status", id -> checkExists("Property_Status", "cdstatus", id) ? id : null, this::listPropertyStatus);
        if (idStatus == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdstatus(resolveStatusName(idStatus));

        int idProperty = propertyDAO.insertPropertyWithAddress(p, endereco);
        if (idProperty > 0) {
            vincularProprietarioObrigatorio(idProperty);
        }
        return idProperty;
    }

    private void vincularProprietarioObrigatorio(int idProperty) {
        System.out.println("\n--- VÍNCULO OBRIGATÓRIO DE PROPRIETÁRIO ---");
        System.out.println("Todo imóvel recém-cadastrado deve ter pelo menos um proprietário vinculado.");
        
        int idUser = lerIdValido("ID do Proprietário/Usuário",
            userDAO::findById,
            () -> userDAO.getAllUsersList().forEach(System.out::println),
            () -> userView.cadastrar()
        );

        if (idUser > 0) {
            propertyDAO.linkOwner(idProperty, idUser);
            System.out.println("Proprietário vinculado com sucesso!");
        } else {
            // Se o usuário cancelar com 0, o sistema avisa mas permite continuar (ou podemos travar num loop se for 100% obrigatório)
            System.out.println("AVISO: O imóvel ficou sem proprietário vinculado. Recomenda-se realizar o vínculo manualmente depois.");
        }
    }

    private void consultar() {
        String input = ler("\nDigite o ID do imóvel para consultar (0 para voltar): ").trim();
        if (input.equals("0") || input.isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return;
        }

        Properties p = propertyDAO.findById(id);
        if (p != null) {
            String info = propertyDAO.findByIdDetalhado(id);
            System.out.println(info != null ? info : "Erro ao carregar detalhes.");
        } else {
            System.out.println("Imóvel com ID " + id + " não encontrado.");
        }
    }

    private void atualizar() {
        int id = lerIdValido("ID do imóvel para atualizar",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        Properties p = propertyDAO.findById(id);

        System.out.println("Pressione ENTER para manter o valor atual.");
        p.setNrregistration(lerOuManter("Matrícula", p.getNrregistration()));
        p.setDsdescription(lerOuManter("Descrição", p.getDsdescription()));
        
        // Atualizar endereço embarcado
        if (confirmar("Atualizar endereço? (s/n): ")) {
            model.Addresses endereco = UserView.lerEndereco();
            propertyDAO.updateAddress(id, endereco);
        }

        p.setCdtype(resolveTypeName(lerIdOuManter("ID Tipo", reverseTypeName(p.getCdtype()), idType -> checkExists("Property_Types", "cdtype", idType) ? idType : null, this::listPropertyTypes)));
        p.setCdpurpose(resolvePurposeName(lerIdOuManter("ID Finalidade", reversePurposeName(p.getCdpurpose()), idPurp -> checkExists("Property_Purposes", "cdpurpose", idPurp) ? idPurp : null, this::listPropertyPurposes)));
        p.setCdstatus(resolveStatusName(lerIdOuManter("ID Status", reverseStatusName(p.getCdstatus()), idStat -> checkExists("Property_Status", "cdstatus", idStat) ? idStat : null, this::listPropertyStatus)));

        propertyDAO.updateProperty(p);
        System.out.println("Imóvel atualizado com sucesso!");
    }

    private void excluir() {
        int id = lerIdValido("ID do imóvel para EXCLUIR (0 para cancelar)",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        Properties p = propertyDAO.findById(id);

        // Verifica se tem contrato ativo vinculado
        if (propertyDAO.hasActiveContract(id)) {
            System.out.println("\nAVISO: O imóvel ID " + id + " possui contrato ativo vinculado, exclusão não permitida!");
            if (confirmar("Ver lista de imóveis aptos para exclusão? (s/n): ")) mostrarExcluiveis();
            return;
        }

        // Verifica status (Alugado não pode excluir)
        String status = p.getCdstatus();
        if (status != null && "Alugado".equalsIgnoreCase(status)) {
            System.out.println("\nAVISO: O imóvel ID " + id + " está 'Alugado', exclusão não permitida!");
            if (confirmar("Ver lista de imóveis aptos para exclusão? (s/n): ")) mostrarExcluiveis();
            return;
        }
        
        if (confirmar("Confirmar exclusão de '" + p.getNrregistration() + "'? (s/n): ")) {
            propertyDAO.deleteProperty(id);
            System.out.println("Imóvel removido!");
        }
    }

    private void mostrarExcluiveis() {
        System.out.println("\n--- IMÓVEIS APTOS PARA EXCLUSÃO ---");
        java.util.List<String> aptos = propertyDAO.getAvailableOnly();
        if (aptos.isEmpty()) {
            // Se não tem "Disponível", mostra os que não têm contrato ativo
            java.util.List<model.Properties> todos = propertyDAO.listAll();
            boolean encontrou = false;
            for (model.Properties p : todos) {
                if (!propertyDAO.hasActiveContract(p.getCdproperty()) && !"Alugado".equalsIgnoreCase(p.getCdstatus())) {
                    System.out.println("ID: " + p.getCdproperty() + " | Reg: " + p.getNrregistration() + " | Status: " + p.getCdstatus());
                    encontrou = true;
                }
            }
            if (!encontrou) {
                System.out.println("Nenhum imóvel apto para exclusão (todos possuem contratos ativos ou estão alugados).");
            }
        } else {
            aptos.forEach(System.out::println);
        }
    }

    private boolean checkExists(String tableName, String idColumnName, int id) {
        switch (tableName) {
            case "Property_Types":
                PropertyTypeDAO typeDAO = new PropertyTypeDAO();
                return typeDAO.findById(id) != null;
            case "Property_Purposes":
                PropertyPurposeDAO purposeDAO = new PropertyPurposeDAO();
                return purposeDAO.findById(id) != null;
            case "Property_Status":
                PropertyStatusDAO statusDAO = new PropertyStatusDAO();
                return statusDAO.findById(id) != null;
            default:
                return false;
        }
    }

    private void listPropertyTypes() {
        System.out.println("\n--- TIPOS DE IMÓVEL DISPONÍVEIS ---");
        PropertyTypeDAO typeDAO = new PropertyTypeDAO();
        typeDAO.listAll().forEach(pt ->
            System.out.println("ID: " + pt.getCdtype() + " | " + pt.getNmtype()));
    }

    private void listPropertyPurposes() {
        System.out.println("\n--- FINALIDADES DISPONÍVEIS ---");
        PropertyPurposeDAO purposeDAO = new PropertyPurposeDAO();
        purposeDAO.listAll().forEach(pp ->
            System.out.println("ID: " + pp.getCdpurpose() + " | " + pp.getNmpurpose()));
    }

    private void listPropertyStatus() {
        System.out.println("\n--- STATUS DISPONÍVEIS ---");
        PropertyStatusDAO statusDAO = new PropertyStatusDAO();
        statusDAO.listAll().forEach(ps ->
            System.out.println("ID: " + ps.getCdstatus() + " | " + ps.getNmstatus()));
    }

    // ========== Resolução ID → Texto (NoSQL-nativo) ==========

    private static final java.util.Map<Integer, String> TYPE_MAP = java.util.Map.of(
        1, "Casa", 2, "Apartamento", 3, "Terreno", 4, "Sala Comercial", 5, "Galpão"
    );
    private static final java.util.Map<Integer, String> PURPOSE_MAP = java.util.Map.of(
        1, "Residencial", 2, "Comercial", 3, "Industrial"
    );
    private static final java.util.Map<Integer, String> STATUS_MAP = java.util.Map.of(
        1, "Alugado", 2, "Disponível", 3, "Vendido"
    );

    private String resolveTypeName(int id) { return TYPE_MAP.getOrDefault(id, "Outro"); }
    private String resolvePurposeName(int id) { return PURPOSE_MAP.getOrDefault(id, "Outro"); }
    private String resolveStatusName(int id) { return STATUS_MAP.getOrDefault(id, "Disponível"); }

    private int reverseTypeName(String name) {
        for (var e : TYPE_MAP.entrySet()) if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        return 1;
    }
    private int reversePurposeName(String name) {
        for (var e : PURPOSE_MAP.entrySet()) if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        return 1;
    }
    private int reverseStatusName(String name) {
        for (var e : STATUS_MAP.entrySet()) if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        return 2;
    }
}
