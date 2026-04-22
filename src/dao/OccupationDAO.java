package dao;

import model.Occupations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccupationDAO {

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

    public List<Occupations> listAll() {
        List<Occupations> list = new ArrayList<>();
        String sql = "SELECT * FROM Occupations ORDER BY nmoccupation";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Occupations o = new Occupations();
                o.setCdoccupation(rs.getInt("cdoccupation"));
                o.setNmoccupation(rs.getString("nmoccupation"));
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}