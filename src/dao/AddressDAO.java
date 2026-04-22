package dao;

import model.Addresses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

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
}