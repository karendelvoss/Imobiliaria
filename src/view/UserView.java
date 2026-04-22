package view;

import dao.UserDAO;
import model.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static view.ConsoleIO.*;

/** Fluxos de UI para a entidade Usuário. */
public class UserView {

    private static final DateTimeFormatter DF_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserDAO userDAO;

    public UserView(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

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

    private void cadastrar() {
        Users u = new Users();
        u.setNmuser(ler("Nome: "));
        u.setDocument(ler("Doc: "));
        u.setNrcellphone(ler("Celular: "));
        u.setDtbirth(LocalDate.parse(ler("Data Nasc (AAAA-MM-DD): ")));
        u.setCdaddress(lerInt("ID Endereço: "));
        u.setCdoccupation(lerInt("ID Profissão: "));
        userDAO.saveUser(u, null);
    }

    private void consultar() {
        while (true) {
            String input = ler("\nID do Usuário para consulta detalhada: ");
            if (input.isEmpty()) return;
            Users u = userDAO.findById(Integer.parseInt(input));
            if (u != null) { imprimir(u); return; }
            if (confirmar("ERRO: Usuário não encontrado. Listar todos? (s/n): ")) {
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
        System.out.println("Documento:     " + u.getDocument() + " (Tipo: " + u.getFgdocument() + ")");
        System.out.println("Celular:       " + u.getNrcellphone());
        System.out.println("Nascimento:    " + u.getDtbirth().format(DF_BR));
        System.out.println("ID Endereço:   " + u.getCdaddress());
        System.out.println("ID Profissão:  " + u.getCdoccupation());
        System.out.println("========================================");
    }

    private void atualizar() {
        while (true) {
            String input = ler("\nID do usuário para atualizar: ");
            if (input.isEmpty()) return;
            Users u = userDAO.findById(Integer.parseInt(input));
            if (u == null) {
                if (confirmar("ID não encontrado. Ver lista? (s/n): ")) {
                    userDAO.getAllUsersList().forEach(System.out::println);
                } else return;
                continue;
            }
            System.out.println("Pressione ENTER para manter o valor atual.");
            u.setNmuser(lerOuManter("Nome", u.getNmuser()));
            u.setDocument(lerOuManter("Documento", u.getDocument()));
            u.setFgdocument(lerIntOuManter("Tipo Documento", u.getFgdocument()));
            u.setNrcellphone(lerOuManter("Celular", u.getNrcellphone()));
            String data = ler("Data Nasc (" + u.getDtbirth() + "): ");
            if (!data.isEmpty()) u.setDtbirth(LocalDate.parse(data));
            u.setCdaddress(lerIntOuManter("ID Endereço", u.getCdaddress()));
            u.setCdoccupation(lerIntOuManter("ID Profissão", u.getCdoccupation()));
            userDAO.update(u);
            System.out.println("Usuário atualizado com sucesso!");
            return;
        }
    }

    private void excluir() {
        while (true) {
            String input = ler("\nID do usuário para EXCLUIR: ");
            if (input.isEmpty()) return;
            int id = Integer.parseInt(input);
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
