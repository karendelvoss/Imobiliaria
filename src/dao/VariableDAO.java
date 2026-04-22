package dao;

import model.Variables;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VariableDAO {
    public void insert(Variables v) {
        String sqlMax = "SELECT COALESCE(MAX(cdvariable), 0) + 1 AS prox_id FROM Variables";
        String sql = "INSERT INTO Variables (cdvariable, nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlMax)) {
            
            int nextId = rs.next() ? rs.getInt("prox_id") : 1;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nextId);
                ps.setString(2, v.getNmvariable());
                ps.setString(3, v.getVlvariable());
                ps.setString(4, v.getTpvariable());
                ps.setBoolean(5, v.isFgtriggeralert());
                ps.setInt(6, v.getCdcontract());
                ps.executeUpdate();
                System.out.println("Variável de contrato inserida com sucesso! (ID: " + nextId + ")");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Variables> listAll() {
        List<Variables> list = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Variables ORDER BY cdvariable")) {
            while (rs.next()) {
                Variables v = new Variables();
                v.setCdvariable(rs.getInt("cdvariable"));
                v.setNmvariable(rs.getString("nmvariable"));
                v.setVlvariable(rs.getString("vlvariable"));
                v.setTpvariable(rs.getString("tpvariable"));
                v.setFgtriggeralert(rs.getBoolean("fgtriggeralert"));
                v.setCdcontract(rs.getInt("cdcontract"));
                list.add(v);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Variables findById(int id) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Variables WHERE cdvariable = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Variables v = new Variables();
                    v.setCdvariable(rs.getInt("cdvariable"));
                    v.setNmvariable(rs.getString("nmvariable"));
                    v.setVlvariable(rs.getString("vlvariable"));
                    v.setTpvariable(rs.getString("tpvariable"));
                    v.setFgtriggeralert(rs.getBoolean("fgtriggeralert"));
                    v.setCdcontract(rs.getInt("cdcontract"));
                    return v;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void update(Variables v) {
        String sql = "UPDATE Variables SET nmvariable=?, vlvariable=?, tpvariable=?, fgtriggeralert=?, cdcontract=? WHERE cdvariable=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getNmvariable()); ps.setString(2, v.getVlvariable()); ps.setString(3, v.getTpvariable());
            ps.setBoolean(4, v.isFgtriggeralert()); ps.setInt(5, v.getCdcontract()); ps.setInt(6, v.getCdvariable());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Variables WHERE cdvariable = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("Erro ao excluir: " + e.getMessage()); }
    }
}