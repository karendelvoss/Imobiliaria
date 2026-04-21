package view;

import dao.*;
import model.*;
import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in, "UTF-8");
    private static UserDAO userDAO = new UserDAO();
    private static PropertyDAO propertyDAO = new PropertyDAO();
    private static ContractDAO contractDAO = new ContractDAO();
    //private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        int modulo = 0;
        while (modulo != 9) {
            System.out.println("\n========================================");
            System.out.println("     SISTEMA IMOBILIÁRIO INTEGRADO      ");
            System.out.println("========================================");
            System.out.println("1. MÓDULO DE CADASTROS (CRUD)");
            System.out.println("2. MÓDULO DE PROCESSOS DE NEGÓCIO");
            System.out.println("3. MÓDULO DE RELATÓRIOS");
            System.out.println("9. SAIR");
            System.out.print("Selecione o módulo: ");
            
            try { modulo = Integer.parseInt(sc.nextLine()); } catch (Exception e) { modulo = 0; }

            switch (modulo) {
                case 1: menuCRUD(); break;
                case 2: menuProcessos(); break;
                case 3: menuRelatorios(); break;
                case 9: System.out.println("Saindo..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void menuCRUD() {
        System.out.println("\n--- GESTÃO DE CADASTROS ---");
        System.out.println("1. Usuários   2. Imóveis");
        System.out.print("Escolha: ");
        int op = Integer.parseInt(sc.nextLine());
        if (op == 1) menuUsuarios();
        else menuImoveis();
    }

    private static void menuUsuarios() {
        System.out.println("\n--- SUBMENU: USUÁRIOS ---");
        System.out.println("1. Cadastrar 2. Consultar 3. Atualizar 4. Excluir");
        int op = Integer.parseInt(sc.nextLine());
        switch (op) {
            case 1: cadastrarUsuario(); break;
            case 2: consultarUsuario(); break;
            case 3: atualizarUsuario(); break;
            case 4: excluirUsuario(); break;
        }
    }

    private static void menuImoveis() {
        System.out.println("\n--- SUBMENU: IMÓVEIS ---");
        System.out.println("1. Cadastrar 2. Consultar 3. Atualizar 4. Excluir");
        int op = Integer.parseInt(sc.nextLine());
        switch (op) {
            case 1: cadastrarImovel(); break;
            case 2: consultarImovel(); break;
            case 3: atualizarImovel(); break;
            case 4: excluirImovel(); break;
        }
    }

    private static void menuProcessos() {
        System.out.println("\n--- PROCESSOS DE NEGÓCIO ---");
        System.out.println("1. EFETIVAR NOVO CONTRATO");
        System.out.println("2. VINCULAR PROPRIETÁRIO A IMÓVEL");
        int op = Integer.parseInt(sc.nextLine());
        if (op == 1) realizarContrato();
        else if (op == 2) vincularDonoImovel();
    }

    private static void menuRelatorios() {
        System.out.println("\n--- MÓDULO DE RELATÓRIOS ---");
        System.out.println("1. Imóveis por Bairro");
        System.out.println("2. Extrato Financeiro");
        System.out.println("3. Listagem Geral (JOINS)");
        int op = Integer.parseInt(sc.nextLine());
        if (op == 1) propertyDAO.relatorioImoveisPorBairro();
        else if (op == 2) relatorioFinanceiroContrato();
        else propertyDAO.relatorioCompletoImoveis();
    }

    private static void cadastrarUsuario() {
        Users u = new Users();
        System.out.print("Nome: "); u.setNmuser(sc.nextLine());
        System.out.print("Doc: "); u.setDocument(sc.nextLine());
        System.out.print("Celular: "); u.setNrcellphone(sc.nextLine());
        System.out.print("Data Nasc (AAAA-MM-DD): "); u.setDtbirth(LocalDate.parse(sc.nextLine()));
        System.out.print("ID Endereço: "); u.setCdaddress(Integer.parseInt(sc.nextLine()));
        System.out.print("ID Profissão: "); u.setCdoccupation(Integer.parseInt(sc.nextLine()));
        userDAO.saveUser(u, null);
    }

    private static void consultarUsuario() {
        System.out.print("ID do Usuário: ");
        Users u = userDAO.findById(Integer.parseInt(sc.nextLine()));
        if (u != null) System.out.println("Nome: " + u.getNmuser() + " | Doc: " + u.getDocument());
        else System.out.println("Não encontrado.");
    }

    private static void atualizarUsuario() {
        System.out.print("ID para atualizar: ");
        Users u = userDAO.findById(Integer.parseInt(sc.nextLine()));
        if (u != null) {
            System.out.print("Novo Nome (" + u.getNmuser() + "): ");
            String n = sc.nextLine(); if(!n.isEmpty()) u.setNmuser(n);
            userDAO.update(u);
        }
    }

    private static void excluirUsuario() {
        System.out.print("ID para EXCLUIR: ");
        int id = Integer.parseInt(sc.nextLine());
        String motivo = userDAO.verificarVinculos(id);
        if (motivo != null) System.out.println("IMPOSSÍVEL EXCLUIR! Motivo: " + motivo);
        else { userDAO.delete(id); System.out.println("Removido!"); }
    }

    private static void cadastrarImovel() {
        Properties p = new Properties();
        System.out.print("Matrícula: "); p.setNrregistration(sc.nextLine());
        System.out.print("Descrição: "); p.setDsdescription(sc.nextLine());
        System.out.print("Área Total (m²): "); p.setVltotalarea(Double.parseDouble(sc.nextLine()));
        System.out.print("ID Endereço: "); p.setCdaddress(Integer.parseInt(sc.nextLine()));
        System.out.print("ID Tipo: "); p.setCdtype(Integer.parseInt(sc.nextLine()));
        System.out.print("ID Finalidade: "); p.setCdpurpose(Integer.parseInt(sc.nextLine()));
        System.out.print("ID Status: "); p.setCdstatus(Integer.parseInt(sc.nextLine()));
        propertyDAO.insertProperty(p);
    }

    private static void consultarImovel() {
        System.out.print("ID do Imóvel: ");
        String info = propertyDAO.findByIdDetalhado(Integer.parseInt(sc.nextLine()));
        if (info != null) System.out.println(info);
        else System.out.println("ID não localizado.");
    }

    private static void atualizarImovel() {
        System.out.print("ID do imóvel: ");
        int id = Integer.parseInt(sc.nextLine());
        Properties p = propertyDAO.findById(id);
        if (p != null) {
            System.out.print("Nova Matrícula (" + p.getNrregistration() + "): ");
            String mat = sc.nextLine(); if(!mat.isEmpty()) p.setNrregistration(mat);
            System.out.print("Nova Descrição (" + p.getDsdescription() + "): ");
            String d = sc.nextLine(); if(!d.isEmpty()) p.setDsdescription(d);
            propertyDAO.updateProperty(p);
            System.out.println("Atualizado!");
        } else {
            System.out.println("Imóvel não encontrado.");
        }
    }

    private static void excluirImovel() {
        boolean saiu = false;
        while (!saiu) {
            System.out.print("\nID do imóvel para EXCLUIR: ");
            String input = sc.nextLine();
            if (input.isEmpty()) return;
            int id = Integer.parseInt(input);
            Properties p = propertyDAO.findById(id);

            if (p == null) {
                System.out.println("ERRO: O ID " + id + " não existe no sistema.");
                System.out.print("Deseja ver a lista de imóveis que PODEM ser excluídos? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) mostrarImoveisExcluiveis();
                else return;
                continue;
            }

            if (p.getCdstatus() != 1) {
                System.out.println("\nAVISO: O imóvel ID " + id + " faz parte de um contrato, não é permitida a exclusão!");
                System.out.print("Deseja ver a lista de imóveis aptos para exclusão? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) mostrarImoveisExcluiveis();
                else return;
            } else {
                System.out.print("Confirmar exclusão de '" + p.getNrregistration() + "'? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    propertyDAO.deleteProperty(id);
                    System.out.println("Imóvel removido!");
                    saiu = true;
                } else return;
            }
        }
    }

    private static void mostrarImoveisExcluiveis() {
        propertyDAO.getAvailableOnly().forEach(System.out::println);
    }

    private static void realizarContrato() {
        System.out.println("\n--- NOVO CONTRATO ---");
        System.out.print("ID Imóvel: "); int idP = Integer.parseInt(sc.nextLine());
        System.out.print("ID Cliente: "); int idU = Integer.parseInt(sc.nextLine());
        Contracts c = new Contracts();
        c.setDtcreation(LocalDate.now());
        System.out.print("Título: "); c.setDstitle(sc.nextLine());
        c.setCdproperty(idP);
        c.setCdindex(1);
        List<Installments> parcelas = new ArrayList<>();
        System.out.print("Valor: "); double v = Double.parseDouble(sc.nextLine());
        parcelas.add(criarParcela(1, v));
        List<User_Contract> partes = new ArrayList<>();
        User_Contract uc = new User_Contract(); uc.setCduser(idU); uc.setCdrole(2);
        partes.add(uc);
        contractDAO.processFullContract(c, partes, parcelas, null, 2);
    }

    private static Installments criarParcela(int num, double valor) {
        Installments inst = new Installments();
        inst.setNrinstallment(num); inst.setVlbase(valor);
        inst.setDtdue(LocalDate.now().plusMonths(num)); inst.setCdstatus(1);
        return inst;
    }

    private static void vincularDonoImovel() {
        System.out.print("ID Imóvel: "); int idP = Integer.parseInt(sc.nextLine());
        System.out.print("ID Usuário: "); int idU = Integer.parseInt(sc.nextLine());
        propertyDAO.linkOwner(idP, idU);
    }

    private static void relatorioFinanceiroContrato() {
        System.out.print("ID Contrato: ");
        contractDAO.gerarExtrato(Integer.parseInt(sc.nextLine()));
    }
}