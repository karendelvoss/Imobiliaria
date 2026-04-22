package dao;

import model.Addresses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    public void insert(Addresses a) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdaddress), 0) + 1 AS prox_id FROM Addresses";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) proxId = rsMax.getInt("prox_id");
        } catch (SQLException e) { e.printStackTrace(); }

        String sql = "INSERT INTO Addresses (cdaddress, cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, proxId);
            ps.setString(2, a.getCdzipcode());
            ps.setString(3, a.getNmstreet());
            ps.setString(4, a.getNraddress());
            ps.setString(5, a.getDscomplement());
            ps.setInt(6, a.getCddistrict());
            ps.executeUpdate();
            System.out.println("Endereço inserido com sucesso! (ID: " + proxId + ")");
        } catch (SQLException e) { System.err.println("Erro ao inserir endereço: " + e.getMessage()); }
    }

    public Addresses findById(int id) {
        String sql = "SELECT * FROM Addresses WHERE cdaddress = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Addresses a = new Addresses();
                    a.setCdaddress(rs.getInt("cdaddress"));
                    a.setNmstreet(rs.getString("nmstreet"));
                    a.setNraddress(rs.getString("nraddress"));
                    a.setCddistrict(rs.getInt("cddistrict"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Addresses> listAll() {
        List<Addresses> list = new ArrayList<>();
        String sql = "SELECT * FROM Addresses ORDER BY cdaddress";
        try (Connection conn = Conexao.getConexao(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Addresses a = new Addresses();
                a.setCdaddress(rs.getInt("cdaddress"));
                a.setCdzipcode(rs.getString("cdzipcode"));
                a.setNmstreet(rs.getString("nmstreet"));
                a.setNraddress(rs.getString("nraddress"));
                a.setDscomplement(rs.getString("dscomplement"));
                a.setCddistrict(rs.getInt("cddistrict"));
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> listAllFormatted() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT a.cdaddress, a.nmstreet, a.nraddress, d.nmdistrict " +
                     "FROM Addresses a " +
                     "JOIN Districts d ON a.cddistrict = d.cddistrict " +
                     "ORDER BY a.cdaddress";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String formatted = String.format("ID: %-3d | %s, %s - Bairro: %s",
                        rs.getInt("cdaddress"),
                        rs.getString("nmstreet"),
                        rs.getString("nraddress"),
                        rs.getString("nmdistrict"));
                list.add(formatted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Addresses a) {
        String sql = "UPDATE Addresses SET cdzipcode=?, nmstreet=?, nraddress=?, dscomplement=?, cddistrict=? WHERE cdaddress=?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCdzipcode()); ps.setString(2, a.getNmstreet()); ps.setString(3, a.getNraddress());
            ps.setString(4, a.getDscomplement()); ps.setInt(5, a.getCddistrict()); ps.setInt(6, a.getCdaddress()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Addresses WHERE cdaddress = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Erro ao excluir endereço: " + e.getMessage()); return false; }
    }
}