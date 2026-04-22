package dao;

import model.Commissions;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommissionDAO {
    public void insert(Commissions c) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdcommission), 0) + 1 AS prox_id FROM Commissions";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) proxId = rsMax.getInt("prox_id");
        } catch (SQLException e) { e.printStackTrace(); }

        String sql = "INSERT INTO Commissions (cdcommission, vlcommission, dtpayment, cdcontract) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setDouble(2, c.getVlcommission());
            ps.setDate(3, c.getDtpayment() != null ? Date.valueOf(c.getDtpayment()) : null);
            ps.setInt(4, c.getCdcontract());
            ps.executeUpdate();
            System.out.println("Comissão inserida com sucesso! (ID: " + proxId + ")");
        } catch (SQLException e) { System.err.println("Erro ao inserir comissão: " + e.getMessage()); }
    }

    public Commissions findById(int id) {
        String sql = "SELECT * FROM Commissions WHERE cdcommission = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Commissions c = new Commissions();
                    c.setCdcommission(rs.getInt("cdcommission"));
                    c.setVlcommission(rs.getDouble("vlcommission"));
                    if(rs.getDate("dtpayment") != null) c.setDtpayment(rs.getDate("dtpayment").toLocalDate());
                    c.setCdcontract(rs.getInt("cdcontract"));
                    return c;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Commissions> listAll() {
        List<Commissions> list = new ArrayList<>();
        String sql = "SELECT * FROM Commissions ORDER BY cdcommission";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Commissions c = new Commissions();
                c.setCdcommission(rs.getInt("cdcommission"));
                c.setVlcommission(rs.getDouble("vlcommission"));
                if(rs.getDate("dtpayment") != null) c.setDtpayment(rs.getDate("dtpayment").toLocalDate());
                c.setCdcontract(rs.getInt("cdcontract"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void update(Commissions c) {
        String sql = "UPDATE Commissions SET vlcommission=?, dtpayment=?, cdcontract=? WHERE cdcommission=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, c.getVlcommission()); ps.setDate(2, c.getDtpayment() != null ? Date.valueOf(c.getDtpayment()) : null);
            ps.setInt(3, c.getCdcontract()); ps.setInt(4, c.getCdcommission()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Commissions WHERE cdcommission = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; } catch (SQLException e) { return false; }
    }
}