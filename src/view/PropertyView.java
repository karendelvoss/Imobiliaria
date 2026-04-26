package view;

import dao.Conexao;
import dao.PropertyDAO;
import model.Properties;
import dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void listAddresses() {
        String sql = "SELECT cdaddress, nmstreet, nraddress FROM Addresses ORDER BY cdaddress";
        System.out.println("\n--- ENDEREÇOS DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdaddress") + " | " + rs.getString("nmstreet") + ", " + rs.getString("nraddress"));
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar endereços."); }
    }

    private void listPropertyTypes() {
        String sql = "SELECT cdtype, nmtype FROM Property_Types ORDER BY cdtype";
        System.out.println("\n--- TIPOS DE IMÓVEL DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdtype") + " | " + rs.getString("nmtype"));
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar tipos de imóvel."); }
    }

    private void listPropertyPurposes() {
        String sql = "SELECT cdpurpose, nmpurpose FROM Property_Purposes ORDER BY cdpurpose";
        System.out.println("\n--- FINALIDADES DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdpurpose") + " | " + rs.getString("nmpurpose"));
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar finalidades."); }
    }

    private void listPropertyStatus() {
        String sql = "SELECT cdstatus, nmstatus FROM Property_Status ORDER BY cdstatus";
        System.out.println("\n--- STATUS DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdstatus") + " | " + rs.getString("nmstatus"));
            }
        } catch (SQLException e) { System.out.println("Erro ao buscar status."); }
    }
}
