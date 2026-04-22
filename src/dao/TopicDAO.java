package dao;

import model.Topics;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopicDAO {
    public void insert(Topics t) {
        String sqlMax = "SELECT COALESCE(MAX(cdtopic), 0) + 1 FROM Topics";
        String sql = "INSERT INTO Topics (cdtopic, nmtopic) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
            if (rs.next()) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, rs.getInt(1));
                    ps.setString(2, t.getNmtopic());
                    ps.executeUpdate();
                    System.out.println("Tópico inserido com sucesso!");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Topics> listAll() {
        List<Topics> list = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Topics")) {
            while (rs.next()) {
                Topics t = new Topics();
                t.setCdtopic(rs.getInt("cdtopic"));
                t.setNmtopic(rs.getString("nmtopic"));
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Topics findById(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Topics WHERE cdtopic = ?")) {
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

    public void update(Topics t) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("UPDATE Topics SET nmtopic = ? WHERE cdtopic = ?")) {
            ps.setString(1, t.getNmtopic());
            ps.setInt(2, t.getCdtopic());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Topics WHERE cdtopic = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir: " + e.getMessage());
        }
    }
}