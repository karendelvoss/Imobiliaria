package dao;

import model.Districts;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistrictDAO {
    public void insert(Districts d) {
        String sqlMax = "SELECT COALESCE(MAX(cddistrict), 0) + 1 AS prox_id FROM Districts";
        String sqlInsert = "INSERT INTO Districts (cddistrict, nmdistrict, cdcity) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
             
            int nextId = rs.next() ? rs.getInt("prox_id") : 1;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setString(2, d.getNmdistrict());
                ps.setInt(3, d.getCdcity());
                ps.executeUpdate();
                System.out.println("Bairro inserido com sucesso! (ID: " + nextId + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Districts findById(int id) {
        String sql = "SELECT * FROM Districts WHERE cddistrict = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Districts d = new Districts();
                    d.setCddistrict(rs.getInt("cddistrict"));
                    d.setNmdistrict(rs.getString("nmdistrict"));
                    d.setCdcity(rs.getInt("cdcity"));
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Districts> listAll() {
        List<Districts> list = new ArrayList<>();
        String sql = "SELECT * FROM Districts";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Districts d = new Districts();
                d.setCddistrict(rs.getInt("cddistrict"));
                d.setNmdistrict(rs.getString("nmdistrict"));
                d.setCdcity(rs.getInt("cdcity"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Districts d) {
        String sql = "UPDATE Districts SET nmdistrict=?, cdcity=? WHERE cddistrict=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNmdistrict());
            ps.setInt(2, d.getCdcity());
            ps.setInt(3, d.getCddistrict());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Districts WHERE cddistrict = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}