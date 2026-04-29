package view;

import dao.UserDAO;
import dao.Conexao;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static view.ConsoleIO.*;

/**
 * Interface de console para gestão de usuários.
 */
public class UserView {

    private static final DateTimeFormatter DF_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserDAO userDAO;

    public UserView(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Menu principal para operações com usuários.
     */
    public void menu() {
        System.out.println("\n--- SUBMENU: USUÁRIOS ---");
        System.out.println("1. Cadastrar 2. Consultar 3. Atualizar 4. Excluir");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: cadastrar(); break;
            case 2: consultar(); break;
            case 3: atualizar(); break;
            case 4: excluir(); break;
            default: System.out.println("Opção inválida.");
        }
    }

    /**
     * Fluxo de cadastro de um novo usuário.
     * 
     * @return ID do usuário criado ou -1 se cancelado.
     */
    public int cadastrar() {
        Users u = new Users();
        u.setNmuser(ler("Nome: "));
        
        while (true) {
            String doc = ler("Doc (Apenas 11 números): ");
            if (doc.matches("\\d{11}")) {
                u.setDocument(doc);
                u.setFgdocument(true);
                break;
            }
            System.out.println("ERRO: O documento deve conter exatamente 11 números!");
        }

        u.setDsissuingbody(ler("Órgão Emissor do Documento: "));
        
        while (true) {
            String cel = ler("Celular (Ex: 11 98888-7777): ");
            if (cel.matches("\\d{2,3} \\d{4,5}-\\d{4}")) {
                u.setNrcellphone(cel);
                break;
            }
            System.out.println("ERRO: Formato de celular inválido.");
        }
        
        while (true) {
            String dataStr = ler("Data Nasc (AAAA-MM-DD): ");
            try {
                u.setDtbirth(LocalDate.parse(dataStr.replace("/", "-")));
                break;
            } catch (Exception e) {
                System.out.println("ERRO: Data inválida!");
            }
        }
        
        int idAdd = lerIdValido("ID Endereço (0 para cancelar)", 
                id -> checkAddressExists(id) ? id : null, 
                this::listAddresses);
        if (idAdd == -1) return -1;
        u.setCdaddress(idAdd);

        int idOcc = lerIdValido("ID Profissão (0 para cancelar)", 
                id -> checkOccupationExists(id) ? id : null, 
                this::listOccupations);
        if (idOcc == -1) return -1;
        u.setCdoccupation(idOcc);
        
        userDAO.saveUser(u, null);
        return u.getCduser();
    }

    private void consultar() {
        while (true) {
            String input = ler("\nID do Usuário para consulta detalhada: ").trim();
            if (input.isEmpty()) return;

            int id;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro ou pressione ENTER para voltar.");
                continue;
            }

            Users u = userDAO.findById(id);
            if (u != null) {
                imprimir(u);
                return;
            }
            if (confirmar("Usuário não encontrado. Listar todos? (s/n): ")) {
                userDAO.getAllUsersList().forEach(System.out::println);
            } else return;
        }
    }

    private void imprimir(Users u) {
        System.out.println("\n========================================");
        System.out.println("           DADOS DO USUÁRIO             ");
        System.out.println("========================================");
        System.out.println("ID:            " + u.getCduser());
        System.out.println("Nome:          " + u.getNmuser());
        System.out.println("Documento:     " + u.getDocument() + " (CPF: " + (u.isFgdocument() ? "Sim" : "Não") + ")");
        System.out.println("Celular:       " + u.getNrcellphone());
        System.out.println("Nascimento:    " + u.getDtbirth().format(DF_BR));
        System.out.println("ID Endereço:   " + u.getCdaddress());
        System.out.println("ID Profissão:  " + u.getCdoccupation());
        System.out.println("========================================");
    }

    private void atualizar() {
        while (true) {
            String input = ler("\nID do usuário para atualizar: ").trim();
            if (input.isEmpty()) return;
            int idUsuario;
            try {
                idUsuario = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro ou pressione ENTER para voltar.");
                continue;
            }
            Users u = userDAO.findById(idUsuario);
            if (u == null) {
                if (confirmar("ID não encontrado. Ver lista? (s/n): ")) {
                    userDAO.getAllUsersList().forEach(System.out::println);
                } else return;
                continue;
            }
            System.out.println("Pressione ENTER para manter o valor atual.");
            u.setNmuser(lerOuManter("Nome", u.getNmuser()));
            
            while (true) {
                String doc = lerOuManter("Documento", u.getDocument());
                if (doc.matches("\\d{11}")) {
                    u.setDocument(doc);
                    break;
                }
                System.out.println("ERRO: O documento deve conter exatamente 11 números!");
            }
            
            u.setFgdocument(confirmar("É CPF? (s/n, atual: " + (u.isFgdocument() ? "Sim" : "Não") + "): "));
            
            while (true) {
                String cel = lerOuManter("Celular", u.getNrcellphone());
                if (cel.matches("\\d{2,3} \\d{4,5}-\\d{4}")) {
                    u.setNrcellphone(cel);
                    break;
                }
                System.out.println("ERRO: Formato de celular inválido. Siga o exemplo com DDD, espaço e traço.");
            }
            
            while (true) {
                String data = ler("Data Nasc (" + u.getDtbirth() + "): ");
                if (data.isEmpty()) break;
                try {
                    u.setDtbirth(LocalDate.parse(data.replace("/", "-")));
                    break;
                } catch (Exception e) {
                    System.out.println("ERRO: Data inválida! Use o formato AAAA-MM-DD ou AAAA/MM/DD (Ex: 1990-12-31).");
                }
            }
            
            u.setCdaddress(lerIdOuManter("ID Endereço", u.getCdaddress(),
                    id -> checkAddressExists(id) ? id : null,
                    this::listAddresses));
                    
            u.setCdoccupation(lerIdOuManter("ID Profissão", u.getCdoccupation(),
                    id -> checkOccupationExists(id) ? id : null,
                    this::listOccupations));
                    
            userDAO.update(u);
            System.out.println("Usuário atualizado com sucesso!");
            return;
        }
    }

    private void excluir() {
        while (true) {
            String input = ler("\nID do usuário para EXCLUIR: ").trim();
            if (input.isEmpty()) return;
            int id;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro ou pressione ENTER para voltar.");
                continue;
            }
            Users u = userDAO.findById(id);
            if (u == null) {
                System.out.println("ERRO: O ID " + id + " não existe no sistema.");
                if (confirmar("Deseja ver a lista de usuários aptos para exclusão? (s/n): ")) {
                    mostrarExcluiveis();
                }
                return;
            }
            String motivo = userDAO.verificarVinculos(id);
            if (motivo != null) {
                System.out.println("\nIMPOSSÍVEL EXCLUIR! Motivo: " + motivo);
                if (confirmar("Ver usuários SEM vínculos? (s/n): ")) mostrarExcluiveis();
                return;
            }
            if (confirmar("Confirmar exclusão definitiva do usuário '" + u.getNmuser() + "'? (s/n): ")) {
                userDAO.delete(id);
                System.out.println("Usuário removido com sucesso!");
            }
            return;
        }
    }

    private void mostrarExcluiveis() {
        List<String> aptos = userDAO.getDeletableUsers();
        if (aptos.isEmpty()) {
            System.out.println("Aviso: Todos os usuários cadastrados possuem vínculos ativos.");
        } else {
            System.out.println("\n--- USUÁRIOS SEM VÍNCULOS (APTOS PARA EXCLUSÃO) ---");
            aptos.forEach(System.out::println);
        }
    }

    private boolean checkAddressExists(int id) {
        String sql = "SELECT 1 FROM Addresses WHERE cdaddress = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void listAddresses() {
        String sql = "SELECT cdaddress, nmstreet, nraddress FROM Addresses ORDER BY cdaddress";
        System.out.println("\n--- ENDEREÇOS DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdaddress") + " | " + rs.getString("nmstreet") + ", " + rs.getString("nraddress"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar endereços.");
        }
    }

    private boolean checkOccupationExists(int id) {
        String sql = "SELECT 1 FROM Occupations WHERE cdoccupation = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void listOccupations() {
        String sql = "SELECT cdoccupation, nmoccupation FROM Occupations ORDER BY cdoccupation";
        System.out.println("\n--- PROFISSÕES DISPONÍVEIS ---");
        try (Connection conn = Conexao.getConexao();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("cdoccupation") + " | " + rs.getString("nmoccupation"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar profissões.");
        }
    }
}
