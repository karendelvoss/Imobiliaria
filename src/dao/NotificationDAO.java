package dao;

import model.NotificationChannel;
import model.NotificationEventType;
import model.Notifications;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência e lógica de deduplicação de notificações.
 */
public class NotificationDAO {

    /**
     * Insere uma nova notificação no banco de dados.
     * 
     * @param n Objeto contendo os dados da notificação.
     */
    public void insert(Notifications n) {
        String sql = "INSERT INTO Notifications (dsmessage, dtsend, cdcontract, cduser, cdnotificationtemplate, fgchannel) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getDsmessage());
            ps.setDate(2, n.getDtsend() != null ? Date.valueOf(n.getDtsend()) : null);
            ps.setInt(3, n.getCdcontract());
            ps.setInt(4, n.getCduser());
            ps.setInt(5, n.getCdnotificationtemplate());
            ps.setInt(6, n.getFgchannel() > 0 ? n.getFgchannel() : NotificationChannel.EMAIL.getCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setCdnotification(keys.getInt(1));
            }
            System.out.println("Notificação inserida com sucesso! (ID: " + n.getCdnotification() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir notificação: " + e.getMessage());
        }
    }

    /**
     * Busca uma notificação pelo seu identificador.
     * 
     * @param id Identificador da notificação.
     * @return Objeto Notifications ou null.
     */
    public Notifications findById(int id) {
        String sql = "SELECT * FROM Notifications WHERE cdnotification = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todas as notificações cadastradas, ordenadas pela data de envio.
     * 
     * @return Lista de objetos Notifications.
     */
    public List<Notifications> listAll() {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications ORDER BY dtsend DESC, cdnotification DESC";
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Exclui uma notificação pelo seu identificador.
     * 
     * @param id Identificador da notificação.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Notifications WHERE cdnotification = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir notificação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se já existe uma notificação com os mesmos parâmetros básicos na data informada.
     * 
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param cdtemplate Identificador do modelo de notificação.
     * @param dtsend Data de envio.
     * @return true se a notificação já existe.
     */
    public boolean jaExiste(int cdcontract, int cduser, int cdtemplate, java.time.LocalDate dtsend) {
        String sql = "SELECT 1 FROM Notifications WHERE cdcontract = ? AND cduser = ? AND cdnotificationtemplate = ? AND dtsend = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cdcontract);
            ps.setInt(2, cduser);
            ps.setInt(3, cdtemplate);
            ps.setDate(4, Date.valueOf(dtsend));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar duplicata de notificação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca o ID do modelo de notificação baseado no código do tipo de evento.
     * 
     * @param tpcode Código do tipo de evento.
     * @return ID do modelo ou -1 se não encontrado.
     */
    public int findTemplateIdByCode(int tpcode) {
        String sql = "SELECT cdnotificationtemplate FROM Notification_Templates WHERE tpcode = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tpcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar template de notificação: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registra uma nova notificação caso ela ainda não exista para os mesmos parâmetros e data.
     * 
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param tpcode Código do tipo de evento.
     * @param mensagem Conteúdo da mensagem.
     * @param dtsend Data de envio.
     * @return true se a notificação foi criada com sucesso.
     */
    public boolean criarSeNaoExistir(int cdcontract, int cduser, int tpcode,
                                      String mensagem, java.time.LocalDate dtsend) {
        return criarSeNaoExistir(cdcontract, cduser, tpcode, mensagem, dtsend, NotificationChannel.EMAIL);
    }

    /**
     * Registra uma nova notificação via canal específico caso ela ainda não exista.
     * 
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param tpcode Código do tipo de evento.
     * @param mensagem Conteúdo da mensagem.
     * @param dtsend Data de envio.
     * @param channel Canal de notificação desejado.
     * @return true se a notificação foi criada com sucesso.
     */
    public boolean criarSeNaoExistir(int cdcontract, int cduser, int tpcode,
                                      String mensagem, java.time.LocalDate dtsend,
                                      NotificationChannel channel) {
        int cdtemplate = findTemplateIdByCode(tpcode);
        if (cdtemplate < 0) {
            System.err.printf("Template não encontrado para tpcode=%d.%n", tpcode);
            return false;
        }

        if (jaExiste(cdcontract, cduser, cdtemplate, dtsend)) {
            return false;
        }

        Notifications n = new Notifications();
        n.setCdcontract(cdcontract);
        n.setCduser(cduser);
        n.setCdnotificationtemplate(cdtemplate);
        n.setDsmessage(mensagem);
        n.setDtsend(dtsend);
        n.setFgchannel(channel.getCode());
        insert(n);
        return true;
    }

    /**
     * Mapeia um registro do ResultSet para um objeto Notifications.
     * 
     * @param rs ResultSet posicionado no registro.
     * @return Objeto Notifications preenchido.
     * @throws SQLException Caso ocorra erro no mapeamento.
     */
    private Notifications mapRow(ResultSet rs) throws SQLException {
        Notifications n = new Notifications();
        n.setCdnotification(rs.getInt("cdnotification"));
        n.setDsmessage(rs.getString("dsmessage"));
        if (rs.getDate("dtsend") != null) n.setDtsend(rs.getDate("dtsend").toLocalDate());
        n.setCdcontract(rs.getInt("cdcontract"));
        n.setCduser(rs.getInt("cduser"));
        n.setCdnotificationtemplate(rs.getInt("cdnotificationtemplate"));
        n.setFgchannel(rs.getInt("fgchannel"));
        return n;
    }
}