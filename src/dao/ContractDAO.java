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

            // 1. Salva o Contrato principal e recupera o ID gerado
            String sqlContract = "INSERT INTO Contracts (dtcreation, dstitle, cdtemplate, cdproperty, cdindex) VALUES (?, ?, ?, ?, ?)";
            int generatedContractId = 0;

            try (PreparedStatement stmtC = conn.prepareStatement(sqlContract, Statement.RETURN_GENERATED_KEYS)) {
                stmtC.setDate(1, Date.valueOf(contract.getDtcreation()));
                stmtC.setString(2, contract.getDstitle());
                stmtC.setInt(3, contract.getCdtemplate() > 0 ? contract.getCdtemplate() : 1);
                stmtC.setInt(4, contract.getCdproperty());
                stmtC.setInt(5, contract.getCdindex());
                stmtC.executeUpdate();

                ResultSet rs = stmtC.getGeneratedKeys();
                if (rs.next()) {
                    generatedContractId = rs.getInt(1);
                }
            }

            // 2. Vincula os Participantes (Locatário, Proprietário, etc.)
            String sqlParticipants = "INSERT INTO User_Contract (cdcontract, cduser, cdrole) VALUES (?, ?, ?)";
            try (PreparedStatement stmtP = conn.prepareStatement(sqlParticipants)) {
                for (User_Contract part : participants) {
                    stmtP.setInt(1, generatedContractId);
                    stmtP.setInt(2, part.getCduser());
                    stmtP.setInt(3, part.getCdrole());
                    stmtP.addBatch();
                }
                stmtP.executeBatch();
            }

            // 3. Gera as Parcelas Financeiras
            String sqlInstallments = "INSERT INTO Installments (dtdue, vlbase, cdstatus, nrinstallment, cdcontract) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmtI = conn.prepareStatement(sqlInstallments)) {
                for (Installments inst : installments) {
                    stmtI.setDate(1, Date.valueOf(inst.getDtdue()));
                    stmtI.setDouble(2, inst.getVlbase());
                    stmtI.setInt(3, inst.getCdstatus());
                    stmtI.setInt(4, inst.getNrinstallment());
                    stmtI.setInt(5, generatedContractId);
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

    /**
     * RELATÓRIO: Gera o extrato detalhado de parcelas para o console.
     */
   public boolean gerarExtrato(int idContract) {
    String sql = "SELECT c.dstitle, i.nrinstallment, i.dtdue, i.vlbase, idx.nmindex " +
                 "FROM Contracts c " +
                 "JOIN Installments i ON c.cdcontract = i.cdcontract " +
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
}