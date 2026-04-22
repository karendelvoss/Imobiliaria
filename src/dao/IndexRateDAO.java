package dao;

import model.Index_Rates;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IndexRateDAO {

    /**
     * Insere uma nova taxa de índice no banco de dados.
     */
    public void insertRate(Index_Rates rate) {
        String sqlMax = "SELECT COALESCE(MAX(cdrate), 0) + 1 AS prox_id FROM Index_Rates";
        String sql = "INSERT INTO Index_Rates (cdrate, refmonth, refyear, vlrate, fk_indexes_cdindex) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {

            int nextId = rs.next() ? rs.getInt("prox_id") : 1;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nextId);
                ps.setInt(2, rate.getRefmonth());
                ps.setInt(3, rate.getRefyear());
                ps.setDouble(4, rate.getVlrate());
                ps.setInt(5, rate.getCdindex());

                ps.executeUpdate();
                System.out.println("Taxa de índice inserida com sucesso! (ID: " + nextId + ")");
            }
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
                rate.setCdindex(rs.getInt("fk_indexes_cdindex"));
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
        String sql = "SELECT * FROM Index_Rates WHERE fk_indexes_cdindex = ? ORDER BY refyear DESC, refmonth DESC LIMIT 1";
        
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
                    rate.setCdindex(rs.getInt("fk_indexes_cdindex"));
                    return rate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca uma taxa de índice específica pelo ID primário (cdrate).
     */
    public Index_Rates findById(int id) {
        String sql = "SELECT * FROM Index_Rates WHERE cdrate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Index_Rates rate = new Index_Rates();
                    rate.setCdrate(rs.getInt("cdrate"));
                    rate.setRefmonth(rs.getInt("refmonth"));
                    rate.setRefyear(rs.getInt("refyear"));
                    rate.setVlrate(rs.getDouble("vlrate"));
                    rate.setCdindex(rs.getInt("fk_indexes_cdindex"));
                    return rate;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void update(Index_Rates rate) {
        String sql = "UPDATE Index_Rates SET refmonth=?, refyear=?, vlrate=?, fk_indexes_cdindex=? WHERE cdrate=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rate.getRefmonth());
            ps.setInt(2, rate.getRefyear());
            ps.setDouble(3, rate.getVlrate());
            ps.setInt(4, rate.getCdindex());
            ps.setInt(5, rate.getCdrate());
            ps.executeUpdate();
            System.out.println("Taxa atualizada com sucesso!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Index_Rates WHERE cdrate=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Taxa excluída com sucesso!");
        } catch (SQLException e) { System.err.println("Erro ao excluir: " + e.getMessage()); }
    }
}