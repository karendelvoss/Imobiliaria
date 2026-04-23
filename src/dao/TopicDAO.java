package dao;

import model.Topics;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopicDAO {
    public void insert(Topics t) {
        String sql = "INSERT INTO Topics (nmtopic) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNmtopic());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) t.setCdtopic(keys.getInt(1));
            }
            System.out.println("Tópico inserido com sucesso! (ID: " + t.getCdtopic() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir tópico: " + e.getMessage());
        }
    }

    public Topics findById(int id) {
        String sql = "SELECT * FROM Topics WHERE cdtopic = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Topics t = new Topics();
                    t.setCdtopic(rs.getInt("cdtopic"));
                    t.setNmtopic(rs.getString("nmtopic"));
                    return t;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Topics> listAll() {
        List<Topics> list = new ArrayList<>();
        String sql = "SELECT * FROM Topics ORDER BY cdtopic";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Topics t = new Topics();
                t.setCdtopic(rs.getInt("cdtopic"));
                t.setNmtopic(rs.getString("nmtopic"));
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void update(Topics t) {
        String sql = "UPDATE Topics SET nmtopic=? WHERE cdtopic=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNmtopic()); ps.setInt(2, t.getCdtopic()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Topics WHERE cdtopic = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Erro ao excluir: " + e.getMessage()); return false; }
    }
}