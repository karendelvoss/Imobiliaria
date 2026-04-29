package dao;

import model.Properties;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para os imóveis (Properties).
 */
public class PropertyDAO {

    /**
     * Insere um novo imóvel no banco de dados.
     * 
     * @param prop Objeto contendo os dados do imóvel.
     * @return O ID gerado para o imóvel ou -1 em caso de erro.
     */
    public int insertProperty(Properties prop) {
        String sql = "INSERT INTO Properties (nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, prop.getNrregistration());
            ps.setString(2, prop.getDsdescription());
            ps.setDouble(3, prop.getVltotalarea());
            ps.setInt(4, prop.getCdaddress());
            ps.setInt(5, prop.getCdtype());
            ps.setInt(6, prop.getCdpurpose());
            ps.setInt(7, prop.getCdstatus());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    prop.setCdproperty(keys.getInt(1));
                }
            }
            System.out.println("Imóvel cadastrado com sucesso! (ID: " + prop.getCdproperty() + ")");
            return prop.getCdproperty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Vincula um proprietário a um imóvel.
     * 
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário proprietário.
     */
    public void linkOwner(int idProperty, int idUser) {
        String sql = "INSERT INTO Properties_Users (cdproperty, cduser) VALUES (?, ?)";
        
        try (Connection conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProperty);
            ps.setInt(2, idUser);
            
            ps.executeUpdate();
            System.out.println("Proprietário vinculado ao imóvel com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("Erro ao vincular proprietário ao imóvel: " + e.getMessage());
        }
    }

    /**
     * Desvíncula um proprietário de um imóvel.
     * 
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário proprietário.
     */
    public void unlinkOwner(int idProperty, int idUser) {
        String sql = "DELETE FROM Properties_Users WHERE cdproperty = ? AND cduser = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProperty);
            ps.setInt(2, idUser);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Proprietário desvinculado do imóvel com sucesso!");
            } else {
                System.out.println("Nenhum vínculo encontrado para remoção.");
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao desvincular proprietário: " + e.getMessage());
        }
    }

    /**
     * Lista todos os imóveis cadastrados.
     * 
     * @return Lista de objetos Properties.
     */
    public List<Properties> listAll() {
        List<Properties> list = new ArrayList<>();
        String sql = "SELECT * FROM Properties";
        
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Properties p = new Properties();
                p.setCdproperty(rs.getInt("cdproperty"));
                p.setNrregistration(rs.getString("nrregistration"));
                p.setDsdescription(rs.getString("dsdescription")); 
                p.setVltotalarea(rs.getDouble("vltotalarea"));
                p.setCdaddress(rs.getInt("cdaddress"));
                p.setCdtype(rs.getInt("cdtype"));
                p.setCdpurpose(rs.getInt("cdpurpose"));
                p.setCdstatus(rs.getInt("cdstatus"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Exclui um imóvel pelo seu identificador.
     * 
     * @param id Identificador do imóvel.
     */
    public void deleteProperty(int id) {
        String sql = "DELETE FROM Properties WHERE cdproperty = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza os dados de um imóvel existente.
     * 
     * @param prop Objeto contendo os dados atualizados do imóvel.
     */
    public void updateProperty(Properties prop) {
        String sql = "UPDATE Properties SET nrregistration = ?, dsdescription = ?, " +
                     "vltotalarea = ?, cdaddress = ?, cdtype = ?, cdpurpose = ?, " +
                     "cdstatus = ? WHERE cdproperty = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, prop.getNrregistration());
            ps.setString(2, prop.getDsdescription());
            ps.setDouble(3, prop.getVltotalarea());
            ps.setInt(4, prop.getCdaddress());
            ps.setInt(5, prop.getCdtype());
            ps.setInt(6, prop.getCdpurpose());
            ps.setInt(7, prop.getCdstatus());
            ps.setInt(8, prop.getCdproperty());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar imóvel: " + e.getMessage());
        }
    }

    /**
     * Retorna uma lista de imóveis disponíveis para vinculação.
     * 
     * @return Lista de Strings formatadas com ID e Matrícula.
     */
    public List<String> getAvailableProperties() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cdproperty, nrregistration, dsdescription FROM properties ORDER BY cdproperty";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdproperty") + " | Reg: " + rs.getString("nrregistration"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Busca um imóvel pelo seu identificador.
     * 
     * @param id Identificador do imóvel.
     * @return Objeto Properties ou null.
     */
    public Properties findById(int id) {
        String sql = "SELECT * FROM Properties WHERE cdproperty = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Properties p = new Properties();
                    p.setCdproperty(rs.getInt("cdproperty"));
                    p.setNrregistration(rs.getString("nrregistration"));
                    p.setDsdescription(rs.getString("dsdescription")); 
                    p.setVltotalarea(rs.getDouble("vltotalarea"));
                    p.setCdaddress(rs.getInt("cdaddress"));
                    p.setCdtype(rs.getInt("cdtype"));
                    p.setCdpurpose(rs.getInt("cdpurpose"));
                    p.setCdstatus(rs.getInt("cdstatus"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar imóvel por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Conta quantos proprietários estão vinculados a um imóvel.
     * 
     * @param idProperty Identificador do imóvel.
     * @return Quantidade de proprietários.
     */
    public int countOwners(int idProperty) {
        String sql = "SELECT COUNT(*) FROM properties_users WHERE cdproperty = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProperty);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Verifica se o imóvel possui ao menos um contrato vigente vinculado
     * (qualquer status diferente de FINALIZADO — inclui ATIVO e INDETERMINADO).
     *
     * @param idProperty Identificador do imóvel.
     * @return {@code true} se existir pelo menos um contrato vigente.
     */
    public boolean hasActiveContract(int idProperty) {
        String sql = "SELECT 1 FROM contracts WHERE cdproperty = ? AND cdstatus <> ? LIMIT 1";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProperty);
            ps.setInt(2, model.ContractStatus.FINALIZADO.getCode());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar contratos ativos do imóvel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista apenas os imóveis com status "Disponível".
     * 
     * @return Lista de Strings formatadas.
     */
    public List<String> getAvailableOnly() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cdproperty, nrregistration, dsdescription FROM properties WHERE cdstatus = 2 ORDER BY cdproperty";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdproperty") + " | Reg: " + rs.getString("nrregistration") + " | " + rs.getString("dsdescription"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Verifica se um usuário já é proprietário de um imóvel.
     * 
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário.
     * @return true se o vínculo já existe.
     */
    public boolean hasAlreadyThisOwner(int idProperty, int idUser) {
        String sql = "SELECT 1 FROM properties_users WHERE cdproperty = ? AND cduser = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProperty);
            ps.setInt(2, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista os nomes dos proprietários vinculados a um imóvel.
     * 
     * @param idProp Identificador do imóvel.
     * @return Lista de nomes dos proprietários.
     */
    public List<String> getOwnersByProperty(int idProp) {
        List<String> owners = new ArrayList<>();
        String sql = "SELECT u.nmuser FROM users u " +
                     "JOIN properties_users pu ON u.cduser = pu.cduser " +
                     "WHERE pu.cdproperty = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProp);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    owners.add(rs.getString("nmuser"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return owners;
    }

    /**
     * Lista os IDs dos proprietários vinculados a um imóvel.
     * 
     * @param idProp Identificador do imóvel.
     * @return Lista de IDs dos proprietários.
     */
    public List<Integer> getOwnerIdsByProperty(int idProp) {
        List<Integer> owners = new ArrayList<>();
        String sql = "SELECT cduser FROM properties_users WHERE cdproperty = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProp);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    owners.add(rs.getInt("cduser"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return owners;
    }

    /**
     * Lista os imóveis que possuem pelo menos um proprietário vinculado.
     * 
     * @return Lista de Strings formatadas.
     */
    public List<String> getPropertiesWithOwners() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT p.cdproperty, p.nrregistration " +
                     "FROM properties p " +
                     "JOIN properties_users pu ON p.cdproperty = pu.cdproperty " +
                     "ORDER BY p.cdproperty";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdproperty") + " | Reg: " + rs.getString("nrregistration"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Verifica se um usuário é proprietário de um imóvel.
     * 
     * @param idProp Identificador do imóvel.
     * @param idUser Identificador do usuário.
     * @return true se o usuário é proprietário.
     */
    public boolean isUserOwner(int idProp, int idUser) {
        String sql = "SELECT 1 FROM properties_users WHERE cdproperty = ? AND cduser = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProp);
            ps.setInt(2, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Lista imóveis que podem ser vinculados (não estão com status "Vendido").
     * 
     * @return Lista de Strings formatadas.
     */
    public List<String> getLinkableProperties() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cdproperty, nrregistration FROM properties WHERE cdstatus <> 3 ORDER BY cdproperty";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add("ID: " + rs.getInt("cdproperty") + " | Reg: " + rs.getString("nrregistration"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Busca os detalhes completos de um imóvel, cruzando com tabelas auxiliares.
     * 
     * @param id Identificador do imóvel.
     * @return String formatada com os detalhes ou null.
     */
    public String findByIdDetalhado(int id) {
        String sql = "SELECT p.*, t.nmtype, f.nmpurpose, s.nmstatus, a.nmstreet, a.nraddress " + 
                     "FROM properties p " +
                     "JOIN property_types t ON p.cdtype = t.cdtype " +
                     "JOIN property_purposes f ON p.cdpurpose = f.cdpurpose " +
                     "JOIN property_status s ON p.cdstatus = s.cdstatus " +
                     "JOIN addresses a ON p.cdaddress = a.cdaddress " +
                     "WHERE p.cdproperty = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String endereco = rs.getString("nmstreet") + ", nº " + rs.getString("nraddress");
                double area = rs.getDouble("vltotalarea");

                return String.format(
                    "\n--- DETALHES DO IMÓVEL ---\n" +
                    "ID: %d | Matrícula: %s\n" +
                    "Descrição: %s\n" +
                    "Tipo: %s | Finalidade: %s\n" +
                    "Área: %.2fm² | Status: %s\n" +
                    "Endereço: %s",
                    rs.getInt("cdproperty"), rs.getString("nrregistration"),
                    rs.getString("dsdescription"), rs.getString("nmtype"),
                    rs.getString("nmpurpose"), area,
                    rs.getString("nmstatus"), endereco
                );
            }
        } catch (SQLException e) { 
            System.err.println("Erro na consulta detalhada: " + e.getMessage());
        }
        return null;
    }

    /**
     * Exibe no console um relatório completo de todos os imóveis cadastrados,
     * com opção de filtragem por bairro.
     *
     * @param filtroBairro Nome (ou parte) do bairro para filtrar. Use {@code null}
     *                     ou string vazia para listar todos os imóveis.
     */
    public void relatorioCompletoImoveis(String filtroBairro) {
        boolean comFiltro = filtroBairro != null && !filtroBairro.isBlank();

        String sql = "SELECT p.cdproperty, p.nrregistration, t.nmtype, s.nmstatus, " +
                     "a.nmstreet, a.nraddress, d.nmdistrict, c.nmcity " +
                     "FROM properties p " +
                     "JOIN property_types t ON p.cdtype = t.cdtype " +
                     "JOIN property_status s ON p.cdstatus = s.cdstatus " +
                     "JOIN addresses a ON p.cdaddress = a.cdaddress " +
                     "JOIN districts d ON a.cddistrict = d.cddistrict " +
                     "JOIN cities c ON d.cdcity = c.cdcity " +
                     (comFiltro ? "WHERE LOWER(d.nmdistrict) LIKE ? " : "") +
                     "ORDER BY d.nmdistrict, p.cdproperty";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (comFiltro) {
                ps.setString(1, "%" + filtroBairro.toLowerCase() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                String titulo = comFiltro
                        ? "RELATÓRIO: IMÓVEIS (Bairro contém \"" + filtroBairro + "\")"
                        : "RELATÓRIO: LISTAGEM GERAL DE IMÓVEIS";
                System.out.println("\n--- " + titulo + " ---");
                System.out.printf("%-3s | %-9s | %-14s | %-10s | %-18s | %-15s | %s%n",
                        "ID", "MATRÍCULA", "TIPO", "STATUS", "BAIRRO", "CIDADE", "ENDEREÇO");
                System.out.println("--------------------------------------------------------------------------------------------------------");

                boolean encontrou = false;
                while (rs.next()) {
                    encontrou = true;
                    String enderecoCompleto = rs.getString("nmstreet") + ", nº " + rs.getString("nraddress");
                    System.out.printf("%-3d | %-9s | %-14s | %-10s | %-18s | %-15s | %s%n",
                            rs.getInt("cdproperty"), rs.getString("nrregistration"),
                            rs.getString("nmtype"), rs.getString("nmstatus"),
                            rs.getString("nmdistrict"), rs.getString("nmcity"),
                            enderecoCompleto);
                }

                if (!encontrou) {
                    System.out.println(comFiltro
                            ? "Nenhum imóvel encontrado para o bairro informado."
                            : "Nenhum imóvel cadastrado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro no relatório: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga conveniente — lista todos os imóveis sem filtro de bairro.
     */
    public void relatorioCompletoImoveis() {
        relatorioCompletoImoveis(null);
    }
}