package view;

import dao.UserDAO;
import model.Users;

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
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- SUBMENU: USUÁRIOS ---");
            System.out.println("1. Cadastrar 2. Listar 3. Consultar 4. Atualizar 5. Excluir 0. Voltar");
            op = lerIntSeguro("Escolha: ");
            switch (op) {
                case 1: cadastrar(); break;
                case 2: listar(); break;
                case 3: consultar(); break;
                case 4: atualizar(); break;
                case 5: excluir(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Lista todos os usuários cadastrados.
     */
    private void listar() {
        List<Users> usuarios = userDAO.listAllUsers();
        if (usuarios.isEmpty()) {
            System.out.println("\nNenhum usuário cadastrado.");
            if (confirmar("Deseja cadastrar um novo? (s/n): ")) {
                cadastrar();
            }
            return;
        }
        System.out.println("\n--- LISTA DE USUÁRIOS ---");
        for (Users u : usuarios) {
            System.out.printf("ID: %d | Nome: %s | CPF: %s | Celular: %s | Profissão: %s%n",
                u.getCduser(), u.getNmuser(), u.getDocument(),
                u.getNrcellphone(), u.getOccupation() != null ? u.getOccupation() : "N/A");
        }
    }

    /**
     * Fluxo de cadastro de um novo usuário.
     * 
     * @return ID do usuário criado ou -1 se cancelado.
     */
    public int cadastrar() {
        Users u = new Users();
        
        // Nome: obrigatório, mínimo 3 chars, sem números
        while (true) {
            String nome = ler("Nome: ").trim();
            if (nome.length() < 3) {
                System.out.println("ERRO: O nome deve ter no mínimo 3 caracteres.");
                continue;
            }
            if (nome.matches(".*\\d.*")) {
                System.out.println("ERRO: O nome não pode conter números.");
                continue;
            }
            u.setNmuser(nome);
            break;
        }
        
        // Documento (CPF): 11 dígitos + validação dos dígitos verificadores
        while (true) {
            String doc = ler("CPF (apenas 11 números): ").replaceAll("[^0-9]", "");
            if (doc.length() != 11) {
                System.out.println("ERRO: O CPF deve conter exatamente 11 números.");
                continue;
            }
            if (!validarCPF(doc)) {
                System.out.println("ERRO: CPF inválido (dígitos verificadores incorretos).");
                continue;
            }
            u.setDocument(doc);
            u.setFgdocument(true);
            break;
        }

        // Órgão emissor: obrigatório
        while (true) {
            String orgao = ler("Órgão Emissor (Ex: SSP/SC): ").trim();
            if (orgao.isEmpty()) {
                System.out.println("ERRO: O órgão emissor é obrigatório.");
                continue;
            }
            u.setDsissuingbody(orgao);
            break;
        }
        
        // Celular: aceita formatos variados, normaliza
        while (true) {
            String cel = ler("Celular (Ex: 47 99999-8888): ").trim();
            String celLimpo = cel.replaceAll("[^0-9]", "");
            if (celLimpo.length() < 10 || celLimpo.length() > 11) {
                System.out.println("ERRO: O celular deve ter 10 ou 11 dígitos (DDD + número).");
                continue;
            }
            // Normaliza para formato "DD NNNNN-NNNN"
            if (cel.matches("\\d{2,3}\\s?\\d{4,5}[-\\s]?\\d{4}") || celLimpo.length() >= 10) {
                u.setNrcellphone(cel);
                break;
            }
            System.out.println("ERRO: Formato de celular inválido. Ex: 47 99999-8888");
        }
        
        // Data de nascimento: válida, não futura, idade entre 0 e 150
        while (true) {
            String dataStr = ler("Data Nasc (AAAA-MM-DD): ").trim();
            try {
                LocalDate dt = LocalDate.parse(dataStr.replace("/", "-"));
                if (dt.isAfter(LocalDate.now())) {
                    System.out.println("ERRO: A data de nascimento não pode ser no futuro.");
                    continue;
                }
                int idade = LocalDate.now().getYear() - dt.getYear();
                if (idade > 150) {
                    System.out.println("ERRO: Data improvável (mais de 150 anos).");
                    continue;
                }
                u.setDtbirth(dt);
                break;
            } catch (Exception e) {
                System.out.println("ERRO: Data inválida! Use o formato AAAA-MM-DD (Ex: 1990-05-20).");
            }
        }
        
        // Endereço embarcado com validações
        System.out.println("\n--- ENDEREÇO DO USUÁRIO ---");
        model.Addresses endereco = lerEnderecoValidado();

        // Profissão: obrigatória
        String profissao;
        while (true) {
            profissao = ler("Profissão: ").trim();
            if (profissao.isEmpty()) {
                System.out.println("ERRO: A profissão é obrigatória.");
                continue;
            }
            break;
        }
        
        userDAO.saveUserWithAddress(u, endereco, profissao, null);
        return u.getCduser();
    }

    /**
     * Coleta os dados de endereço com validações.
     */
    public static model.Addresses lerEnderecoValidado() {
        model.Addresses a = new model.Addresses();
        
        // CEP: 8 dígitos
        while (true) {
            String cep = ler("CEP (8 dígitos): ").replaceAll("[^0-9]", "");
            if (cep.length() != 8) {
                System.out.println("ERRO: O CEP deve ter exatamente 8 dígitos.");
                continue;
            }
            a.setCdzipcode(cep);
            break;
        }
        
        // Rua: obrigatória
        while (true) {
            String rua = ler("Rua: ").trim();
            if (rua.isEmpty()) { System.out.println("ERRO: A rua é obrigatória."); continue; }
            a.setNmstreet(rua);
            break;
        }
        
        // Número: deve ser numérico ou "S/N"
        while (true) {
            String num = ler("Número: ").trim();
            if (num.isEmpty()) { System.out.println("ERRO: O número é obrigatório."); continue; }
            if (!num.matches("\\d+") && !num.equalsIgnoreCase("S/N")) {
                System.out.println("ERRO: Digite um número válido (Ex: 100) ou S/N se não houver.");
                continue;
            }
            a.setNraddress(num);
            break;
        }
        
        a.setDscomplement(ler("Complemento (ou ENTER para pular): ").trim());
        
        // Bairro: obrigatório
        while (true) {
            String bairro = ler("Bairro: ").trim();
            if (bairro.isEmpty()) { System.out.println("ERRO: O bairro é obrigatório."); continue; }
            a.setDistrict(bairro);
            break;
        }
        
        // Cidade: obrigatória
        while (true) {
            String cidade = ler("Cidade: ").trim();
            if (cidade.isEmpty()) { System.out.println("ERRO: A cidade é obrigatória."); continue; }
            a.setCity(cidade);
            break;
        }
        
        // Estado: 2 letras
        while (true) {
            String estado = ler("Estado (UF, Ex: SC): ").trim().toUpperCase();
            if (!estado.matches("[A-Z]{2}")) { System.out.println("ERRO: O estado deve ter 2 letras (Ex: SC, SP, RJ)."); continue; }
            a.setState(estado);
            break;
        }
        
        // País: obrigatório
        while (true) {
            String pais = ler("País: ").trim();
            if (pais.isEmpty()) { System.out.println("ERRO: O país é obrigatório."); continue; }
            a.setCountry(pais);
            break;
        }
        
        return a;
    }

    /**
     * Coleta os dados de endereço do console (versão simplificada sem validações rígidas).
     */
    public static model.Addresses lerEndereco() {
        return lerEnderecoValidado();
    }

    /**
     * Valida os dígitos verificadores de um CPF.
     */
    private static boolean validarCPF(String cpf) {
        if (cpf.length() != 11) return false;
        // Rejeita CPFs com todos os dígitos iguais (ex: 11111111111)
        if (cpf.chars().distinct().count() == 1) return false;
        
        // Calcula primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int d1 = 11 - (soma % 11);
        if (d1 >= 10) d1 = 0;
        if (d1 != (cpf.charAt(9) - '0')) return false;
        
        // Calcula segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int d2 = 11 - (soma % 11);
        if (d2 >= 10) d2 = 0;
        return d2 == (cpf.charAt(10) - '0');
    }

    private void consultar() {
        String input = ler("\nDigite o ID do usuário para consultar (0 para voltar): ").trim();
        if (input.equals("0") || input.isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return;
        }

        Users u = userDAO.findById(id);
        if (u != null) {
            imprimir(u);
        } else {
            System.out.println("Usuário com ID " + id + " não encontrado.");
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
        System.out.println("Profissão:     " + (u.getOccupation() != null ? u.getOccupation() : "N/A"));
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
            
            // Atualizar endereço embarcado
            if (confirmar("Atualizar endereço? (s/n): ")) {
                model.Addresses endereco = lerEndereco();
                userDAO.updateAddress(u.getCduser(), endereco);
            }

            String profAtual = userDAO.getOccupation(u.getCduser());
            String novaProfissao = lerOuManter("Profissão", profAtual != null ? profAtual : "");
            userDAO.updateOccupation(u.getCduser(), novaProfissao);
                    
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

}
