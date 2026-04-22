package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateTopicDAO {

    public void link(int idTopic, int idTemplate) {
        String sql = "INSERT INTO Template_Topics_Topics_Contract_Templates (cdtopic, cdtemplate) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTopic);
            ps.setInt(2, idTemplate);
            ps.executeUpdate();
            System.out.println("Tópico vinculado ao Modelo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao vincular tópico: " + e.getMessage());
        }
    }

    public void unlink(int idTopic, int idTemplate) {
        String sql = "DELETE FROM Template_Topics_Topics_Contract_Templates WHERE cdtopic = ? AND cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTopic);
            ps.setInt(2, idTemplate);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Tópico desvinculado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao desvincular tópico: " + e.getMessage());
        }
    }

    public boolean exists(int idTopic, int idTemplate) {
        String sql = "SELECT 1 FROM Template_Topics_Topics_Contract_Templates WHERE cdtopic = ? AND cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTopic);
            ps.setInt(2, idTemplate);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<String> getTopicsByTemplate(int idTemplate) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT t.cdtopic, t.nmtopic FROM topics t JOIN Template_Topics_Topics_Contract_Templates tt ON t.cdtopic = tt.cdtopic WHERE tt.cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add("ID: " + rs.getInt("cdtopic") + " - " + rs.getString("nmtopic"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}