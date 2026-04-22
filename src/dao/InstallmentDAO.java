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
 * DAO para gerenciar as operações de persistência da entidade Installments.
 * Centraliza o acesso a dados para parcelas, desacoplando do ContractDAO.
 */
public class InstallmentDAO {

    /**
     * Busca uma parcela específica pelo seu ID.
     * @param installmentId O ID da parcela a ser encontrada.
     * @return um objeto Installments se encontrado, caso contrário null.
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
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca todas as parcelas associadas a um contrato específico.
     * @param contractId O ID do contrato.
     * @return Uma lista de objetos Installments.
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
            e.printStackTrace();
        }
        return installments;
    }

    /**
     * Atualiza os dados de uma parcela no banco de dados.
     * @param installment O objeto Installment com os dados atualizados.
     */
    public void update(Installments installment) {
        String sql = "UPDATE Installments SET dtdue = ?, vlbase = ?, vladjusted = ?, cdstatus = ?, dtpayment = ? WHERE cdinstallment = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(installment.getDtdue()));
            ps.setDouble(2, installment.getVlbase());
            ps.setDouble(3, installment.getVladjusted());
            ps.setInt(4, installment.getCdstatus());
            ps.setDate(5, installment.getDtpayment() != null ? Date.valueOf(installment.getDtpayment()) : null);
            ps.setInt(6, installment.getCdinstallment());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar parcela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para mapear um ResultSet para um objeto Installments.
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
        return inst;
    }

    /**
     * Busca a última parcela pendente de um contrato.
     * @param contractId O ID do contrato.
     * @return A última parcela com status pendente, ou null se não houver.
     */
    public Installments findLastPendingInstallmentByContractId(int contractId) {
        String sql = "SELECT * FROM Installments " +
                     "WHERE cdcontract = ? AND cdstatus = 1 " + // 1 = Pendente
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
            e.printStackTrace();
        }
        return null;
    }
}