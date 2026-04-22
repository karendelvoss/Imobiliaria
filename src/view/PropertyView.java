package view;

import dao.Conexao;
import dao.PropertyDAO;
import model.Properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static view.ConsoleIO.*;

/** Fluxos de UI para a entidade Imóvel. */
public class PropertyView {

    private final PropertyDAO propertyDAO;

    public PropertyView(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

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

    private void cadastrar() {
        System.out.println("\n--- CADASTRAR IMÓVEL ---");
        Properties p = new Properties();
        p.setNrregistration(ler("Matrícula: "));
        p.setDsdescription(ler("Descrição: "));
        p.setVltotalarea(lerDouble("Área Total (m²): "));

        int idAddress = lerIdValido("ID Endereço (0 para cancelar)", id -> checkExists("Addresses", "cdaddress", id), this::listAddresses);
        if (idAddress == -1) { System.out.println("Cadastro cancelado."); return; }
        p.setCdaddress(idAddress);

        int idType = lerIdValido("ID Tipo (0 para cancelar)", id -> checkExists("Property_Types", "cdtype", id), this::listPropertyTypes);
        if (idType == -1) { System.out.println("Cadastro cancelado."); return; }
        p.setCdtype(idType);

        int idPurpose = lerIdValido("ID Finalidade (0 para cancelar)", id -> checkExists("Property_Purposes", "cdpurpose", id), this::listPropertyPurposes);
        if (idPurpose == -1) { System.out.println("Cadastro cancelado."); return; }
        p.setCdpurpose(idPurpose);

        int idStatus = lerIdValido("ID Status (0 para cancelar)", id -> checkExists("Property_Status", "cdstatus", id), this::listPropertyStatus);
        if (idStatus == -1) { System.out.println("Cadastro cancelado."); return; }
        p.setCdstatus(idStatus);

        propertyDAO.insertProperty(p);
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
        
        p.setCdaddress(lerIdOuManter("ID Endereço", p.getCdaddress(), idAddr -> checkExists("Addresses", "cdaddress", idAddr), this::listAddresses));
        p.setCdtype(lerIdOuManter("ID Tipo", p.getCdtype(), idType -> checkExists("Property_Types", "cdtype", idType), this::listPropertyTypes));
        p.setCdpurpose(lerIdOuManter("ID Finalidade", p.getCdpurpose(), idPurp -> checkExists("Property_Purposes", "cdpurpose", idPurp), this::listPropertyPurposes));
        p.setCdstatus(lerIdOuManter("ID Status", p.getCdstatus(), idStat -> checkExists("Property_Status", "cdstatus", idStat), this::listPropertyStatus));

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
        propertyDAO.getAvailableOnly().forEach(System.out::println);
    }

    // --- MÉTODOS AUXILIARES DE VALIDAÇÃO E LISTAGEM ---

    private boolean checkExists(String tableName, String idColumnName, int id) {
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { return false; }
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
