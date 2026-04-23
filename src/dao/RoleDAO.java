package dao;

import model.Roles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public void insert(Roles r) {
        String sqlInsert = "INSERT INTO Roles (nmrole) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNmrole());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setCdrole(keys.getInt(1));
            }
            System.out.println("Papel inserido com sucesso! (ID: " + r.getCdrole() + ")");
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