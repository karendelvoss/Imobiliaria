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
        ps.setBoolean(3, u.isFgdocument());
        ps.setString(4, u.getNrcellphone());
        ps.setDate(5, java.sql.Date.valueOf(u.getDtbirth()));
        ps.setInt(6, u.getCdaddress());
        ps.setInt(7, u.getCdoccupation());
        ps.setInt(8, u.getCduser());
        ps.executeUpdate();
    } catch (SQLException e) {
        if (e.getMessage().contains("cdaddress") || e.getMessage().contains("addresses")) {
            System.err.println("ERRO: O ID de Endereço (" + u.getCdaddress() + ") informado não existe no sistema.");
        } else if (e.getMessage().contains("cdoccupation") || e.getMessage().contains("occupations")) {
            System.err.println("ERRO: O ID de Profissão (" + u.getCdoccupation() + ") informado não existe no sistema.");
        } else if (e.getMessage().contains("document") || e.getMessage().contains("users_document_key")) {
            System.err.println("ERRO: Já existe um usuário cadastrado com este documento.");
        } else {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
        }
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
        String sqlCont = "SELECT COUNT(*) FROM User_Contract WHERE cduser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCont)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "CLIENTE COM CONTRATO ATIVO";
        }
        // Verifica se possui conta bancária
        String sqlBank = "SELECT COUNT(*) FROM bank_accounts WHERE cduser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlBank)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "CONTA BANCÁRIA VINCULADA";
        }
    } catch (SQLException e) { 
        e.printStackTrace(); 
        return "ERRO AO VERIFICAR VÍNCULOS"; 
    }
    return null; // Sem vínculos
}

    // MÉTODO: EXCLUIR (Delete)
    public boolean delete(int id) {
        String sql = "DELETE FROM Users WHERE cduser = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                System.out.println("Nenhum usuário encontrado com esse ID.");
                return false;
            }
        } catch (SQLException e) {
            // Se houver contratos ou imóveis ligados ao usuário, o banco vai dar erro de FK aqui
            System.err.println("Erro ao excluir (verifique se há vínculos): " + e.getMessage());
            return false;
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

                stmtU.setBoolean(2, user.isFgdocument());
                stmtU.setString(3, user.getDocument());
                stmtU.setString(4, user.getNmuser());
                stmtU.setString(5, user.getNrcellphone());
                stmtU.setInt(6, user.getCdaddress());
                stmtU.setInt(7, user.getCdoccupation());

                stmtU.executeUpdate();
                try (ResultSet keys = stmtU.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedUserId = keys.getInt(1);
                        user.setCduser(generatedUserId);
                    }
                }
            }

            if (broker != null && generatedUserId > 0) {
                String sqlBroker = "INSERT INTO Broker_Data (cduser, nrcreci) VALUES (?, ?)";
                try (PreparedStatement stmtB = conn.prepareStatement(sqlBroker)) {
                    stmtB.setInt(1, generatedUserId);
                    stmtB.setString(2, broker.getNrcreci());
                    stmtB.executeUpdate();
                }
            }

            conn.commit(); 
            System.out.println("Dados salvos com sucesso!");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            if (e.getMessage().contains("cdaddress") || e.getMessage().contains("addresses")) {
                System.err.println("ERRO: O ID de Endereço (" + user.getCdaddress() + ") informado não existe no banco de dados.");
            } else if (e.getMessage().contains("cdoccupation") || e.getMessage().contains("occupations")) {
                System.err.println("ERRO: O ID de Profissão (" + user.getCdoccupation() + ") informado não existe no banco de dados.");
            } else if (e.getMessage().contains("document") || e.getMessage().contains("users_document_key")) {
                System.err.println("ERRO: Já existe um usuário cadastrado com este documento.");
            } else {
                System.err.println("Erro ao salvar usuário: " + e.getMessage());
            }
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
                 "AND cduser NOT IN (SELECT cduser FROM User_Contract) " +
                 "AND cduser NOT IN (SELECT cduser FROM bank_accounts) " +
                 "ORDER BY cduser";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            list.add("ID: " + rs.getInt("cduser") + " | Nome: " + rs.getString("nmuser"));
        }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}