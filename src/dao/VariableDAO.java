package dao;

import model.Variables;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VariableDAO {
    public void insert(Variables v) {
        String sql = "INSERT INTO Variables (nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getNmvariable());
            ps.setString(2, v.getVlvariable());
            ps.setInt(3, v.getTpvariable());
            ps.setBoolean(4, v.isFgtriggeralert());
            ps.setInt(5, v.getCdcontract());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setCdvariable(keys.getInt(1));
            }
            System.out.println("Variável inserida com sucesso! (ID: " + v.getCdvariable() + ")");
        } catch (SQLException e) { System.err.println("Erro ao inserir variável: " + e.getMessage()); }
    }

    public Variables findById(int id) {
        String sql = "SELECT * FROM Variables WHERE cdvariable = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Variables v = new Variables();
                    v.setCdvariable(rs.getInt("cdvariable"));
                    v.setNmvariable(rs.getString("nmvariable"));
                    v.setVlvariable(rs.getString("vlvariable"));
                    v.setTpvariable(rs.getInt("tpvariable"));
                    v.setFgtriggeralert(rs.getBoolean("fgtriggeralert"));
                    v.setCdcontract(rs.getInt("cdcontract"));
                    return v;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Variables> listAll() {
        List<Variables> list = new ArrayList<>();
        String sql = "SELECT * FROM Variables ORDER BY cdvariable";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Variables v = new Variables();
                v.setCdvariable(rs.getInt("cdvariable"));
                v.setNmvariable(rs.getString("nmvariable"));
                v.setVlvariable(rs.getString("vlvariable"));
                v.setTpvariable(rs.getInt("tpvariable"));
                v.setFgtriggeralert(rs.getBoolean("fgtriggeralert"));
                v.setCdcontract(rs.getInt("cdcontract"));
                list.add(v);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void update(Variables v) {
        String sql = "UPDATE Variables SET nmvariable=?, vlvariable=?, tpvariable=?, fgtriggeralert=?, cdcontract=? WHERE cdvariable=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getNmvariable()); ps.setString(2, v.getVlvariable()); ps.setInt(3, v.getTpvariable());
            ps.setBoolean(4, v.isFgtriggeralert()); ps.setInt(5, v.getCdcontract()); ps.setInt(6, v.getCdvariable()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Variables WHERE cdvariable = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; } catch (SQLException e) { return false; }
    }
}