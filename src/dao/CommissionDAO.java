package dao;

import model.Commissions;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as comissões.
 */
public class CommissionDAO {

    /**
     * Insere uma nova comissão no banco de dados.
     * 
     * @param c Objeto contendo os dados da comissão.
     */
    public void insert(Commissions c) {
        String sql = "INSERT INTO Commissions (vlcommission, dtpayment, cdcontract) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, c.getVlcommission());
            ps.setDate(2, c.getDtpayment() != null ? Date.valueOf(c.getDtpayment()) : null);
            ps.setInt(3, c.getCdcontract());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setCdcommission(keys.getInt(1));
            }
            System.out.println("Comissão inserida com sucesso! (ID: " + c.getCdcommission() + ")");
        } catch (SQLException e) { System.err.println("Erro ao inserir comissão: " + e.getMessage()); }
    }

    public Commissions findById(int id) {
        String sql = "SELECT * FROM Commissions WHERE cdcommission = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Commissions c = new Commissions();
                    c.setCdcommission(rs.getInt("cdcommission"));
                    c.setVlcommission(rs.getDouble("vlcommission"));
                    if(rs.getDate("dtpayment") != null) c.setDtpayment(rs.getDate("dtpayment").toLocalDate());
                    c.setCdcontract(rs.getInt("cdcontract"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as comissões cadastradas.
     * 
     * @return Lista de objetos Commissions.
     */
    public List<Commissions> listAll() {
        List<Commissions> list = new ArrayList<>();
        String sql = "SELECT * FROM Commissions ORDER BY cdcommission";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Commissions c = new Commissions();
                c.setCdcommission(rs.getInt("cdcommission"));
                c.setVlcommission(rs.getDouble("vlcommission"));
                if(rs.getDate("dtpayment") != null) c.setDtpayment(rs.getDate("dtpayment").toLocalDate());
                c.setCdcontract(rs.getInt("cdcontract"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Atualiza os dados de uma comissão existente.
     * 
     * @param c Objeto contendo os dados atualizados.
     */
    public void update(Commissions c) {
        String sql = "UPDATE Commissions SET vlcommission=?, dtpayment=?, cdcontract=? WHERE cdcommission=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, c.getVlcommission());
            ps.setDate(2, c.getDtpayment() != null ? Date.valueOf(c.getDtpayment()) : null);
            ps.setInt(3, c.getCdcontract());
            ps.setInt(4, c.getCdcommission());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exclui uma comissão pelo seu identificador.
     * 
     * @param id Identificador da comissão.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Commissions WHERE cdcommission = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}