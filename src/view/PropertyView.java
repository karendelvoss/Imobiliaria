package view;

import dao.AddressDAO;
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
        System.out.println("\n--- SUBMENU: IMÓVEIS ---");
        System.out.println("1. Cadastrar 2. Consultar 3. Atualizar 4. Excluir");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: cadastrar(); break;
            case 2: consultar(); break;
            case 3: atualizar(); break;
            case 4: excluir(); break;
            default: System.out.println("Opção inválida.");
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

        int idAddress = lerIdValido("ID Endereço", id -> checkExists("Addresses", "cdaddress", id) ? id : null, this::listAddresses);
        if (idAddress == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdaddress(idAddress);

        int idType = lerIdValido("ID Tipo", id -> checkExists("Property_Types", "cdtype", id) ? id : null, this::listPropertyTypes);
        if (idType == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdtype(idType);

        int idPurpose = lerIdValido("ID Finalidade", id -> checkExists("Property_Purposes", "cdpurpose", id) ? id : null, this::listPropertyPurposes);
        if (idPurpose == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdpurpose(idPurpose);

        int idStatus = lerIdValido("ID Status", id -> checkExists("Property_Status", "cdstatus", id) ? id : null, this::listPropertyStatus);
        if (idStatus == -1) { System.out.println("Cadastro cancelado."); return -1; }
        p.setCdstatus(idStatus);

        int idProperty = propertyDAO.insertProperty(p);
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
        int id = lerIdValido("ID do Imóvel para consulta (0 para cancelar)",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        String info = propertyDAO.findByIdDetalhado(id);
        System.out.println(info != null ? info : "Erro ao carregar detalhes do imóvel.");
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
        
        p.setCdaddress(lerIdOuManter("ID Endereço", p.getCdaddress(), idAddr -> checkExists("Addresses", "cdaddress", idAddr) ? idAddr : null, this::listAddresses));
        p.setCdtype(lerIdOuManter("ID Tipo", p.getCdtype(), idType -> checkExists("Property_Types", "cdtype", idType) ? idType : null, this::listPropertyTypes));
        p.setCdpurpose(lerIdOuManter("ID Finalidade", p.getCdpurpose(), idPurp -> checkExists("Property_Purposes", "cdpurpose", idPurp) ? idPurp : null, this::listPropertyPurposes));
        p.setCdstatus(lerIdOuManter("ID Status", p.getCdstatus(), idStat -> checkExists("Property_Status", "cdstatus", idStat) ? idStat : null, this::listPropertyStatus));

        propertyDAO.updateProperty(p);
        System.out.println("Imóvel atualizado com sucesso!");
    }

    private void excluir() {
        int id = lerIdValido("ID do imóvel para EXCLUIR (0 para cancelar)",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        Properties p = propertyDAO.findById(id);

        if (p.getCdstatus() != 2) { // Status 2 = Disponível
            System.out.println("\nAVISO: O imóvel ID " + id + " não está 'Disponível' (pode estar alugado/vendido), exclusão não permitida!");
            if (confirmar("Ver lista de imóveis aptos para exclusão? (s/n): ")) mostrarExcluiveis();
            return;
        }
        
        if (confirmar("Confirmar exclusão de '" + p.getNrregistration() + "'? (s/n): ")) {
            propertyDAO.deleteProperty(id);
            System.out.println("Imóvel removido!");
        }
    }

    private void mostrarExcluiveis() {
        System.out.println("\n--- IMÓVEIS APTOS PARA EXCLUSÃO (DISPONÍVEIS) ---");
        propertyDAO.getAvailableProperties().forEach(System.out::println);
    }

    private boolean checkExists(String tableName, String idColumnName, int id) {
        switch (tableName) {
            case "Addresses":
                AddressDAO addressDAO = new AddressDAO();
                return addressDAO.findById(id) != null;
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

    private void listAddresses() {
        System.out.println("\n--- ENDEREÇOS DISPONÍVEIS ---");
        AddressDAO addressDAO = new AddressDAO();
        addressDAO.listAllFormatted().forEach(System.out::println);
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
}
