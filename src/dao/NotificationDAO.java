package dao;

import model.Notifications;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public void insert(Notifications n) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdnotification), 0) + 1 AS prox_id FROM Notifications";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) proxId = rsMax.getInt("prox_id");
        } catch (SQLException e) { e.printStackTrace(); }

        String sql = "INSERT INTO Notifications (cdnotification, dsmessage, dtsend, fgread, cdcontract, cduser) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setString(2, n.getDsmessage());
            ps.setDate(3, n.getDtsend() != null ? Date.valueOf(n.getDtsend()) : null);
            ps.setBoolean(4, n.isFgread());
            ps.setInt(5, n.getCdcontract());
            ps.setInt(6, n.getCduser());
            ps.executeUpdate();
            System.out.println("Notificação inserida com sucesso! (ID: " + proxId + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir notificação: " + e.getMessage());
        }
    }

    public Notifications findById(int id) {
        String sql = "SELECT * FROM Notifications WHERE cdnotification = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Notifications n = new Notifications();
                    n.setCdnotification(rs.getInt("cdnotification"));
                    n.setDsmessage(rs.getString("dsmessage"));
                    if (rs.getDate("dtsend") != null) {
                        n.setDtsend(rs.getDate("dtsend").toLocalDate());
                    }
                    n.setFgread(rs.getBoolean("fgread"));
                    n.setCdcontract(rs.getInt("cdcontract"));
                    n.setCduser(rs.getInt("cduser"));
                    return n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Notifications> listAll() {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications ORDER BY dtsend DESC, cdnotification DESC";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Notifications n = new Notifications();
                n.setCdnotification(rs.getInt("cdnotification"));
                n.setDsmessage(rs.getString("dsmessage"));
                if (rs.getDate("dtsend") != null) {
                    n.setDtsend(rs.getDate("dtsend").toLocalDate());
                }
                n.setFgread(rs.getBoolean("fgread"));
                n.setCdcontract(rs.getInt("cdcontract"));
                n.setCduser(rs.getInt("cduser"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Notifications n) {
        String sql = "UPDATE Notifications SET dsmessage=?, dtsend=?, fgread=?, cdcontract=?, cduser=? WHERE cdnotification=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getDsmessage());
            ps.setDate(2, n.getDtsend() != null ? Date.valueOf(n.getDtsend()) : null);
            ps.setBoolean(3, n.isFgread());
            ps.setInt(4, n.getCdcontract());
            ps.setInt(5, n.getCduser());
            ps.setInt(6, n.getCdnotification());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Notifications WHERE cdnotification = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir notificação: " + e.getMessage());
            return false;
        }
    }
}