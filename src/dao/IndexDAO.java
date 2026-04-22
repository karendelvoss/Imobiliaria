package dao;

import model.Indexes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IndexDAO {
    public void insert(Indexes idx) {
        String sql = "INSERT INTO Indexes (nmindex) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idx.getNmindex());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Indexes findById(int id) {
        String sql = "SELECT * FROM Indexes WHERE cdindex = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Indexes idx = new Indexes();
                    idx.setCdindex(rs.getInt("cdindex"));
                    idx.setNmindex(rs.getString("nmindex"));
                    return idx;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Indexes> listAll() {
        List<Indexes> list = new ArrayList<>();
        String sql = "SELECT * FROM Indexes";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Indexes idx = new Indexes();
                idx.setCdindex(rs.getInt("cdindex"));
                idx.setNmindex(rs.getString("nmindex"));
                list.add(idx);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Indexes idx) {
        String sql = "UPDATE Indexes SET nmindex=? WHERE cdindex=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idx.getNmindex());
            ps.setInt(2, idx.getCdindex());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Indexes WHERE cdindex = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}