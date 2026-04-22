package dao;

import model.Property_Status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyStatusDAO {
    public void insert(Property_Status psObj) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdstatus), 0) + 1 AS prox_id FROM Property_Status";
        
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) {
                proxId = rsMax.getInt("prox_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO Property_Status (cdstatus, nmstatus) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setString(2, psObj.getNmstatus());
            ps.executeUpdate();
            System.out.println("Status de imóvel inserido com sucesso! (ID: " + proxId + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir status: " + e.getMessage());
        }
    }

    public Property_Status findById(int id) {
        String sql = "SELECT * FROM Property_Status WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property_Status p = new Property_Status();
                    p.setCdstatus(rs.getInt("cdstatus"));
                    p.setNmstatus(rs.getString("nmstatus"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Property_Status> listAll() {
        List<Property_Status> list = new ArrayList<>();
        String sql = "SELECT * FROM Property_Status ORDER BY cdstatus";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Property_Status p = new Property_Status();
                p.setCdstatus(rs.getInt("cdstatus"));
                p.setNmstatus(rs.getString("nmstatus"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Property_Status psObj) {
        String sql = "UPDATE Property_Status SET nmstatus = ? WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, psObj.getNmstatus());
            ps.setInt(2, psObj.getCdstatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Property_Status WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir status: " + e.getMessage());
            return false;
        }
    }
}