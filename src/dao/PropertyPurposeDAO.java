package dao;

import model.Property_Purposes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as finalidades de imóvel (ex: Venda, Locação).
 */
public class PropertyPurposeDAO {

    /**
     * Insere uma nova finalidade de imóvel.
     * 
     * @param pp Objeto contendo os dados da finalidade.
     */
    public void insert(Property_Purposes pp) {
        String sql = "INSERT INTO Property_Purposes (nmpurpose) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pp.getNmpurpose());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) pp.setCdpurpose(keys.getInt(1));
            }
            System.out.println("Finalidade de imóvel inserida com sucesso! (ID: " + pp.getCdpurpose() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir finalidade: " + e.getMessage());
        }
    }

    /**
     * Busca uma finalidade de imóvel pelo ID.
     * 
     * @param id Identificador da finalidade.
     * @return Objeto Property_Purposes ou null.
     */
    public Property_Purposes findById(int id) {
        String sql = "SELECT * FROM Property_Purposes WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property_Purposes pp = new Property_Purposes();
                    pp.setCdpurpose(rs.getInt("cdpurpose"));
                    pp.setNmpurpose(rs.getString("nmpurpose"));
                    return pp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as finalidades de imóvel cadastradas.
     * 
     * @return Lista de objetos Property_Purposes.
     */
    public List<Property_Purposes> listAll() {
        List<Property_Purposes> list = new ArrayList<>();
        String sql = "SELECT * FROM Property_Purposes ORDER BY cdpurpose";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Property_Purposes pp = new Property_Purposes();
                pp.setCdpurpose(rs.getInt("cdpurpose"));
                pp.setNmpurpose(rs.getString("nmpurpose"));
                list.add(pp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza uma finalidade de imóvel existente.
     * 
     * @param pp Objeto contendo os dados atualizados.
     */
    public void update(Property_Purposes pp) {
        String sql = "UPDATE Property_Purposes SET nmpurpose = ? WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pp.getNmpurpose());
            ps.setInt(2, pp.getCdpurpose());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui uma finalidade de imóvel.
     * 
     * @param id Identificador da finalidade.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Property_Purposes WHERE cdpurpose = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir finalidade: " + e.getMessage());
            return false;
        }
    }
}