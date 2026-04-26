package dao;

import dto.ParticipantDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência da tabela de junção entre usuários, contratos e papéis (User_Contract).
 */
public class UserContractDAO {

    /**
     * Busca os participantes de um contrato e consolida suas informações em um DTO para relatórios.
     * 
     * @param contractId Identificador do contrato.
     * @return Lista de ParticipantDTO.
     */
    public List<ParticipantDTO> findParticipantsByContractId(int contractId) {
        List<ParticipantDTO> participants = new ArrayList<>();
        String sql = "SELECT " +
                     "    u.nmuser, " +
                     "    u.document, " +
                     "    r.nmrole, " +
                     "    u.nrcellphone " +
                     "FROM User_Contract uc " +
                     "JOIN Users u ON uc.cduser = u.cduser " +
                     "JOIN Roles r ON uc.cdrole = r.cdrole " +
                     "WHERE uc.cdcontract = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ParticipantDTO dto = new ParticipantDTO();
                    dto.setNomeRazaoSocial(rs.getString("nmuser"));
                    dto.setCpfCnpj(rs.getString("document"));
                    dto.setPapelRole(rs.getString("nmrole"));
                    dto.setContatoPrincipal(rs.getString("nrcellphone"));
                    participants.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar participantes do contrato: " + e.getMessage());
        }
        return participants;
    }

    /**
     * Vincula um usuário a um contrato com um determinado papel.
     * 
     * @param uc Objeto contendo o vínculo (usuário, contrato e papel).
     */
    public void insert(model.User_Contract uc) {
        String sql = "INSERT INTO User_Contract (cdcontract, cduser, cdrole) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, uc.getCdcontract());
            ps.setInt(2, uc.getCduser());
            ps.setInt(3, uc.getCdrole());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir parte no contrato: " + e.getMessage());
        }
    }

    /**
     * Busca todos os vínculos de usuários associados a um contrato.
     * 
     * @param contractId Identificador do contrato.
     * @return Lista de objetos User_Contract.
     */
    public List<model.User_Contract> findByContractId(int contractId) {
        List<model.User_Contract> list = new ArrayList<>();
        String sql = "SELECT cdcontract, cduser, cdrole FROM User_Contract WHERE cdcontract = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.User_Contract uc = new model.User_Contract();
                    uc.setCdcontract(rs.getInt("cdcontract"));
                    uc.setCduser(rs.getInt("cduser"));
                    uc.setCdrole(rs.getInt("cdrole"));
                    list.add(uc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar partes por contrato: " + e.getMessage());
        }
        return list;
    }
}