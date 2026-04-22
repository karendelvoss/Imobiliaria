package dao;

import model.Property_Purposes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyPurposeDAO {
    public void insert(Property_Purposes pp) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdpurpose), 0) + 1 AS prox_id FROM Property_Purposes";
        
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) {
                proxId = rsMax.getInt("prox_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO Property_Purposes (cdpurpose, nmpurpose) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setString(2, pp.getNmpurpose());
            ps.executeUpdate();
            System.out.println("Finalidade de imóvel inserida com sucesso! (ID: " + proxId + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir finalidade: " + e.getMessage());
        }
    }

    public Property_Purposes findById(int id) {
        String sql = "SELECT * FROM Property_Purposes WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property_Purposes pp = new Property_Purposes();
                    pp.setCdpurpose(rs.getInt("cdpurpose"));
                    pp.setNmpurpose(rs.getString("nmpurpose"));
                    return pp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Property_Purposes> listAll() {
        List<Property_Purposes> list = new ArrayList<>();
        String sql = "SELECT * FROM Property_Purposes ORDER BY cdpurpose";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Property_Purposes pp = new Property_Purposes();
                pp.setCdpurpose(rs.getInt("cdpurpose"));
                pp.setNmpurpose(rs.getString("nmpurpose"));
                list.add(pp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Property_Purposes pp) {
        String sql = "UPDATE Property_Purposes SET nmpurpose = ? WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pp.getNmpurpose());
            ps.setInt(2, pp.getCdpurpose());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Property_Purposes WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir finalidade: " + e.getMessage());
            return false;
        }
    }
}