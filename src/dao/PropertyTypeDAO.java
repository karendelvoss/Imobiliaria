package dao;

import model.Property_Types;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os tipos de imóvel (ex: Casa, Apartamento).
 */
public class PropertyTypeDAO {

    /**
     * Insere um novo tipo de imóvel.
     * 
     * @param pt Objeto contendo os dados do tipo de imóvel.
     */
    public void insert(Property_Types pt) {
        String sql = "INSERT INTO Property_Types (nmtype) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pt.getNmtype());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) pt.setCdtype(keys.getInt(1));
            }
            System.out.println("Tipo de imóvel inserido com sucesso! (ID: " + pt.getCdtype() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir tipo de imóvel: " + e.getMessage());
        }
    }

    /**
     * Busca um tipo de imóvel pelo ID.
     * 
     * @param id Identificador do tipo de imóvel.
     * @return Objeto Property_Types ou null.
     */
    public Property_Types findById(int id) {
        String sql = "SELECT * FROM Property_Types WHERE cdtype = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property_Types pt = new Property_Types();
                    pt.setCdtype(rs.getInt("cdtype"));
                    pt.setNmtype(rs.getString("nmtype"));
                    return pt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os tipos de imóvel cadastrados.
     * 
     * @return Lista de objetos Property_Types.
     */
    public List<Property_Types> listAll() {
        List<Property_Types> list = new ArrayList<>();
        String sql = "SELECT * FROM Property_Types ORDER BY cdtype";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Property_Types pt = new Property_Types();
                pt.setCdtype(rs.getInt("cdtype"));
                pt.setNmtype(rs.getString("nmtype"));
                list.add(pt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza um tipo de imóvel existente.
     * 
     * @param pt Objeto contendo os dados atualizados.
     */
    public void update(Property_Types pt) {
        String sql = "UPDATE Property_Types SET nmtype = ? WHERE cdtype = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pt.getNmtype());
            ps.setInt(2, pt.getCdtype());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui um tipo de imóvel.
     * 
     * @param id Identificador do tipo de imóvel.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Property_Types WHERE cdtype = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir tipo de imóvel: " + e.getMessage());
            return false;
        }
    }
}