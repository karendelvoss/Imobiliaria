package dao;

import model.Occupations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para a entidade de Profissões (Occupations).
 */
public class OccupationDAO {

    /**
     * Insere uma nova profissão no banco de dados.
     * 
     * @param occ Objeto contendo os dados da profissão.
     */
    public void save(Occupations occ) {
        String sql = "INSERT INTO Occupations (nmoccupation) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, occ.getNmoccupation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) occ.setCdoccupation(keys.getInt(1));
            }
            System.out.println("Profissão '" + occ.getNmoccupation() + "' cadastrada com sucesso com o ID: " + occ.getCdoccupation());
        } catch (SQLException e) {
            System.err.println("Erro ao salvar profissão: " + e.getMessage());
        }
    }

    /**
     * Busca uma profissão pelo seu identificador.
     * 
     * @param id Identificador da profissão.
     * @return Objeto Occupations ou null.
     */
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

    /**
     * Lista todas as profissões cadastradas de forma formatada.
     * 
     * @return Lista de Strings contendo ID e Nome das profissões.
     */
    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM Occupations ORDER BY nmoccupation";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdoccupation") + " | Nome: " + rs.getString("nmoccupation"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar profissões: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza o nome de uma profissão existente.
     * 
     * @param occ Objeto contendo o ID e o novo nome da profissão.
     */
    public void update(Occupations occ) {
        String sql = "UPDATE Occupations SET nmoccupation = ? WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, occ.getNmoccupation());
            ps.setInt(2, occ.getCdoccupation());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Profissão atualizada com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar profissão: " + e.getMessage());
        }
    }

    /**
     * Exclui uma profissão pelo seu identificador.
     * 
     * @param id Identificador da profissão.
     */
    public void delete(int id) {
        String sql = "DELETE FROM Occupations WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Profissão excluída com sucesso.");
            } else {
                System.out.println("Nenhuma profissão encontrada com o ID informado.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("violates foreign key constraint")) {
                System.err.println("ERRO: Impossível excluir. Esta profissão está em uso por um ou mais usuários.");
            } else {
                System.err.println("Erro ao excluir profissão: " + e.getMessage());
            }
        }
    }
}