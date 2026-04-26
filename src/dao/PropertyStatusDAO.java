package dao;

import model.Property_Status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os status de imóvel (ex: Disponível, Alugado).
 */
public class PropertyStatusDAO {

    /**
     * Insere um novo status de imóvel.
     * 
     * @param psObj Objeto contendo os dados do status.
     */
    public void insert(Property_Status psObj) {
        String sql = "INSERT INTO Property_Status (nmstatus) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, psObj.getNmstatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) psObj.setCdstatus(keys.getInt(1));
            }
            System.out.println("Status de imóvel inserido com sucesso! (ID: " + psObj.getCdstatus() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir status: " + e.getMessage());
        }
    }

    /**
     * Busca um status de imóvel pelo ID.
     * 
     * @param id Identificador do status.
     * @return Objeto Property_Status ou null.
     */
    public Property_Status findById(int id) {
        String sql = "SELECT * FROM Property_Status WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property_Status p = new Property_Status();
                    p.setCdstatus(rs.getInt("cdstatus"));
                    p.setNmstatus(rs.getString("nmstatus"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os status de imóvel cadastrados.
     * 
     * @return Lista de objetos Property_Status.
     */
    public List<Property_Status> listAll() {
        List<Property_Status> list = new ArrayList<>();
        String sql = "SELECT * FROM Property_Status ORDER BY cdstatus";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Property_Status p = new Property_Status();
                p.setCdstatus(rs.getInt("cdstatus"));
                p.setNmstatus(rs.getString("nmstatus"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza um status de imóvel existente.
     * 
     * @param psObj Objeto contendo os dados atualizados.
     */
    public void update(Property_Status psObj) {
        String sql = "UPDATE Property_Status SET nmstatus = ? WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, psObj.getNmstatus());
            ps.setInt(2, psObj.getCdstatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui um status de imóvel.
     * 
     * @param id Identificador do status.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Property_Status WHERE cdstatus = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir status: " + e.getMessage());
            return false;
        }
    }
}