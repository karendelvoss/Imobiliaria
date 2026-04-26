package dao;

import model.Contract_Templates;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os modelos de contrato.
 */
public class ContractTemplateDAO {

    /**
     * Insere um novo modelo de contrato.
     * 
     * @param ct Objeto contendo os dados do modelo.
     */
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

    /**
     * Busca um modelo de contrato pelo ID.
     * 
     * @param id Identificador do modelo.
     * @return Objeto Contract_Templates ou null.
     */
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

    /**
     * Lista todos os modelos de contrato cadastrados.
     * 
     * @return Lista de modelos de contrato.
     */
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

    /**
     * Atualiza um modelo de contrato.
     * 
     * @param ct Objeto contendo os dados atualizados.
     */
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

    /**
     * Exclui um modelo de contrato.
     * 
     * @param id Identificador do modelo.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Contract_Templates WHERE cdtemplate = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir (verifique se o modelo está sendo usado): " + e.getMessage());
            return false;
        }
    }

    /**
     * Vincula um tópico a um modelo de contrato.
     * 
     * @param idTemplate Identificador do modelo.
     * @param idTopic Identificador do tópico.
     */
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