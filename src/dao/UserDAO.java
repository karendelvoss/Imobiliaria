package dao;

import model.Users;
import model.Broker_Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os usuários e corretores.
 */
public class UserDAO {

    /**
     * Busca um usuário pelo seu identificador.
     * 
     * @param id Identificador do usuário.
     * @return Objeto Users ou null.
     */
    public Users findById(int id) {
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
                    u.setDsissuingbody(rs.getString("dsissuingbody"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Atualiza os dados de um usuário existente.
     * 
     * @param u Objeto contendo os dados atualizados do usuário.
     */
    public void update(Users u) {
        String sql = "UPDATE users SET nmuser=?, document=?, fgdocument=?, nrcellphone=?, dtbirth=?, cdaddress=?, cdoccupation=?, dsissuingbody=? WHERE cduser=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNmuser());
            ps.setString(2, u.getDocument());
            ps.setBoolean(3, u.isFgdocument());
            ps.setString(4, u.getNrcellphone());
            ps.setDate(5, java.sql.Date.valueOf(u.getDtbirth()));
            ps.setInt(6, u.getCdaddress());
            ps.setInt(7, u.getCdoccupation());
            ps.setString(8, u.getDsissuingbody());
            ps.setInt(9, u.getCduser());
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

    /**
     * Verifica se um usuário possui vínculos ativos no sistema (imóveis, contratos, contas).
     * 
     * @param id Identificador do usuário.
     * @return Descrição do vínculo ou null caso não possua.
     */
    public String verificarVinculos(int id) {
        try (Connection conn = Conexao.getConexao()) {
            String sqlProp = "SELECT COUNT(*) FROM properties_users WHERE cduser = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlProp)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return "PROPRIETÁRIO VINCULADO A IMÓVEL";
            }
            String sqlCont = "SELECT COUNT(*) FROM User_Contract WHERE cduser = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCont)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return "CLIENTE COM CONTRATO ATIVO";
            }
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
        return null;
    }

    /**
     * Exclui um usuário pelo seu identificador.
     * 
     * @param id Identificador do usuário.
     * @return true se excluído com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Users WHERE cduser = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Salva um novo usuário e seus dados de corretor, se houver, em uma transação.
     * 
     * @param user Objeto contendo os dados do usuário.
     * @param broker Objeto contendo os dados do corretor ou null.
     */
    public void saveUser(Users user, Broker_Data broker) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false); 

            String sqlUser = "INSERT INTO Users (dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation, dsissuingbody) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedUserId = 0;

            try (PreparedStatement stmtU = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
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
                stmtU.setString(8, user.getDsissuingbody());

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

    /**
     * Lista todos os usuários cadastrados de forma simplificada.
     * 
     * @return Lista de Strings formatadas com ID e Nome.
     */
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

    /**
     * Lista apenas os usuários que podem ser excluídos por não possuírem vínculos ativos.
     * 
     * @return Lista de Strings formatadas com ID e Nome.
     */
    public List<String> getDeletableUsers() {
        List<String> list = new ArrayList<>();
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