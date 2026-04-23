package dao;

import model.Contract_Templates;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractTemplateDAO {
    public void insert(Contract_Templates ct) {
        String sql = "INSERT INTO Contract_Templates (nmtemplate, dsversion, fgactive) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ct.getNmtemplate());
            ps.setString(2, ct.getDsversion());
            ps.setBoolean(3, ct.isFgactive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ct.setCdtemplate(keys.getInt(1));
            }
            System.out.println("Modelo de contrato inserido com sucesso! (ID: " + ct.getCdtemplate() + ")");
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
                    ct.setFgactive(rs.getBoolean("fgactive"));
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
                ct.setFgactive(rs.getBoolean("fgactive"));
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
            ps.setBoolean(3, ct.isFgactive());
            ps.setInt(4, ct.getCdtemplate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Contract_Templates WHERE cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir (verifique se o modelo está sendo usado por cláusulas/tópicos): " + e.getMessage());
            return false;
        }
    }

    public void linkTopic(int idTemplate, int idTopic) {
        String sql = "INSERT INTO Template_Topics (cdtemplate, cdtopic) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTemplate);
            ps.setInt(2, idTopic);
            ps.executeUpdate();
            System.out.println("Tópico vinculado ao modelo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao vincular tópico (pode já estar vinculado): " + e.getMessage());
        }
    }
}