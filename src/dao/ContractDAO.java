package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência e processos de negócio de contratos.
 */
public class ContractDAO {

    /**
     * Realiza o registro completo de um contrato, incluindo participantes, parcelas e comissões.
     * Atualiza o status do imóvel vinculado em uma única transação.
     * 
     * @param contract Objeto contendo os dados do contrato.
     * @param participants Lista de participantes e seus papéis.
     * @param installments Lista de parcelas financeiras.
     * @param novoStatus Novo código de status para o imóvel.
     */
    public void processFullContract(Contracts contract, List<User_Contract> participants, List<Installments> installments, int novoStatus) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);

            int generatedContractId = 0;

            String sqlContract = "INSERT INTO Contracts (dtcreation, dstitle, cdtemplate, cdproperty, cdindex, dtlimit, cdstatus, cdnotary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtC = conn.prepareStatement(sqlContract, Statement.RETURN_GENERATED_KEYS)) {
                stmtC.setDate(1, Date.valueOf(contract.getDtcreation()));
                stmtC.setString(2, contract.getDstitle());
                stmtC.setInt(3, contract.getCdtemplate() > 0 ? contract.getCdtemplate() : 1);
                stmtC.setInt(4, contract.getCdproperty());
                if (contract.getCdindex() > 0) {
                    stmtC.setInt(5, contract.getCdindex());
                } else {
                    stmtC.setNull(5, Types.INTEGER);
                }
                if (contract.getDtlimit() != null) {
                    stmtC.setDate(6, Date.valueOf(contract.getDtlimit()));
                } else {
                    stmtC.setNull(6, Types.DATE);
                }
                stmtC.setInt(7, contract.getCdstatus() > 0 ? contract.getCdstatus() : model.ContractStatus.ATIVO.getCode());
                if (contract.getCdnotary() > 0) {
                    stmtC.setInt(8, contract.getCdnotary());
                } else {
                    stmtC.setNull(8, Types.INTEGER);
                }
                stmtC.executeUpdate();
                try (ResultSet keys = stmtC.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedContractId = keys.getInt(1);
                        contract.setCdcontract(generatedContractId);
                    }
                }
            }

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

            if (novoStatus > 0) {
                String sqlStatus = "UPDATE Properties SET cdstatus = ? WHERE cdproperty = ?";
                try (PreparedStatement stmtS = conn.prepareStatement(sqlStatus)) {
                    stmtS.setInt(1, novoStatus);
                    stmtS.setInt(2, contract.getCdproperty());
                    stmtS.executeUpdate();
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
     * Insere um contrato no banco de dados.
     * 
     * @param contract Objeto contendo os dados do contrato.
     * @return O ID gerado para o contrato ou -1 em caso de erro.
     */
    public int insertContract(Contracts contract) {
        String sqlContract = "INSERT INTO Contracts (dtcreation, dstitle, cdtemplate, dtlimit, cdstatus, cdnotary) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmtC = conn.prepareStatement(sqlContract, Statement.RETURN_GENERATED_KEYS)) {
            stmtC.setDate(1, Date.valueOf(contract.getDtcreation()));
            stmtC.setString(2, contract.getDstitle());
            stmtC.setInt(3, contract.getCdtemplate() > 0 ? contract.getCdtemplate() : 1);
            if (contract.getDtlimit() != null) {
                stmtC.setDate(4, Date.valueOf(contract.getDtlimit()));
            } else {
                stmtC.setNull(4, Types.DATE);
            }
            stmtC.setInt(5, contract.getCdstatus() > 0 ? contract.getCdstatus() : model.ContractStatus.ATIVO.getCode());
            if (contract.getCdnotary() > 0) {
                stmtC.setInt(6, contract.getCdnotary());
            } else {
                stmtC.setNull(6, Types.INTEGER);
            }
            stmtC.executeUpdate();
            try (ResultSet keys = stmtC.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir contrato parcial: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Lista todos os contratos ativos (modo geral).
     * 
     * @return Lista de Strings com ID e Título dos contratos.
     */
    public List<String> getActiveContractsList() {
        return getActiveContractsList("geral");
    }

    /**
     * Lista apenas os contratos que possuem pelo menos uma parte vinculada
     * (registro em {@code user_contract}). Útil para relatórios que dependem
     * de participantes — evita listar contratos "fantasma" criados em fluxos
     * abortados.
     *
     * @return Lista formatada "ID: X - Título".
     */
    public List<String> getContractsWithParticipantsList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT c.cdcontract, c.dstitle " +
                     "FROM Contracts c " +
                     "JOIN User_Contract uc ON uc.cdcontract = c.cdcontract " +
                     "ORDER BY c.cdcontract";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdcontract") + " - " + rs.getString("dstitle"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar contratos com partes: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista contratos ativos filtrados por tipo (venda/locação/geral).
     * 
     * @param tipo Tipo de filtro desejado.
     * @return Lista de Strings com ID e Título dos contratos.
     */
    public List<String> getActiveContractsList(String tipo) {
        List<String> list = new ArrayList<>();
        boolean isGeral = tipo == null || "geral".equalsIgnoreCase(tipo);

        StringBuilder sqlBuilder = new StringBuilder("SELECT DISTINCT c.cdcontract, c.dstitle FROM Contracts c ");

        if (!isGeral) {
            sqlBuilder.append("JOIN Properties p ON c.cdproperty = p.cdproperty ")
                      .append("JOIN Property_Status ps ON p.cdstatus = ps.cdstatus ")
                      .append("WHERE ps.nmstatus ILIKE ? OR ps.nmstatus ILIKE ? ");
        }

        sqlBuilder.append("ORDER BY c.cdcontract");
        
        try (Connection conn = Conexao.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            if (!isGeral) {
                if (tipo.equalsIgnoreCase("venda")) {
                    stmt.setString(1, "%vend%");
                    stmt.setString(2, "%venda%");
                } else {
                    stmt.setString(1, "%alugad%");
                    stmt.setString(2, "%loca%");
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add("ID: " + rs.getInt("cdcontract") + " - " + rs.getString("dstitle"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar contratos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca um contrato pelo seu identificador.
     * 
     * @param contractId Identificador do contrato.
     * @return Objeto Contracts ou null.
     */
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
                    if (rs.getDate("dtlimit") != null) c.setDtlimit(rs.getDate("dtlimit").toLocalDate());
                    c.setCdstatus(rs.getInt("cdstatus"));
                    c.setCdnotary(rs.getInt("cdnotary"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contrato por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todos os contratos que possuem índice de reajuste.
     * 
     * @return Lista de objetos Contracts.
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
                if (rs.getDate("dtlimit") != null) c.setDtlimit(rs.getDate("dtlimit").toLocalDate());
                c.setCdstatus(rs.getInt("cdstatus"));
                c.setCdnotary(rs.getInt("cdnotary"));
                contracts.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contratos com índice de reajuste: " + e.getMessage());
        }
        return contracts;
    }

    /**
     * Busca contratos que expiram em um determinado mês e ano.
     * 
     * @param month Mês de expiração.
     * @param year Ano de expiração.
     * @return Lista de contratos expirando.
     */
    public List<Contracts> findExpiringContracts(int month, int year) {
        List<Contracts> contracts = new ArrayList<>();
        String sql = "SELECT * FROM Contracts WHERE cdstatus != 2 AND dtlimit IS NOT NULL " +
                     "AND EXTRACT(MONTH FROM dtlimit) = ? AND EXTRACT(YEAR FROM dtlimit) = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, month);
            ps.setInt(2, year);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contracts c = new Contracts();
                    c.setCdcontract(rs.getInt("cdcontract"));
                    c.setDtcreation(rs.getDate("dtcreation").toLocalDate());
                    c.setDstitle(rs.getString("dstitle"));
                    c.setCdtemplate(rs.getInt("cdtemplate"));
                    c.setCdproperty(rs.getInt("cdproperty"));
                    if (rs.getObject("cdindex") != null) c.setCdindex(rs.getInt("cdindex"));
                    if (rs.getDate("dtlimit") != null) c.setDtlimit(rs.getDate("dtlimit").toLocalDate());
                    c.setCdstatus(rs.getInt("cdstatus"));
                    c.setCdnotary(rs.getInt("cdnotary"));
                    contracts.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contratos expirando: " + e.getMessage());
        }
        return contracts;
    }

    /**
     * Exibe o extrato financeiro do contrato no console.
     * 
     * @param idContract Identificador do contrato.
     * @return true se o contrato foi encontrado e o extrato gerado.
     */
    public boolean gerarExtrato(int idContract) {
        String sql = "SELECT c.dstitle, i.nrinstallment, i.dtdue, i.vlbase, idx.nmindex " +
                     "FROM Contracts c " + 
                     "LEFT JOIN Installments i ON c.cdcontract = i.cdcontract " +
                     "LEFT JOIN Indexes idx ON c.cdindex = idx.cdindex " +
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
            
            return temDados;
            
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um contrato existe.
     * 
     * @param idContract Identificador do contrato.
     * @return true se o contrato existe.
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

    /**
     * Atualiza os dados de um contrato.
     * 
     * @param c Objeto contendo os dados atualizados.
     */
    public void updateContract(Contracts c) {
        String sql = "UPDATE Contracts SET dstitle=?, cdtemplate=?, cdindex=?, dtcreation=?, dtlimit=?, cdproperty=?, cdstatus=?, cdnotary=? WHERE cdcontract=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDstitle());
            ps.setInt(2, c.getCdtemplate());
            if (c.getCdindex() > 0) {
                ps.setInt(3, c.getCdindex());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setDate(4, Date.valueOf(c.getDtcreation()));
            if (c.getDtlimit() != null) {
                ps.setDate(5, Date.valueOf(c.getDtlimit()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            if (c.getCdproperty() > 0) {
                ps.setInt(6, c.getCdproperty());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, c.getCdstatus() > 0 ? c.getCdstatus() : model.ContractStatus.ATIVO.getCode());
            if (c.getCdnotary() > 0) {
                ps.setInt(8, c.getCdnotary());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.setInt(9, c.getCdcontract());
            ps.executeUpdate();
            System.out.println("Contrato atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar contrato: " + e.getMessage());
        }
    }

    /**
     * Exclui um contrato e todas as suas dependências em uma única transação.
     * 
     * @param id Identificador do contrato.
     * @return true se a exclusão foi bem-sucedida.
     */
    public boolean deleteContract(int id) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate("DELETE FROM Installments WHERE cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM User_Contract WHERE cdcontract = " + id);
            conn.createStatement().executeUpdate("DELETE FROM Notifications WHERE cdcontract = " + id);
            
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

    /**
     * Lista todos os contratos cadastrados no sistema.
     * 
     * @return Lista de objetos Contracts.
     */
    public java.util.List<Contracts> findAllActive() {
        java.util.List<Contracts> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Contracts";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Contracts c = new Contracts();
                c.setCdcontract(rs.getInt("cdcontract"));
                if (rs.getDate("dtcreation") != null) c.setDtcreation(rs.getDate("dtcreation").toLocalDate());
                c.setDstitle(rs.getString("dstitle"));
                c.setCdtemplate(rs.getInt("cdtemplate"));
                c.setCdproperty(rs.getInt("cdproperty"));
                c.setCdindex(rs.getInt("cdindex"));
                if (rs.getDate("dtlimit") != null) c.setDtlimit(rs.getDate("dtlimit").toLocalDate());
                c.setCdstatus(rs.getInt("cdstatus"));
                c.setCdnotary(rs.getInt("cdnotary"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contratos ativos: " + e.getMessage());
        }
        return list;
    }
}