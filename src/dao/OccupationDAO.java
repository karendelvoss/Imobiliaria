package dao;

import model.Occupations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccupationDAO {
    public void insert(Occupations o) {
        String sqlMax = "SELECT COALESCE(MAX(cdoccupation), 0) + 1 FROM Occupations";
        String sql = "INSERT INTO Occupations (cdoccupation, nmoccupation) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
            if (rs.next()) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, rs.getInt(1));
                    ps.setString(2, o.getNmoccupation());
                    ps.executeUpdate();
                    System.out.println("Profissão inserida com sucesso!");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Occupations> listAll() {
        List<Occupations> list = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Occupations")) {
            while (rs.next()) {
                Occupations o = new Occupations();
                o.setCdoccupation(rs.getInt("cdoccupation"));
                o.setNmoccupation(rs.getString("nmoccupation"));
                list.add(o);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Occupations findById(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Occupations WHERE cdoccupation = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Occupations o = new Occupations();
                    o.setCdoccupation(rs.getInt("cdoccupation"));
                    o.setNmoccupation(rs.getString("nmoccupation"));
                    return o;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void update(Occupations o) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("UPDATE Occupations SET nmoccupation = ? WHERE cdoccupation = ?")) {
            ps.setString(1, o.getNmoccupation());
            ps.setInt(2, o.getCdoccupation());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Occupations WHERE cdoccupation = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir: " + e.getMessage());
        }
    }
}