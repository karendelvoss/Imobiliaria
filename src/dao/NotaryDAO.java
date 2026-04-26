package dao;

import model.Notaries;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os dados notariais (Notaries).
 */
public class NotaryDAO {

    /**
     * Insere um novo registro notarial.
     * 
     * @param notary Objeto contendo os dados notariais.
     * @return O ID gerado para o registro ou -1 em caso de erro.
     */
    public int insertNotary(Notaries notary) {
        String sql = "INSERT INTO Notaries (cdcity, book, leaf, dt, nrnotary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notary.getCdcity());
            stmt.setString(2, notary.getBook());
            stmt.setString(3, notary.getLeaf());
            if (notary.getDt() != null) {
                stmt.setDate(4, Date.valueOf(notary.getDt()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setInt(5, notary.getNrnotary());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    notary.setCdnotary(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir Notary: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Busca um registro notarial pelo seu identificador.
     * 
     * @param id Identificador do registro.
     * @return Objeto Notaries ou null.
     */
    public Notaries findById(int id) {
        String sql = "SELECT * FROM Notaries WHERE cdnotary = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Notaries n = new Notaries();
                    n.setCdnotary(rs.getInt("cdnotary"));
                    n.setCdcity(rs.getInt("cdcity"));
                    n.setBook(rs.getString("book"));
                    n.setLeaf(rs.getString("leaf"));
                    if (rs.getDate("dt") != null) n.setDt(rs.getDate("dt").toLocalDate());
                    n.setNrnotary(rs.getInt("nrnotary"));
                    return n;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Notary: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os registros notariais cadastrados.
     * 
     * @return Lista de objetos Notaries.
     */
    public List<Notaries> listAll() {
        List<Notaries> list = new ArrayList<>();
        String sql = "SELECT * FROM Notaries";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Notaries n = new Notaries();
                n.setCdnotary(rs.getInt("cdnotary"));
                n.setCdcity(rs.getInt("cdcity"));
                n.setBook(rs.getString("book"));
                n.setLeaf(rs.getString("leaf"));
                if (rs.getDate("dt") != null) n.setDt(rs.getDate("dt").toLocalDate());
                n.setNrnotary(rs.getInt("nrnotary"));
                list.add(n);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Notaries: " + e.getMessage());
        }
        return list;
    }
}
