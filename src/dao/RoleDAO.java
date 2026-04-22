package dao;

import model.Roles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public void insert(Roles r) {
        String sqlMax = "SELECT COALESCE(MAX(cdrole), 0) + 1 AS prox_id FROM Roles";
        String sqlInsert = "INSERT INTO Roles (cdrole, nmrole) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
             
            int nextId = rs.next() ? rs.getInt("prox_id") : 1;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setString(2, r.getNmrole());
                ps.executeUpdate();
                System.out.println("Papel inserido com sucesso! (ID: " + nextId + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Roles findById(int id) {
        String sql = "SELECT * FROM Roles WHERE cdrole = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Roles r = new Roles();
                    r.setCdrole(rs.getInt("cdrole"));
                    r.setNmrole(rs.getString("nmrole"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Roles> listAll() {
        List<Roles> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Roles r = new Roles();
                r.setCdrole(rs.getInt("cdrole"));
                r.setNmrole(rs.getString("nmrole"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Roles r) {
        String sql = "UPDATE Roles SET nmrole=? WHERE cdrole=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNmrole());
            ps.setInt(2, r.getCdrole());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Roles WHERE cdrole = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir papel: " + e.getMessage());
            return false;
        }
    }
}