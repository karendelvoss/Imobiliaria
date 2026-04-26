package dao;

import model.Installments;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as parcelas (Installments).
 */
public class InstallmentDAO {

    /**
     * Busca uma parcela específica pelo seu identificador.
     * 
     * @param installmentId Identificador da parcela.
     * @return Objeto Installments ou null.
     */
    public Installments findById(int installmentId) {
        String sql = "SELECT * FROM Installments WHERE cdinstallment = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, installmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstallment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar parcela por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as parcelas associadas a um contrato.
     * 
     * @param contractId Identificador do contrato.
     * @return Lista de objetos Installments.
     */
    public List<Installments> findByContractId(int contractId) {
        List<Installments> installments = new ArrayList<>();
        String sql = "SELECT * FROM Installments WHERE cdcontract = ? ORDER BY nrinstallment";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    installments.add(mapResultSetToInstallment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar parcelas por contrato: " + e.getMessage());
        }
        return installments;
    }

    /**
     * Atualiza os dados de uma parcela existente.
     * 
     * @param installment Objeto contendo os dados atualizados.
     */
    public void update(Installments installment) {
        String sql = "UPDATE Installments SET dtdue = ?, vlbase = ?, vladjusted = ?, cdstatus = ?, dtpayment = ?, vlpenalty = ?, vlinterest = ? WHERE cdinstallment = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(installment.getDtdue()));
            ps.setDouble(2, installment.getVlbase());
            ps.setDouble(3, installment.getVladjusted());
            ps.setInt(4, installment.getCdstatus());
            ps.setDate(5, installment.getDtpayment() != null ? Date.valueOf(installment.getDtpayment()) : null);
            ps.setDouble(6, installment.getVlpenalty());
            ps.setDouble(7, installment.getVlinterest());
            ps.setInt(8, installment.getCdinstallment());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar parcela: " + e.getMessage());
        }
    }

    /**
     * Insere várias parcelas em lote.
     * 
     * @param installments Lista de parcelas a serem inseridas.
     */
    public void insertBatch(List<Installments> installments) {
        String sql = "INSERT INTO Installments (dtdue, vlbase, cdstatus, nrinstallment, cdcontract, vlpenalty, vlinterest) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Installments inst : installments) {
                ps.setDate(1, Date.valueOf(inst.getDtdue()));
                ps.setDouble(2, inst.getVlbase());
                ps.setInt(3, inst.getCdstatus());
                ps.setInt(4, inst.getNrinstallment());
                ps.setInt(5, inst.getFk_Contracts_cdcontract());
                ps.setDouble(6, inst.getVlpenalty());
                ps.setDouble(7, inst.getVlinterest());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir parcelas: " + e.getMessage());
        }
    }

    /**
     * Mapeia um registro do ResultSet para um objeto Installments.
     * 
     * @param rs ResultSet posicionado no registro.
     * @return Objeto Installments preenchido.
     * @throws SQLException Caso ocorra erro no mapeamento.
     */
    private Installments mapResultSetToInstallment(ResultSet rs) throws SQLException {
        Installments inst = new Installments();
        inst.setCdinstallment(rs.getInt("cdinstallment"));
        inst.setDtdue(rs.getDate("dtdue").toLocalDate());
        inst.setVlbase(rs.getDouble("vlbase"));
        inst.setVladjusted(rs.getDouble("vladjusted"));
        inst.setCdstatus(rs.getInt("cdstatus"));
        inst.setDtpayment(rs.getDate("dtpayment") != null ? rs.getDate("dtpayment").toLocalDate() : null);
        inst.setNrinstallment(rs.getInt("nrinstallment"));
        inst.setFk_Contracts_cdcontract(rs.getInt("cdcontract"));
        inst.setVlpenalty(rs.getDouble("vlpenalty"));
        inst.setVlinterest(rs.getDouble("vlinterest"));
        return inst;
    }

    /**
     * Busca a última parcela com status pendente de um contrato.
     * 
     * @param contractId Identificador do contrato.
     * @return Objeto Installments ou null.
     */
    public Installments findLastPendingInstallmentByContractId(int contractId) {
        String sql = "SELECT * FROM Installments " +
                     "WHERE cdcontract = ? AND cdstatus = 1 " +
                     "ORDER BY nrinstallment DESC LIMIT 1";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstallment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar última parcela pendente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as parcelas pendentes de um contrato.
     * 
     * @param contractId Identificador do contrato.
     * @return Lista de parcelas pendentes.
     */
    public List<Installments> findPendingInstallmentsByContractId(int contractId) {
        List<Installments> installments = new ArrayList<>();
        String sql = "SELECT * FROM Installments WHERE cdcontract = ? AND cdstatus = 1 ORDER BY nrinstallment";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    installments.add(mapResultSetToInstallment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar parcelas pendentes: " + e.getMessage());
        }
        return installments;
    }

    /**
     * Busca parcelas por data de vencimento e status.
     * 
     * @param dtdue Data de vencimento.
     * @param cdstatus Código do status.
     * @return Lista de parcelas encontradas.
     */
    public List<Installments> findByDueDateAndStatus(java.time.LocalDate dtdue, int cdstatus) {
        List<Installments> result = new ArrayList<>();
        String sql = "SELECT * FROM Installments WHERE dtdue = ? AND cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dtdue));
            ps.setInt(2, cdstatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapResultSetToInstallment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar parcelas por vencimento: " + e.getMessage());
        }
        return result;
    }

    /**
     * Busca parcelas que foram pagas em uma determinada data.
     * 
     * @param dtpayment Data de pagamento.
     * @return Lista de parcelas pagas.
     */
    public List<Installments> findByPaymentDate(java.time.LocalDate dtpayment) {
        List<Installments> result = new ArrayList<>();
        String sql = "SELECT * FROM Installments WHERE dtpayment = ? AND cdstatus = 2";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dtpayment));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapResultSetToInstallment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar parcelas pagas por data: " + e.getMessage());
        }
        return result;
    }

    /**
     * Gera o relatório de fluxo de caixa mensal para um ano e contrato específicos.
     * 
     * @param year Ano de referência.
     * @param contractId Identificador do contrato (0 para todos).
     * @return Lista de DTOs do relatório.
     */
    public List<dto.CashFlowReportDTO> getMonthlyCashFlowReport(int year, int contractId) {
        List<dto.CashFlowReportDTO> report = new ArrayList<>();
        String sql = "SELECT " +
                     "  EXTRACT(MONTH FROM dtdue) AS mes, " +
                     "  EXTRACT(YEAR FROM dtdue) AS ano, " +
                     "  SUM(CASE WHEN cdstatus = 2 THEN COALESCE(vladjusted, vlbase) ELSE 0 END) AS recebido, " +
                     "  SUM(CASE WHEN cdstatus = 1 THEN COALESCE(vladjusted, vlbase) ELSE 0 END) AS pendente, " +
                     "  SUM(CASE WHEN cdstatus = 1 AND dtdue < CURRENT_DATE THEN COALESCE(vladjusted, vlbase) ELSE 0 END) AS em_atraso " +
                     "FROM Installments " +
                     "WHERE EXTRACT(YEAR FROM dtdue) = ? " +
                     (contractId > 0 ? "AND cdcontract = ? " : "") +
                     "GROUP BY ano, mes " +
                     "ORDER BY mes";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, year);
            if (contractId > 0) {
                ps.setInt(2, contractId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dto.CashFlowReportDTO dto = new dto.CashFlowReportDTO();
                    dto.setMes(rs.getInt("mes"));
                    dto.setAno(rs.getInt("ano"));
                    dto.setValorRecebido(rs.getDouble("recebido"));
                    dto.setValorPendente(rs.getDouble("pendente"));
                    dto.setValorEmAtraso(rs.getDouble("em_atraso"));
                    report.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório de fluxo de caixa: " + e.getMessage());
        }
        return report;
    }
}