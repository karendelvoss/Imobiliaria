package dao;

import model.States;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StateDAO {
    public void insert(States s) {
        String sqlInsert = "INSERT INTO States (nmstate, sgstate, cdcountry) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNmstate());
            ps.setString(2, s.getSgstate());
            ps.setInt(3, s.getCdcountry());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setCdstate(keys.getInt(1));
            }
            System.out.println("Estado inserido com sucesso! (ID: " + s.getCdstate() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public States findById(int id) {
        String sql = "SELECT * FROM States WHERE cdstate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    States s = new States();
                    s.setCdstate(rs.getInt("cdstate"));
                    s.setNmstate(rs.getString("nmstate"));
                    s.setSgstate(rs.getString("sgstate"));
                    s.setCdcountry(rs.getInt("cdcountry"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<States> listAll() {
        List<States> list = new ArrayList<>();
        String sql = "SELECT * FROM States ORDER BY cdstate";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                States s = new States();
                s.setCdstate(rs.getInt("cdstate"));
                s.setNmstate(rs.getString("nmstate"));
                s.setSgstate(rs.getString("sgstate"));
                s.setCdcountry(rs.getInt("cdcountry"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(States s) {
        String sql = "UPDATE States SET nmstate=?, sgstate=?, cdcountry=? WHERE cdstate=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNmstate());
            ps.setString(2, s.getSgstate());
            ps.setInt(3, s.getCdcountry());
            ps.setInt(4, s.getCdstate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM States WHERE cdstate = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Erro ao excluir estado: " + e.getMessage()); return false; }
    }
}