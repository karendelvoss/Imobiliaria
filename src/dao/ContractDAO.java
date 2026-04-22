package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

    /**
     * PROCESSO DE NEGÓCIO: Cria um contrato completo (Partes + Parcelas) 
     * e atualiza o status do imóvel em uma única transação.
     */
    public void processFullContract(Contracts contract, List<User_Contract> participants, List<Installments> installments, Commissions commission, int novoStatus) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false); // Inicia a transação (ACID)

            int generatedContractId = 0;
            String sqlMax = "SELECT COALESCE(MAX(cdcontract), 0) + 1 AS prox_id FROM Contracts";
            try (Statement st = conn.createStatement();
                 ResultSet rsMax = st.executeQuery(sqlMax)) {
                if (rsMax.next()) generatedContractId = rsMax.getInt("prox_id");
            }

            // 1. Salva o Contrato principal e recupera o ID gerado
            String sqlContract = "INSERT INTO Contracts (cdcontract, dtcreation, dstitle, cdtemplate, cdproperty, cdindex) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtC = conn.prepareStatement(sqlContract)) {
                stmtC.setInt(1, generatedContractId);
                stmtC.setDate(2, Date.valueOf(contract.getDtcreation()));
                stmtC.setString(3, contract.getDstitle());
                stmtC.setInt(4, contract.getCdtemplate() > 0 ? contract.getCdtemplate() : 1);
                stmtC.setInt(5, contract.getCdproperty());
                stmtC.setInt(6, contract.getCdindex());
                stmtC.executeUpdate();
            }

            // 2. Vincula os Participantes (Locatário, Proprietário, etc.)
            String sqlParticipants = "INSERT INTO User_Contract_Contracts_Users_Roles (cdcontract, cduser, cdrole, vlparticipation, fgsignaturestatus) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmtP = conn.prepareStatement(sqlParticipants)) {
                for (User_Contract part : participants) {
                    stmtP.setInt(1, generatedContractId);
                    stmtP.setInt(2, part.getCduser());
                    stmtP.setInt(3, part.getCdrole());
                    stmtP.setDouble(4, part.getVlparticipation());
                    stmtP.setInt(5, part.getFgsignaturestatus());
                    stmtP.addBatch();
                }
                stmtP.executeBatch();
            }

            // 3. Gera as Parcelas Financeiras
            int generatedInstallmentId = 0;
            String sqlMaxInst = "SELECT COALESCE(MAX(cdinstallment), 0) FROM Installments";
            try (Statement st = conn.createStatement();
                 ResultSet rsMaxInst = st.executeQuery(sqlMaxInst)) {
                if (rsMaxInst.next()) generatedInstallmentId = rsMaxInst.getInt(1);
            }

            String sqlInstallments = "INSERT INTO Installments (cdinstallment, dtdue, vlbase, cdstatus, nrinstallment, fk_contracts_cdcontract) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtI = conn.prepareStatement(sqlInstallments)) {
                for (Installments inst : installments) {
                    generatedInstallmentId++;
                    stmtI.setInt(1, generatedInstallmentId);
                    stmtI.setDate(2, Date.valueOf(inst.getDtdue()));
                    stmtI.setDouble(3, inst.getVlbase());
                    stmtI.setInt(4, inst.getCdstatus());
                    stmtI.setInt(5, inst.getNrinstallment());
                    stmtI.setInt(6, generatedContractId);
                    stmtI.addBatch();
                }
                stmtI.executeBatch();
            }

            // 4. REGRA DE NEGÓCIO: Atualiza o status do imóvel (Alugado/Vendido)
            if (novoStatus > 0) {
                String sqlStatus = "UPDATE Properties SET cdstatus = ? WHERE cdproperty = ?";
                try (PreparedStatement stmtS = conn.prepareStatement(sqlStatus)) {
                    stmtS.setInt(1, novoStatus);
                    stmtS.setInt(2, contract.getCdproperty());
                    stmtS.executeUpdate();
                }
            }

            // 5. Grava Comissão (se houver)
            if (commission != null) {
                String sqlCom = "INSERT INTO Commissions (vlcommission, cdcontract, cduser) VALUES (?, ?, ?)";
                try (PreparedStatement stmtCom = conn.prepareStatement(sqlCom)) {
                    stmtCom.setDouble(1, commission.getVlcommission());
                    stmtCom.setInt(2, generatedContractId);
                    stmtCom.setInt(3, commission.getCduser());
                    stmtCom.executeUpdate();
                }
            }

            conn.commit(); // Efetiva todas as alterações no banco
            System.out.println("Sucesso: Contrato #" + generatedContractId + " registrado e imóvel atualizado!");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Erro no Processo de Contrato (Rollback aplicado): " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Auxilia o usuário no Main a encontrar IDs válidos de contratos.
     */
    public List<String> getActiveContractsList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cdcontract, dstitle FROM contracts ORDER BY cdcontract";
        
        try (Connection conn = Conexao.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdcontract") + " - " + rs.getString("dstitle"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar contratos: " + e.getMessage());
        }
        return list;
    }

    public Contracts findById(int contractId) {
        String sql = "SELECT * FROM Contracts WHERE cdcontract = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contracts c = new Contracts();
                    c.setCdcontract(rs.getInt("cdcontract"));
                    if (rs.getDate("dtcreation") != null) c.setDtcreation(rs.getDate("dtcreation").toLocalDate());
                    c.setDstitle(rs.getString("dstitle"));
                    c.setCdtemplate(rs.getInt("cdtemplate"));
                    c.setCdproperty(rs.getInt("cdproperty"));
                    c.setCdindex(rs.getInt("cdindex"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contrato por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca todos os contratos que possuem um índice de reajuste definido.
     * @return Uma lista de objetos Contracts.
     */
    public List<Contracts> findAllWithAdjustmentIndex() {
        List<Contracts> contracts = new ArrayList<>();
        String sql = "SELECT * FROM Contracts WHERE cdindex IS NOT NULL";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Contracts c = new Contracts();
                c.setCdcontract(rs.getInt("cdcontract"));
                c.setDtcreation(rs.getDate("dtcreation").toLocalDate());
                c.setDstitle(rs.getString("dstitle"));
                c.setCdtemplate(rs.getInt("cdtemplate"));
                c.setCdproperty(rs.getInt("cdproperty"));
                c.setCdindex(rs.getInt("cdindex"));
                contracts.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contratos com índice de reajuste: " + e.getMessage());
            e.printStackTrace();
        }
        return contracts;
    }

    /**
     * RELATÓRIO: Gera o extrato detalhado de parcelas para o console.
     */
   public boolean gerarExtrato(int idContract) {
    String sql = "SELECT c.dstitle, i.nrinstallment, i.dtdue, i.vlbase, idx.nmindex " +
                 "FROM Contracts c " +
                 "JOIN Installments i ON c.cdcontract = i.fk_contracts_cdcontract " +
                 "JOIN Indexes idx ON c.cdindex = idx.cdindex " +
                 "WHERE c.cdcontract = ? ORDER BY i.nrinstallment";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, idContract);
        ResultSet rs = ps.executeQuery();

        boolean temDados = false;
        while (rs.next()) {
            if (!temDados) {
                System.out.println("\n--- EXTRATO FINANCEIRO DO CONTRATO ---");
                System.out.println("Contrato: " + rs.getString("dstitle"));
            }
            temDados = true;
            System.out.printf("Parcela #%d | Vencimento: %s | Valor: R$ %.2f | Reajuste: %s\n",
                    rs.getInt("nrinstallment"),
                    rs.getDate("dtdue"),
                    rs.getDouble("vlbase"),
                    rs.getString("nmindex"));
        }
        
        if (!temDados) {
            System.out.println("\nERRO: Nenhum contrato encontrado com o ID " + idContract);
        }
        
        return temDados; // Retorna true se achou, false se não achou
        
    } catch (SQLException e) {
        System.err.println("Erro ao gerar relatório: " + e.getMessage());
        return false;
    }
    }

    /**
     * Verifica rapidamente se um contrato existe no banco de dados.
     */
    public boolean contractExists(int idContract) {
        String sql = "SELECT 1 FROM Contracts WHERE cdcontract = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idContract);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void updateContract(Contracts c) {
        String sql = "UPDATE Contracts SET dstitle=?, cdtemplate=?, cdindex=? WHERE cdcontract=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDstitle());
            ps.setInt(2, c.getCdtemplate());
            ps.setInt(3, c.getCdindex());
            ps.setInt(4, c.getCdcontract());
            ps.executeUpdate();
            System.out.println("Contrato atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar contrato: " + e.getMessage());
        }
    }

    public boolean deleteContract(int id) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false); // Inicia transação
            // Exclui dependências manualmente para evitar erro de FK Constraints
            conn.createStatement().executeUpdate("DELETE FROM Installments WHERE fk_contracts_cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM User_Contract_Contracts_Users_Roles WHERE cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM Commissions WHERE cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM Notifications WHERE cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM Variables WHERE cdcontract = " + id);
            
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Contracts WHERE cdcontract = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("Erro ao excluir contrato (Rollback aplicado): " + e.getMessage());
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
        }
    }
}