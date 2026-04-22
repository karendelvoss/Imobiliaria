package dao;

import model.Contract_Templates;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractTemplateDAO {
    public void insert(Contract_Templates ct) {
        String sql = "INSERT INTO Contract_Templates (nmtemplate, dsversion, fgactive) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getNmtemplate());
            ps.setString(2, ct.getDsversion());
            ps.setString(3, ct.getFgactive());
            ps.executeUpdate();
            System.out.println("Modelo de contrato inserido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir modelo de contrato: " + e.getMessage());
        }
    }

    public Contract_Templates findById(int id) {
        String sql = "SELECT * FROM Contract_Templates WHERE cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract_Templates ct = new Contract_Templates();
                    ct.setCdtemplate(rs.getInt("cdtemplate"));
                    ct.setNmtemplate(rs.getString("nmtemplate"));
                    ct.setDsversion(rs.getString("dsversion"));
                    ct.setFgactive(rs.getString("fgactive"));
                    return ct;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Contract_Templates> listAll() {
        List<Contract_Templates> list = new ArrayList<>();
        String sql = "SELECT * FROM Contract_Templates ORDER BY cdtemplate";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Contract_Templates ct = new Contract_Templates();
                ct.setCdtemplate(rs.getInt("cdtemplate"));
                ct.setNmtemplate(rs.getString("nmtemplate"));
                ct.setDsversion(rs.getString("dsversion"));
                ct.setFgactive(rs.getString("fgactive"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Contract_Templates ct) {
        String sql = "UPDATE Contract_Templates SET nmtemplate=?, dsversion=?, fgactive=? WHERE cdtemplate=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getNmtemplate());
            ps.setString(2, ct.getDsversion());
            ps.setString(3, ct.getFgactive());
            ps.setInt(4, ct.getCdtemplate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Contract_Templates WHERE cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir modelo de contrato: " + e.getMessage());
        }
    }
}