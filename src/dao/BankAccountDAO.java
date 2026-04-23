package dao;

import model.Bank_Accounts;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankAccountDAO {
    public void insert(Bank_Accounts ba) {
        String sql = "INSERT INTO Bank_Accounts (nragency, nraccount, nrpixkey, cduser) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ba.getNragency());
            ps.setString(2, ba.getNraccount());
            ps.setString(3, ba.getNrpixkey());
            ps.setInt(4, ba.getCduser());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ba.setCdbankaccount(keys.getInt(1));
            }
            System.out.println("Conta bancária inserida com sucesso! (ID: " + ba.getCdbankaccount() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir conta bancária: " + e.getMessage());
        }
    }

    public Bank_Accounts findById(int id) {
        String sql = "SELECT * FROM Bank_Accounts WHERE cdbankaccount = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bank_Accounts ba = new Bank_Accounts();
                    ba.setCdbankaccount(rs.getInt("cdbankaccount"));
                    ba.setNragency(rs.getString("nragency"));
                    ba.setNraccount(rs.getString("nraccount"));
                    ba.setNrpixkey(rs.getString("nrpixkey"));
                    ba.setCduser(rs.getInt("cduser"));
                    return ba;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Bank_Accounts> listAll() {
        List<Bank_Accounts> list = new ArrayList<>();
        String sql = "SELECT * FROM Bank_Accounts ORDER BY cdbankaccount";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Bank_Accounts ba = new Bank_Accounts();
                ba.setCdbankaccount(rs.getInt("cdbankaccount"));
                ba.setNragency(rs.getString("nragency"));
                ba.setNraccount(rs.getString("nraccount"));
                ba.setNrpixkey(rs.getString("nrpixkey"));
                ba.setCduser(rs.getInt("cduser"));
                list.add(ba);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Bank_Accounts ba) {
        String sql = "UPDATE Bank_Accounts SET nragency=?, nraccount=?, nrpixkey=?, cduser=? WHERE cdbankaccount=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ba.getNragency());
            ps.setString(2, ba.getNraccount());
            ps.setString(3, ba.getNrpixkey());
            ps.setInt(4, ba.getCduser());
            ps.setInt(5, ba.getCdbankaccount());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Bank_Accounts WHERE cdbankaccount = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir conta bancária: " + e.getMessage());
            return false;
        }
    }
}