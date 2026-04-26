package dao;

import model.Clauses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as cláusulas contratuais.
 */
public class ClauseDAO {

    /**
     * Insere uma nova cláusula no banco de dados.
     * 
     * @param c Objeto contendo os dados da cláusula.
     */
    public void insert(Clauses c) {
        String sql = "INSERT INTO Clauses (dstext, cdtopic, nrorder) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getDstext());
            ps.setInt(2, c.getCdtopic());
            ps.setInt(3, c.getNrorder());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setCdclause(keys.getInt(1));
            }
            System.out.println("Cláusula inserida com sucesso! (ID: " + c.getCdclause() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cláusula: " + e.getMessage());
        }
    }

    /**
     * Busca uma cláusula pelo seu identificador.
     * 
     * @param id Identificador da cláusula.
     * @return Objeto Clauses ou null.
     */
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
                    c.setNrorder(rs.getInt("nrorder"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as cláusulas cadastradas, ordenadas por tópico e ordem.
     * 
     * @return Lista de objetos Clauses.
     */
    public List<Clauses> listAll() {
        List<Clauses> list = new ArrayList<>();
        String sql = "SELECT * FROM Clauses ORDER BY cdtopic, nrorder";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Clauses c = new Clauses();
                c.setCdclause(rs.getInt("cdclause"));
                c.setDstext(rs.getString("dstext"));
                c.setCdtopic(rs.getInt("cdtopic"));
                c.setNrorder(rs.getInt("nrorder"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza os dados de uma cláusula existente.
     * 
     * @param c Objeto contendo os dados atualizados.
     */
    public void update(Clauses c) {
        String sql = "UPDATE Clauses SET dstext=?, cdtopic=?, nrorder=? WHERE cdclause=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDstext());
            ps.setInt(2, c.getCdtopic());
            ps.setInt(3, c.getNrorder());
            ps.setInt(4, c.getCdclause());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui uma cláusula pelo seu identificador.
     * 
     * @param id Identificador da cláusula.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Clauses WHERE cdclause = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cláusula: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca todas as cláusulas vinculadas a um tópico específico.
     * 
     * @param topicId Identificador do tópico.
     * @return Lista de objetos Clauses.
     */
    public List<Clauses> findByTopicId(int topicId) {
        List<Clauses> list = new ArrayList<>();
        String sql = "SELECT * FROM Clauses WHERE cdtopic = ? ORDER BY nrorder";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Clauses c = new Clauses();
                    c.setCdclause(rs.getInt("cdclause"));
                    c.setDstext(rs.getString("dstext"));
                    c.setCdtopic(rs.getInt("cdtopic"));
                    c.setNrorder(rs.getInt("nrorder"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}