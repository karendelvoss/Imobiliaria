package dao;

import model.Broker_Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os dados de corretores.
 */
public class BrokerDataDAO {

    /**
     * Insere novos dados de corretor.
     * 
     * @param b Objeto contendo os dados do corretor.
     */
    public void insert(Broker_Data b) {
        String sql = "INSERT INTO Broker_Data (nrcreci, cduser) VALUES (?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getNrcreci());
            ps.setInt(2, b.getCduser());
            ps.executeUpdate();
            System.out.println("Dados de corretor inseridos com sucesso!");
        } catch (SQLException e) { System.err.println("Erro ao inserir corretor: " + e.getMessage()); }
    }

    /**
     * Busca os dados de um corretor pelo ID do usuário.
     * 
     * @param cduser Identificador do usuário.
     * @return Objeto Broker_Data ou null.
     */
    public Broker_Data findById(int cduser) {
        String sql = "SELECT * FROM Broker_Data WHERE cduser = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cduser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Broker_Data b = new Broker_Data();
                    b.setNrcreci(rs.getString("nrcreci"));
                    b.setCduser(rs.getInt("cduser"));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os dados de corretores cadastrados.
     * 
     * @return Lista de dados de corretores.
     */
    public List<Broker_Data> listAll() {
        List<Broker_Data> list = new ArrayList<>();
        String sql = "SELECT * FROM Broker_Data";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Broker_Data b = new Broker_Data();
                b.setNrcreci(rs.getString("nrcreci"));
                b.setCduser(rs.getInt("cduser"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza os dados de um corretor.
     * 
     * @param b Objeto contendo os dados atualizados.
     */
    public void update(Broker_Data b) {
        String sql = "UPDATE Broker_Data SET nrcreci=? WHERE cduser=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getNrcreci());
            ps.setInt(2, b.getCduser());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui os dados de um corretor.
     * 
     * @param cduser Identificador do usuário.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int cduser) {
        String sql = "DELETE FROM Broker_Data WHERE cduser = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cduser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}