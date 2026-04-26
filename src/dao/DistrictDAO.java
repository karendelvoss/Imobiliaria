package dao;

import model.Districts;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para a entidade de Bairros.
 */
public class DistrictDAO {

    /**
     * Insere um novo bairro no banco de dados.
     * 
     * @param d Objeto contendo os dados do bairro.
     */
    public void insert(Districts d) {
        String sqlInsert = "INSERT INTO Districts (nmdistrict, cdcity) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNmdistrict());
            ps.setInt(2, d.getCdcity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setCddistrict(keys.getInt(1));
            }
            System.out.println("Bairro inserido com sucesso! (ID: " + d.getCddistrict() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca um bairro pelo seu identificador.
     * 
     * @param id Identificador do bairro.
     * @return Objeto Districts ou null.
     */
    public Districts findById(int id) {
        String sql = "SELECT * FROM Districts WHERE cddistrict = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Districts d = new Districts();
                    d.setCddistrict(rs.getInt("cddistrict"));
                    d.setNmdistrict(rs.getString("nmdistrict"));
                    d.setCdcity(rs.getInt("cdcity"));
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os bairros cadastrados.
     * 
     * @return Lista de objetos Districts.
     */
    public List<Districts> listAll() {
        List<Districts> list = new ArrayList<>();
        String sql = "SELECT * FROM Districts";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Districts d = new Districts();
                d.setCddistrict(rs.getInt("cddistrict"));
                d.setNmdistrict(rs.getString("nmdistrict"));
                d.setCdcity(rs.getInt("cdcity"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza os dados de um bairro existente.
     * 
     * @param d Objeto contendo os dados atualizados.
     */
    public void update(Districts d) {
        String sql = "UPDATE Districts SET nmdistrict=?, cdcity=? WHERE cddistrict=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNmdistrict());
            ps.setInt(2, d.getCdcity());
            ps.setInt(3, d.getCddistrict());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui um bairro pelo seu identificador.
     * 
     * @param id Identificador do bairro.
     */
    public void delete(int id) {
        String sql = "DELETE FROM Districts WHERE cddistrict = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}