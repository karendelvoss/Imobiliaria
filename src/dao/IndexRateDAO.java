package dao;

import model.Index_Rates;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IndexRateDAO {

    /**
     * Insere uma nova taxa de índice no banco de dados.
     */
    public void insertRate(Index_Rates rate) {
        String sql = "INSERT INTO Index_Rates (refmonth, refyear, vlrate, fk_Indexes_cdindex) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rate.getRefmonth());
            ps.setInt(2, rate.getRefyear());
            ps.setDouble(3, rate.getVlrate());
            ps.setInt(4, rate.getFk_Indexes_cdindex());

            ps.executeUpdate();
            System.out.println("Index Rate inserted successfully!");

        } catch (SQLException e) {
            System.err.println("Error inserting index rate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lista todas as taxas cadastradas.
     */
    public List<Index_Rates> listAll() {
        List<Index_Rates> list = new ArrayList<>();
        String sql = "SELECT * FROM Index_Rates ORDER BY refyear DESC, refmonth DESC";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Index_Rates rate = new Index_Rates();
                rate.setCdrate(rs.getInt("cdrate"));
                rate.setRefmonth(rs.getInt("refmonth"));
                rate.setRefyear(rs.getInt("refyear"));
                rate.setVlrate(rs.getDouble("vlrate"));
                rate.setFk_Indexes_cdindex(rs.getInt("fk_Indexes_cdindex"));
                list.add(rate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Busca a taxa mais recente para um índice específico.
     * Útil para calcular o reajuste de contratos (Installments).
     */
    public Index_Rates findLatestById(int cdindex) {
        String sql = "SELECT * FROM Index_Rates WHERE fk_Indexes_cdindex = ? ORDER BY refyear DESC, refmonth DESC LIMIT 1";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cdindex);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Index_Rates rate = new Index_Rates();
                    rate.setCdrate(rs.getInt("cdrate"));
                    rate.setRefmonth(rs.getInt("refmonth"));
                    rate.setRefyear(rs.getInt("refyear"));
                    rate.setVlrate(rs.getDouble("vlrate"));
                    rate.setFk_Indexes_cdindex(rs.getInt("fk_Indexes_cdindex"));
                    return rate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}