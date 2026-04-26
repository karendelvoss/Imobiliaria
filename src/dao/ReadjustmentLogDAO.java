package dao;

import model.ReadjustmentLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Gerencia as operações de persistência para os logs de reajuste financeiro.
 */
public class ReadjustmentLogDAO {

    /**
     * Insere um novo log de reajuste no banco de dados.
     * 
     * @param log Objeto contendo os detalhes do reajuste realizado.
     */
    public void insert(ReadjustmentLog log) {
        String sql = "INSERT INTO Readjustment_Logs (cdcontract, cdinstallment, cdindex, vlold, vlnew, dtreadjustment) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, log.getCdcontract());
            ps.setInt(2, log.getCdinstallment());
            ps.setInt(3, log.getCdindex());
            ps.setDouble(4, log.getVlold());
            ps.setDouble(5, log.getVlnew());
            ps.setDate(6, java.sql.Date.valueOf(log.getDtreadjustment()));
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error inserting readjustment log: " + e.getMessage());
        }
    }
}
