package dao;

import model.Index_Rates;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as taxas dos índices.
 */
public class IndexRateDAO {

    /**
     * Insere uma nova taxa de índice.
     * 
     * @param rate Objeto contendo os dados da taxa.
     */
    public void insertRate(Index_Rates rate) {
        String sql = "INSERT INTO Index_Rates (refmonth, refyear, vlrate, cdindex) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rate.getRefmonth());
            ps.setInt(2, rate.getRefyear());
            ps.setDouble(3, rate.getVlrate());
            ps.setInt(4, rate.getFk_Indexes_cdindex());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting index rate: " + e.getMessage());
        }
    }

    /**
     * Lista todas as taxas de índices cadastradas.
     * 
     * @return Lista de objetos Index_Rates.
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
                rate.setFk_Indexes_cdindex(rs.getInt("cdindex"));
                list.add(rate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Busca as taxas dos últimos 12 meses anteriores à data base.
     * 
     * @param cdindex Identificador do índice.
     * @param baseDate Data base para o cálculo.
     * @return Lista de objetos Index_Rates.
     */
    public List<Index_Rates> findLast12MonthsRates(int cdindex, java.time.LocalDate baseDate) {
        List<Index_Rates> list = new ArrayList<>();
        java.time.LocalDate limitDate = baseDate.minusMonths(12);
        
        String sql = "SELECT * FROM Index_Rates WHERE cdindex = ? " +
                     "AND (refyear > ? OR (refyear = ? AND refmonth >= ?)) " +
                     "AND (refyear < ? OR (refyear = ? AND refmonth < ?)) " +
                     "ORDER BY refyear ASC, refmonth ASC";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cdindex);
            ps.setInt(2, limitDate.getYear());
            ps.setInt(3, limitDate.getYear());
            ps.setInt(4, limitDate.getMonthValue());
            ps.setInt(5, baseDate.getYear());
            ps.setInt(6, baseDate.getYear());
            ps.setInt(7, baseDate.getMonthValue());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Index_Rates rate = new Index_Rates();
                    rate.setCdrate(rs.getInt("cdrate"));
                    rate.setRefmonth(rs.getInt("refmonth"));
                    rate.setRefyear(rs.getInt("refyear"));
                    rate.setVlrate(rs.getDouble("vlrate"));
                    rate.setFk_Indexes_cdindex(rs.getInt("cdindex"));
                    list.add(rate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Busca a taxa mais recente para um determinado índice.
     * 
     * @param cdindex Identificador do índice.
     * @return Objeto Index_Rates ou null.
     */
    public Index_Rates findLatestById(int cdindex) {
        String sql = "SELECT * FROM Index_Rates WHERE cdindex = ? ORDER BY refyear DESC, refmonth DESC LIMIT 1";
        
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
                    rate.setFk_Indexes_cdindex(rs.getInt("cdindex"));
                    return rate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca uma taxa de índice pelo ID.
     * 
     * @param id Identificador da taxa.
     * @return Objeto Index_Rates ou null.
     */
    public Index_Rates findById(int id) {
        String sql = "SELECT * FROM Index_Rates WHERE cdrate = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Index_Rates rate = new Index_Rates();
                    rate.setCdrate(rs.getInt("cdrate"));
                    rate.setRefmonth(rs.getInt("refmonth"));
                    rate.setRefyear(rs.getInt("refyear"));
                    rate.setVlrate(rs.getDouble("vlrate"));
                    rate.setFk_Indexes_cdindex(rs.getInt("cdindex"));
                    return rate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Atualiza uma taxa de índice existente.
     * 
     * @param rate Objeto contendo os dados atualizados.
     */
    public void update(Index_Rates rate) {
        String sql = "UPDATE Index_Rates SET refmonth=?, refyear=?, vlrate=?, cdindex=? WHERE cdrate=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rate.getRefmonth());
            ps.setInt(2, rate.getRefyear());
            ps.setDouble(3, rate.getVlrate());
            ps.setInt(4, rate.getFk_Indexes_cdindex());
            ps.setInt(5, rate.getCdrate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui uma taxa de índice.
     * 
     * @param id Identificador da taxa.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Index_Rates WHERE cdrate = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}