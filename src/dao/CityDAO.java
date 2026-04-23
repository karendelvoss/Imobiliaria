package dao;

import model.Cities;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {
    public void insert(Cities c) {
        String sqlInsert = "INSERT INTO Cities (nmcity, cdstate) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNmcity());
            ps.setInt(2, c.getCdstate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setCdcity(keys.getInt(1));
            }
            System.out.println("Cidade inserida com sucesso! (ID: " + c.getCdcity() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cities findById(int id) {
        String sql = "SELECT * FROM Cities WHERE cdcity = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cities c = new Cities();
                    c.setCdcity(rs.getInt("cdcity"));
                    c.setNmcity(rs.getString("nmcity"));
                    c.setCdstate(rs.getInt("cdstate"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cities> listAll() {
        List<Cities> list = new ArrayList<>();
        String sql = "SELECT * FROM Cities";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cities c = new Cities();
                c.setCdcity(rs.getInt("cdcity"));
                c.setNmcity(rs.getString("nmcity"));
                c.setCdstate(rs.getInt("cdstate"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Cities c) {
        String sql = "UPDATE Cities SET nmcity=?, cdstate=? WHERE cdcity=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNmcity());
            ps.setInt(2, c.getCdstate());
            ps.setInt(3, c.getCdcity());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Cities WHERE cdcity = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}