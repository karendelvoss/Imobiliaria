package dao;

import model.Notifications;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public void insert(Notifications n) {
        String sql = "INSERT INTO Notifications (dsmessage, dtsend, fgstatus, tpnotification, cdcontract, cduser) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getDsmessage());
            ps.setDate(2, n.getDtsend() != null ? Date.valueOf(n.getDtsend()) : null);
            ps.setInt(3, n.getFgstatus());
            ps.setInt(4, n.getTpnotification());
            ps.setInt(5, n.getCdcontract());
            ps.setInt(6, n.getCduser());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setCdnotification(keys.getInt(1));
            }
            System.out.println("Notificação inserida com sucesso! (ID: " + n.getCdnotification() + ")");
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
                    n.setFgstatus(rs.getInt("fgstatus"));
                    n.setTpnotification(rs.getInt("tpnotification"));
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
                n.setFgstatus(rs.getInt("fgstatus"));
                n.setTpnotification(rs.getInt("tpnotification"));
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
        String sql = "UPDATE Notifications SET dsmessage=?, dtsend=?, fgstatus=?, tpnotification=?, cdcontract=?, cduser=? WHERE cdnotification=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getDsmessage());
            ps.setDate(2, n.getDtsend() != null ? Date.valueOf(n.getDtsend()) : null);
            ps.setInt(3, n.getFgstatus());
            ps.setInt(4, n.getTpnotification());
            ps.setInt(5, n.getCdcontract());
            ps.setInt(6, n.getCduser());
            ps.setInt(7, n.getCdnotification());
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