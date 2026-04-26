package dao;

import model.Topics;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os tópicos de contratos.
 */
public class TopicDAO {

    /**
     * Insere um novo tópico no banco de dados.
     * 
     * @param t Objeto contendo os dados do tópico.
     */
    public void insert(Topics t) {
        String sql = "INSERT INTO Topics (nmtopic, nrorder) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNmtopic());
            ps.setInt(2, t.getNrorder());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) t.setCdtopic(keys.getInt(1));
            }
            System.out.println("Tópico inserido com sucesso! (ID: " + t.getCdtopic() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir tópico: " + e.getMessage());
        }
    }

    /**
     * Busca um tópico pelo seu identificador.
     * 
     * @param id Identificador do tópico.
     * @return Objeto Topics ou null.
     */
    public Topics findById(int id) {
        String sql = "SELECT * FROM Topics WHERE cdtopic = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Topics t = new Topics();
                    t.setCdtopic(rs.getInt("cdtopic"));
                    t.setNmtopic(rs.getString("nmtopic"));
                    t.setNrorder(rs.getInt("nrorder"));
                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os tópicos cadastrados, ordenados pela ordem definida.
     * 
     * @return Lista de objetos Topics.
     */
    public List<Topics> listAll() {
        List<Topics> list = new ArrayList<>();
        String sql = "SELECT * FROM Topics ORDER BY nrorder, cdtopic";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Topics t = new Topics();
                t.setCdtopic(rs.getInt("cdtopic"));
                t.setNmtopic(rs.getString("nmtopic"));
                t.setNrorder(rs.getInt("nrorder"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza os dados de um tópico existente.
     * 
     * @param t Objeto contendo os dados atualizados do tópico.
     */
    public void update(Topics t) {
        String sql = "UPDATE Topics SET nmtopic=?, nrorder=? WHERE cdtopic=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNmtopic()); 
            ps.setInt(2, t.getNrorder());
            ps.setInt(3, t.getCdtopic()); 
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui um tópico pelo seu identificador.
     * 
     * @param id Identificador do tópico.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Topics WHERE cdtopic = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir tópico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca os tópicos vinculados a um modelo de contrato (Template).
     * 
     * @param templateId Identificador do modelo de contrato.
     * @return Lista de tópicos vinculados.
     */
    public List<Topics> findByTemplateId(int templateId) {
        List<Topics> list = new ArrayList<>();
        String sql = "SELECT t.* FROM Topics t INNER JOIN Template_Topics tt ON t.cdtopic = tt.cdtopic WHERE tt.cdtemplate = ? ORDER BY t.nrorder";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Topics t = new Topics();
                    t.setCdtopic(rs.getInt("cdtopic"));
                    t.setNmtopic(rs.getString("nmtopic"));
                    t.setNrorder(rs.getInt("nrorder"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}