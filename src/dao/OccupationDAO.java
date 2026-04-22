package dao;

import model.Occupations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccupationDAO {

    public void save(Occupations occ) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdoccupation), 0) + 1 AS prox_id FROM Occupations";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) proxId = rsMax.getInt("prox_id");
        } catch (SQLException e) { e.printStackTrace(); }

        String sql = "INSERT INTO Occupations (cdoccupation, nmoccupation) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setString(2, occ.getNmoccupation());
            ps.executeUpdate();
            System.out.println("Profissão '" + occ.getNmoccupation() + "' cadastrada com sucesso com o ID: " + proxId);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar profissão: " + e.getMessage());
        }
    }

    public Occupations findById(int id) {
        String sql = "SELECT * FROM Occupations WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Occupations o = new Occupations();
                    o.setCdoccupation(rs.getInt("cdoccupation"));
                    o.setNmoccupation(rs.getString("nmoccupation"));
                    return o;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM Occupations ORDER BY nmoccupation";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdoccupation") + " | Nome: " + rs.getString("nmoccupation"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar profissões: " + e.getMessage());
        }
        return list;
    }

    public void update(Occupations occ) {
        String sql = "UPDATE Occupations SET nmoccupation = ? WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, occ.getNmoccupation());
            ps.setInt(2, occ.getCdoccupation());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Profissão atualizada com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar profissão: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Occupations WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Profissão excluída com sucesso.");
            } else {
                System.out.println("Nenhuma profissão encontrada com o ID informado.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("violates foreign key constraint")) {
                System.err.println("ERRO: Impossível excluir. Esta profissão está em uso por um ou mais usuários.");
            } else {
                System.err.println("Erro ao excluir profissão: " + e.getMessage());
            }
        }
    }
}