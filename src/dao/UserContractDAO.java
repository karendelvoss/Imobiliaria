package dao;

import dto.ParticipantDTO;
import model.SignatureStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciar as operações de persistência da tabela de junção
 * User_Contract_Contracts_Users_Roles e consolidar dados para relatórios.
 */
public class UserContractDAO {

    /**
     * Busca os participantes de um contrato e consolida suas informações em um DTO.
     * @param contractId O ID do contrato.
     * @return Uma lista de ParticipantDTO com os dados para o relatório.
     */
    public List<ParticipantDTO> findParticipantsByContractId(int contractId) {
        List<ParticipantDTO> participants = new ArrayList<>();
        String sql = "SELECT " +
                     "    u.nmuser, " +
                     "    u.document, " +
                     "    r.nmrole, " +
                     "    uc.vlparticipation, " +
                     "    u.nrcellphone, " +
                     "    uc.fgsignaturestatus " +
                     "FROM User_Contract_Contracts_Users_Roles uc " +
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
                    dto.setPercentualParticipacao(rs.getDouble("vlparticipation"));
                    dto.setContatoPrincipal(rs.getString("nrcellphone"));
                    dto.setStatusAssinatura(SignatureStatus.fromCode(rs.getInt("fgsignaturestatus")).getDescription());
                    participants.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar participantes do contrato: " + e.getMessage());
            e.printStackTrace();
        }
        return participants;
    }
}