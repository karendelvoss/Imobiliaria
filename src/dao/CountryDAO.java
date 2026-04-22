package dao;

import model.Countries;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {
    public void insert(Countries c) {
        // 1. Busca o maior ID no banco e soma 1
        String sqlMax = "SELECT COALESCE(MAX(cdcountry), 0) + 1 AS prox_id FROM Countries";
        String sqlInsert = "INSERT INTO Countries (cdcountry, nmcountry, sgcountry) VALUES (?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
             
            int nextId = rs.next() ? rs.getInt("prox_id") : 1;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setString(2, c.getNmcountry());
                ps.setString(3, c.getSgcountry());
                ps.executeUpdate();
                System.out.println("País inserido com sucesso! (ID: " + nextId + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Countries findById(int id) {
        String sql = "SELECT * FROM Countries WHERE cdcountry = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Countries c = new Countries();
                    c.setCdcountry(rs.getInt("cdcountry"));
                    c.setNmcountry(rs.getString("nmcountry"));
                    c.setSgcountry(rs.getString("sgcountry"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Countries> listAll() {
        List<Countries> list = new ArrayList<>();
        String sql = "SELECT * FROM Countries";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Countries c = new Countries();
                c.setCdcountry(rs.getInt("cdcountry"));
                c.setNmcountry(rs.getString("nmcountry"));
                c.setSgcountry(rs.getString("sgcountry"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Countries c) {
        String sql = "UPDATE Countries SET nmcountry=?, sgcountry=? WHERE cdcountry=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNmcountry());
            ps.setString(2, c.getSgcountry());
            ps.setInt(3, c.getCdcountry());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Countries WHERE cdcountry = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}