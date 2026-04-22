package dao;

import model.Properties;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {

    // 1. Insert a new Property into the database
    public void insertProperty(Properties prop) {
        int proxId = 1;
        String sqlMax = "SELECT COALESCE(MAX(cdproperty), 0) + 1 AS prox_id FROM Properties";
        
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rsMax = st.executeQuery(sqlMax)) {
            if (rsMax.next()) {
                proxId = rsMax.getInt("prox_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO Properties (cdproperty, nrregistration, dsdescription, vltotalarea_, cdaddress, cdtype, cdpurpose, cdstatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, proxId);
            ps.setString(2, prop.getNrregistration());
            ps.setString(3, prop.getDsdescription());
            ps.setDouble(4, prop.getVltotalarea());
            ps.setInt(5, prop.getCdaddress());
            ps.setInt(6, prop.getCdtype());
            ps.setInt(7, prop.getCdpurpose());
            ps.setInt(8, prop.getCdstatus());
            
            ps.executeUpdate();
            System.out.println("Imóvel cadastrado com sucesso! (ID: " + proxId + ")");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

                

    // 2. Link an Owner (User) to a Property (N:N relationship)
    public void linkOwner(int idProperty, int idUser) {
        String sql = "INSERT INTO Properties_Users (cdproperty, cduser) VALUES (?, ?)";
        
        try (Connection conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProperty);
            ps.setInt(2, idUser);
            
            ps.executeUpdate();
            System.out.println("Owner successfully linked to the property!");
            
        } catch (SQLException e) {
            System.err.println("Error linking owner to property: " + e.getMessage());
        }
    }

    // Desvincular Proprietário de Imóvel (N:N relationship)
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

    // 3. List all Properties (Following the logical model columns)
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
                p.setVltotalarea(rs.getDouble("vltotalarea_"));
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

    // 4. Delete a Property
    public void deleteProperty(int id) {
        String sql = "DELETE FROM Properties WHERE cdproperty = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Property deleted!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Update 

    public void updateProperty(Properties prop) {
    // SQL completo com todos os campos da tabela
    String sql = "UPDATE Properties SET nrregistration = ?, dsdescription = ?, " +
                 "vltotalarea_ = ?, cdaddress = ?, cdtype = ?, cdpurpose = ?, " +
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
        ps.setInt(8, prop.getCdproperty()); // O ID do WHERE é o último
        
        ps.executeUpdate();
        System.out.println("Imóvel atualizado com sucesso no banco de dados!");
        
    } catch (SQLException e) {
        System.err.println("Erro ao atualizar imóvel: " + e.getMessage());
    }
}

/**
 * RELATÓRIO: Lista imóveis cruzando com as tabelas de Endereço, Bairro e Cidade.
 * Este método atende ao requisito de associar mais de uma tabela em um relatório.
 */

public void relatorioImoveisPorBairro() {
    // SQL com múltiplos JOINs para buscar a localização completa
    String sql = "SELECT p.cdproperty, p.nrregistration, p.dsdescription, " +
                 "d.nmdistrict, c.nmcity " +
                 "FROM Properties p " +
                 "JOIN Addresses a ON p.cdaddress = a.cdaddress " +
                 "JOIN Districts d ON a.cddistrict = d.cddistrict " +
                 "JOIN Cities c ON d.cdcity = c.cdcity " +
                 "ORDER BY d.nmdistrict";

    try (Connection conn = Conexao.getConexao();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        System.out.println("\n--- RELATÓRIO: IMÓVEIS POR LOCALIZAÇÃO ---");
        System.out.println("------------------------------------------------------------");
        
        boolean encontrou = false;
        while (rs.next()) {
            encontrou = true;
            System.out.printf("ID: %d | Reg: %s | Bairro: %-15s | Cidade: %s | Desc: %s\n",
                    rs.getInt("cdproperty"),
                    rs.getString("nrregistration"),
                    rs.getString("nmdistrict"),
                    rs.getString("nmcity"),
                    rs.getString("dsdescription"));
        }
        
        if (!encontrou) {
            System.out.println("Nenhum imóvel encontrado no banco de dados.");
        }
        System.out.println("------------------------------------------------------------");

    } catch (SQLException e) {
        System.err.println("Erro ao gerar relatório de bairros: " + e.getMessage());
    }
}

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

// Método para buscar um imóvel específico pelo ID (Necessário para a validação no Main)
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
                p.setVltotalarea(rs.getDouble("vltotalarea_"));
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

public List<String> getAvailableOnly() {
    List<String> list = new ArrayList<>();
    // Filtramos pelo status 2 (Disponível) conforme seu banco
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

public boolean hasAlreadyThisOwner(int idProperty, int idUser) {
    String sql = "SELECT 1 FROM properties_users WHERE cdproperty = ? AND cduser = ?";
    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idProperty);
        ps.setInt(2, idUser);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next(); // Se houver um resultado, o vínculo já existe
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
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

public List<String> getLinkableProperties() {
    List<String> list = new ArrayList<>();
    // Filtra para trazer apenas o que não é status 3 (Vendido)
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
            
            double area = rs.getDouble("vltotalarea_");

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

public void relatorioCompletoImoveis() {
    // Ajustado também para evitar erro no módulo de relatórios
    String sql = "SELECT p.cdproperty, p.nrregistration, t.nmtype, s.nmstatus, a.nmstreet, a.nraddress " +
                 "FROM properties p " +
                 "JOIN property_types t ON p.cdtype = t.cdtype " +
                 "JOIN property_status s ON p.cdstatus = s.cdstatus " +
                 "JOIN addresses a ON p.cdaddress = a.cdaddress ORDER BY p.cdproperty";
    try (Connection conn = Conexao.getConexao();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        System.out.println("\nID  | MATRÍCULA | TIPO           | STATUS     | ENDEREÇO COMPLETO");
        System.out.println("--------------------------------------------------------------------------------------");
        while (rs.next()) {
            String enderecoCompleto = rs.getString("nmstreet") + ", nº " + rs.getString("nraddress");
            System.out.printf("%-3d | %-9s | %-14s | %-10s | %s\n", 
                rs.getInt("cdproperty"), rs.getString("nrregistration"), 
                rs.getString("nmtype"), rs.getString("nmstatus"), enderecoCompleto);
        }
    } catch (SQLException e) { 
        System.err.println("Erro no relatório: " + e.getMessage());
    }
}

}