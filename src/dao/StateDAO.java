package dao;

import model.States;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StateDAO {
    public void insert(States s) {
        String sqlMax = "SELECT COALESCE(MAX(cdstate), 0) + 1 AS prox_id FROM States";
        String sql = "INSERT INTO States (cdstate, nmstate, sgstate, cdcountry) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
            
            int nextId = rs.next() ? rs.getInt("prox_id") : 1;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nextId);
                ps.setString(2, s.getNmstate());
                ps.setString(3, s.getSgstate());
                ps.setInt(4, s.getCdcountry());
                ps.executeUpdate();
                System.out.println("Estado inserido com sucesso! (ID: " + nextId + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<States> listAll() {
        List<States> list = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM States ORDER BY cdstate")) {
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

    public States findById(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM States WHERE cdstate = ?")) {
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

    public void update(States s) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("UPDATE States SET nmstate = ?, sgstate = ?, cdcountry = ? WHERE cdstate = ?")) {
            ps.setString(1, s.getNmstate());
            ps.setString(2, s.getSgstate());
            ps.setInt(3, s.getCdcountry());
            ps.setInt(4, s.getCdstate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM States WHERE cdstate = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir estado: " + e.getMessage());
        }
    }
}