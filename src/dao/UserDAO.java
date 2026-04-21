package dao;

import model.Users;
import model.Broker_Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // MÉTODO: CONSULTAR (Read)
public Users findById(int id) {
    // SQL simplificado para garantir que não dê erro de coluna inexistente
    String sql = "SELECT * FROM users WHERE cduser = ?";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Users u = new Users();
                u.setCduser(rs.getInt("cduser"));
                u.setNmuser(rs.getString("nmuser"));
                u.setDocument(rs.getString("document"));
                u.setNrcellphone(rs.getString("nrcellphone"));
                u.setDtbirth(rs.getDate("dtbirth").toLocalDate());
                u.setCdaddress(rs.getInt("cdaddress"));
                u.setCdoccupation(rs.getInt("cdoccupation"));
                return u;
            }
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar usuário: " + e.getMessage());
    }
    return null;
}

    // MÉTODO: ATUALIZAR (Update) - Novo!
public void update(Users u) {
    String sql = "UPDATE users SET nmuser=?, document=?, fgdocument=?, nrcellphone=?, dtbirth=?, cdaddress=?, cdoccupation=? WHERE cduser=?";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, u.getNmuser());
        ps.setString(2, u.getDocument());
        ps.setInt(3, u.getFgdocument());
        ps.setString(4, u.getNrcellphone());
        ps.setDate(5, java.sql.Date.valueOf(u.getDtbirth()));
        ps.setInt(6, u.getCdaddress());
        ps.setInt(7, u.getCdoccupation());
        ps.setInt(8, u.getCduser());
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Método para verificar se o usuário tem "rabos presos" no sistema
public String verificarVinculos(int id) {
    try (Connection conn = Conexao.getConexao()) {
        // Verifica se é dono de algum imóvel
        String sqlProp = "SELECT COUNT(*) FROM properties_users WHERE cduser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlProp)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "PROPRIETÁRIO VINCULADO A IMÓVEL";
        }
        // Verifica se tem contrato (como locatário/comprador)
        String sqlCont = "SELECT COUNT(*) FROM user_contract WHERE cduser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCont)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "CLIENTE COM CONTRATO ATIVO";
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null; // Sem vínculos
}

    // MÉTODO: EXCLUIR (Delete)
    public void delete(int id) {
        String sql = "DELETE FROM Users WHERE cduser = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Usuário removido com sucesso!");
            } else {
                System.out.println("Nenhum usuário encontrado com esse ID.");
            }
        } catch (SQLException e) {
            // Se houver contratos ou imóveis ligados ao usuário, o banco vai dar erro de FK aqui
            System.err.println("Erro ao excluir (verifique se há vínculos): " + e.getMessage());
        }
    }

    // MÉTODO: SALVAR (Create / Processo de Negócio)
    public void saveUser(Users user, Broker_Data broker) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false); 

            String sqlUser = "INSERT INTO Users (dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int generatedUserId = 0;

            try (PreparedStatement stmtU = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                // Tratamento de segurança para datas nulas
                if (user.getDtbirth() != null) {
                    stmtU.setDate(1, Date.valueOf(user.getDtbirth()));
                } else {
                    stmtU.setNull(1, Types.DATE);
                }
                
                stmtU.setInt(2, user.getFgdocument());
                stmtU.setString(3, user.getDocument());
                stmtU.setString(4, user.getNmuser());
                stmtU.setString(5, user.getNrcellphone());
                stmtU.setInt(6, user.getCdaddress());
                stmtU.setInt(7, user.getCdoccupation());
                
                stmtU.executeUpdate();

                ResultSet rs = stmtU.getGeneratedKeys();
                if (rs.next()) {
                    generatedUserId = rs.getInt(1);
                }
            }

            if (broker != null && generatedUserId > 0) {
                String sqlBroker = "INSERT INTO Broker_Data (nrcreci, cduser) VALUES (?, ?)";
                try (PreparedStatement stmtB = conn.prepareStatement(sqlBroker)) {
                    stmtB.setString(1, broker.getNrcreci());
                    stmtB.setInt(2, generatedUserId); 
                    stmtB.executeUpdate();
                }
            }

            conn.commit(); 
            System.out.println("Dados salvos com sucesso!");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<String> getAllUsersList() {
    List<String> list = new ArrayList<>();
    String sql = "SELECT cduser, nmuser FROM users ORDER BY cduser";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            list.add("ID: " + rs.getInt("cduser") + " | Nome: " + rs.getString("nmuser"));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

public List<String> getDeletableUsers() {
    List<String> list = new ArrayList<>();
    // Busca usuários que NÃO estão em properties_users E NÃO estão em user_contract
    String sql = "SELECT cduser, nmuser FROM users " +
                 "WHERE cduser NOT IN (SELECT cduser FROM properties_users) " +
                 "AND cduser NOT IN (SELECT cduser FROM user_contract) " +
                 "ORDER BY cduser";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            list.add("ID: " + rs.getInt("cduser") + " | Nome: " + rs.getString("nmuser"));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

}