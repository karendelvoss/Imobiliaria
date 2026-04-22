package dao;

import model.Clauses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClauseDAO {
    public void insert(Clauses c) {
        String sql = "INSERT INTO Clauses (dstext, cdtopic) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDstext());
            ps.setInt(2, c.getCdtopic());
            ps.executeUpdate();
            System.out.println("Cláusula inserida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cláusula: " + e.getMessage());
        }
    }

    public Clauses findById(int id) {
        String sql = "SELECT * FROM Clauses WHERE cdclause = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Clauses c = new Clauses();
                    c.setCdclause(rs.getInt("cdclause"));
                    c.setDstext(rs.getString("dstext"));
                    c.setCdtopic(rs.getInt("cdtopic"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Clauses> listAll() {
        List<Clauses> list = new ArrayList<>();
        String sql = "SELECT * FROM Clauses ORDER BY cdclause";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Clauses c = new Clauses();
                c.setCdclause(rs.getInt("cdclause"));
                c.setDstext(rs.getString("dstext"));
                c.setCdtopic(rs.getInt("cdtopic"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Clauses c) {
        String sql = "UPDATE Clauses SET dstext=?, cdtopic=? WHERE cdclause=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDstext());
            ps.setInt(2, c.getCdtopic());
            ps.setInt(3, c.getCdclause());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Clauses WHERE cdclause = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cláusula: " + e.getMessage());
        }
    }
}