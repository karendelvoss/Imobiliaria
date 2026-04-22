package view;

import dao.*;
import model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in, "UTF-8");
    private static UserDAO userDAO = new UserDAO();
    private static PropertyDAO propertyDAO = new PropertyDAO();
    private static ContractDAO contractDAO = new ContractDAO();
    private static CountryDAO countryDAO = new CountryDAO();
    private static CityDAO cityDAO = new CityDAO();
    private static DistrictDAO districtDAO = new DistrictDAO();
    private static PropertyTypeDAO propertyTypeDAO = new PropertyTypeDAO();
    private static PropertyPurposeDAO propertyPurposeDAO = new PropertyPurposeDAO();
    private static PropertyStatusDAO propertyStatusDAO = new PropertyStatusDAO();
    private static ContractTemplateDAO contractTemplateDAO = new ContractTemplateDAO();
    private static ClauseDAO clauseDAO = new ClauseDAO();
    private static IndexDAO indexDAO = new IndexDAO();
    private static RoleDAO roleDAO = new RoleDAO();
    private static BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private static NotificationDAO notificationDAO = new NotificationDAO();
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
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- GESTÃO DE CADASTROS (CRUD) ---");
            System.out.println("1. Usuários");
            System.out.println("2. Imóveis");
            System.out.println("3. Localização (Países, Cidades, Bairros)");
            System.out.println("4. Domínios de Imóveis (Tipos, Finalidades, Status)");
            System.out.println("5. Domínios Contratuais (Modelos, Cláusulas, Índices, Papéis)");
            System.out.println("6. Outros (Contas Bancárias, Notificações)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            try { op = Integer.parseInt(sc.nextLine()); } catch (Exception e) { op = -1; }

            switch (op) {
                case 1: menuUsuarios(); break;
                case 2: menuImoveis(); break;
                case 3: menuLocalizacao(); break;
                case 4: menuAtributosImoveis(); break;
                case 5: menuAtributosContratuais(); break;
                case 6: menuOutros(); break;
                case 0: break;
                default: System.out.println("Opção inválida!");
            }
        }
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
        System.out.println("3. DESVINCULAR PROPRIETÁRIO DE IMÓVEL");
        System.out.println("4. ALTERAR CONTRATO");
        System.out.println("5. EXCLUIR CONTRATO");
        int op = Integer.parseInt(sc.nextLine());
        if (op == 1) realizarContrato();
        else if (op == 2) vincularDonoImovel();
        else if (op == 3) desvincularDonoImovel();
        else if (op == 4) alterarContrato();
        else if (op == 5) excluirContrato();
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
        boolean encontrou = false;
        while (!encontrou) {
            System.out.print("\nID do Usuário para consulta detalhada: ");
            String input = sc.nextLine();
            if (input.isEmpty()) break;
            
            int id = Integer.parseInt(input);
            Users u = userDAO.findById(id); 

            if (u != null) {
                System.out.println("\n========================================");
                System.out.println("           DADOS DO USUÁRIO             ");
                System.out.println("========================================");
                System.out.println("ID:            " + u.getCduser());
                System.out.println("Nome:          " + u.getNmuser());
                System.out.println("Documento:     " + u.getDocument() + " (Tipo: " + u.getFgdocument() + ")");
                System.out.println("Celular:       " + u.getNrcellphone());
                System.out.println("Nascimento:    " + u.getDtbirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                System.out.println("ID Endereço:   " + u.getCdaddress());
                System.out.println("ID Profissão:  " + u.getCdoccupation());
                System.out.println("========================================");
                encontrou = true;
            } else {
                System.out.print("ERRO: Usuário não encontrado. Listar todos? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    userDAO.getAllUsersList().forEach(System.out::println);
                } else break;
            }
        }
    }

private static void atualizarUsuario() {
        boolean encontrou = false;
        while (!encontrou) {
            System.out.print("\nID do usuário para atualizar: ");
            String input = sc.nextLine();
            if (input.isEmpty()) return;
            
            int id = Integer.parseInt(input);
            Users u = userDAO.findById(id);

            if (u != null) {
                System.out.println("Pressione ENTER para manter o valor atual.");
                
                System.out.print("Nome (" + u.getNmuser() + "): ");
                String nome = sc.nextLine();
                if (!nome.isEmpty()) u.setNmuser(nome);

                System.out.print("Documento (" + u.getDocument() + "): ");
                String doc = sc.nextLine();
                if (!doc.isEmpty()) u.setDocument(doc);

                System.out.print("Tipo Documento (" + u.getFgdocument() + "): ");
                String fg = sc.nextLine();
                if (!fg.isEmpty()) u.setFgdocument(Integer.parseInt(fg));

                System.out.print("Celular (" + u.getNrcellphone() + "): ");
                String cel = sc.nextLine();
                if (!cel.isEmpty()) u.setNrcellphone(cel);

                System.out.print("Data Nasc (" + u.getDtbirth() + "): ");
                String data = sc.nextLine();
                if (!data.isEmpty()) u.setDtbirth(LocalDate.parse(data));

                System.out.print("ID Endereço (" + u.getCdaddress() + "): ");
                String end = sc.nextLine();
                if (!end.isEmpty()) u.setCdaddress(Integer.parseInt(end));

                System.out.print("ID Profissão (" + u.getCdoccupation() + "): ");
                String prof = sc.nextLine();
                if (!prof.isEmpty()) u.setCdoccupation(Integer.parseInt(prof));

                userDAO.update(u);
                System.out.println("Usuário atualizado com sucesso!");
                encontrou = true;
            } else {
                System.out.print("ID não encontrado. Ver lista? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    userDAO.getAllUsersList().forEach(System.out::println);
                } else break;
            }
        }
    }

private static void excluirUsuario() {
        boolean saiu = false;
        while (!saiu) {
            System.out.print("\nID do usuário para EXCLUIR: ");
            String input = sc.nextLine();
            if (input.isEmpty()) return;
            
            int id = Integer.parseInt(input);
            Users u = userDAO.findById(id);

            if (u == null) {
                System.out.println("ERRO: O ID " + id + " não existe no sistema.");
                System.out.print("Deseja ver a lista de usuários aptos para exclusão? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    mostrarUsuariosExcluiveis();
                } else return;
                continue;
            }

            String motivo = userDAO.verificarVinculos(id);
            if (motivo != null) {
                System.out.println("\nIMPOSSÍVEL EXCLUIR! Motivo: " + motivo);
                System.out.print("Deseja ver os usuários que NÃO possuem vínculos e podem ser excluídos? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    mostrarUsuariosExcluiveis();
                } else return;
            } else {
                System.out.print("Confirmar exclusão definitiva do usuário '" + u.getNmuser() + "'? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    userDAO.delete(id);
                    System.out.println("Usuário removido com sucesso!");
                    saiu = true;
                } else return;
            }
        }
    }

    private static void mostrarUsuariosExcluiveis() {
        List<String> aptos = userDAO.getDeletableUsers();
        if (aptos.isEmpty()) {
            System.out.println("Aviso: Todos os usuários cadastrados possuem vínculos ativos.");
        } else {
            System.out.println("\n--- USUÁRIOS SEM VÍNCULOS (APTOS PARA EXCLUSÃO) ---");
            aptos.forEach(System.out::println);
        }
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
        int idP = -1;
        while (true) {
            System.out.print("ID Imóvel (apenas disponíveis, 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idP = Integer.parseInt(input);
                Properties p = propertyDAO.findById(idP);
                if (p != null && p.getCdstatus() == 1) {
                    break;
                } else {
                    System.out.print("ERRO: Imóvel não encontrado ou indisponível. Ver lista de imóveis aptos? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) propertyDAO.getAvailableOnly().forEach(System.out::println);
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }

        int idU = -1;
        while (true) {
            System.out.print("ID Cliente (Locatário/Comprador, 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idU = Integer.parseInt(input);
                if (userDAO.findById(idU) != null) break;
                else {
                    System.out.print("ERRO: Cliente não encontrado. Ver lista de clientes? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) userDAO.getAllUsersList().forEach(System.out::println);
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }

        Contracts c = new Contracts();
        c.setDtcreation(LocalDate.now());
        System.out.print("Título: "); c.setDstitle(sc.nextLine());
        
        int tpl = -1;
        while (true) {
            System.out.print("ID do Modelo de Contrato (Template, 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                tpl = Integer.parseInt(input);
                if (contractTemplateDAO.findById(tpl) != null) {
                    c.setCdtemplate(tpl);
                    break;
                } else {
                    System.out.print("ERRO: Modelo não encontrado. Ver lista de modelos? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        contractTemplateDAO.listAll().forEach(t -> 
                            System.out.println("ID: " + t.getCdtemplate() + " | Nome: " + t.getNmtemplate())
                        );
                    }
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }

        System.out.print("Tipo de Negócio (1=Locação/Parcelas Fixas, 2=Venda/Parcelas Personalizadas): ");
        int tipoNegocio = Integer.parseInt(sc.nextLine());
        
        c.setCdproperty(idP);
        
        int idIndex = -1;
        while (true) {
            System.out.print("ID do Índice de Reajuste (0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idIndex = Integer.parseInt(input);
                if (indexDAO.findById(idIndex) != null) {
                    c.setCdindex(idIndex);
                    break;
                } else {
                    System.out.print("ERRO: Índice não encontrado. Ver lista de índices? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        indexDAO.listAll().forEach(i -> 
                            System.out.println("ID: " + i.getCdindex() + " | Nome: " + i.getNmindex())
                        );
                    }
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }
        
        List<Installments> parcelas = new ArrayList<>();
        
        System.out.print("Quantidade de Parcelas: "); int qtd = Integer.parseInt(sc.nextLine());
        
        if (tipoNegocio == 1) {
            System.out.print("Valor de cada parcela: "); double v = Double.parseDouble(sc.nextLine());
            for (int i = 1; i <= qtd; i++) parcelas.add(criarParcela(i, v));
        } else {
            System.out.println("Modo Venda/Personalizado. Preencha o valor de cada parcela:");
            for (int i = 1; i <= qtd; i++) {
                System.out.print("Valor da Parcela " + i + ": ");
                double v = Double.parseDouble(sc.nextLine());
                parcelas.add(criarParcela(i, v));
            }
        }

        List<User_Contract> partes = new ArrayList<>();
        
        // 1. Vincular Proprietários automaticamente
        List<Integer> donos = propertyDAO.getOwnerIdsByProperty(idP);
        for (int idDono : donos) {
            User_Contract ud = new User_Contract(); ud.setCduser(idDono); ud.setCdrole(1);
            partes.add(ud);
        }
        
        // 2. Vincular Cliente (Locatário ou Comprador)
        User_Contract uc = new User_Contract(); uc.setCduser(idU); uc.setCdrole(tipoNegocio == 1 ? 2 : 4);
        partes.add(uc);
        
        // 3. Atualizar Status (2 = Alugado, 3 = Vendido)
        int novoStatus = (tipoNegocio == 1) ? 2 : 3;
        contractDAO.processFullContract(c, partes, parcelas, null, novoStatus);
    }

    private static Installments criarParcela(int num, double valor) {
        Installments inst = new Installments();
        inst.setNrinstallment(num); inst.setVlbase(valor);
        inst.setDtdue(LocalDate.now().plusMonths(num)); inst.setCdstatus(1);
        return inst;
    }

    private static void vincularDonoImovel() {
        System.out.println("\n--- VINCULAR PROPRIETÁRIO A IMÓVEL ---");
        int idP = -1;
        while (true) {
            System.out.print("ID Imóvel (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idP = Integer.parseInt(input);
                if (propertyDAO.findById(idP) != null) {
                    break;
                } else {
                    System.out.print("ERRO: Imóvel não encontrado. Deseja ver a lista de imóveis disponíveis? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        propertyDAO.getAvailableProperties().forEach(System.out::println);
                    }
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }

        int idU = -1;
        while (true) {
            System.out.print("ID Usuário Proprietário (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idU = Integer.parseInt(input);
                if (userDAO.findById(idU) != null) {
                    break;
                } else {
                    System.out.print("ERRO: Usuário não encontrado. Deseja ver a lista de usuários? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        userDAO.getAllUsersList().forEach(System.out::println);
                    }
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }

        if (propertyDAO.hasAlreadyThisOwner(idP, idU)) {
            System.out.println("\nAVISO: Este usuário já está vinculado como proprietário deste imóvel.");
            return;
        }

        propertyDAO.linkOwner(idP, idU);
    }

    private static void desvincularDonoImovel() {
        System.out.println("\n--- DESVINCULAR PROPRIETÁRIO DE IMÓVEL ---");
        int idP = -1;
        while (true) {
            System.out.print("ID Imóvel (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idP = Integer.parseInt(input);
                if (propertyDAO.findById(idP) != null) {
                    break;
                } else {
                    System.out.print("ERRO: Imóvel não encontrado. Deseja ver a lista de imóveis com proprietários vinculados? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        List<String> imoveis = propertyDAO.getPropertiesWithOwners();
                        if (imoveis.isEmpty()) {
                            System.out.println("AVISO: Nenhum imóvel possui proprietários vinculados no momento.");
                            return;
                        } else {
                            imoveis.forEach(System.out::println);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }

        int idU = -1;
        while (true) {
            System.out.print("ID Usuário Proprietário (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                idU = Integer.parseInt(input);
                if (propertyDAO.hasAlreadyThisOwner(idP, idU)) {
                    break;
                } else {
                    System.out.print("ERRO: Este usuário não é proprietário deste imóvel. Deseja ver a lista de proprietários vinculados? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        List<String> donos = propertyDAO.getOwnersByProperty(idP);
                        if (donos.isEmpty()) {
                            System.out.println("AVISO: Nenhum proprietário vinculado a este imóvel.");
                            return;
                        } else {
                            donos.forEach(System.out::println);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }

        System.out.print("Confirmar desvinculação? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            propertyDAO.unlinkOwner(idP, idU);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void alterarContrato() {
        System.out.println("\n--- ALTERAR CONTRATO ---");
        while (true) {
            System.out.print("ID Contrato (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                int idC = Integer.parseInt(input);
                Contracts c = contractDAO.findById(idC);
                if (c != null) {
                    System.out.println("Pressione ENTER para manter o valor atual.");
                    System.out.print("Título (" + c.getDstitle() + "): ");
                    String tit = sc.nextLine();
                    if (!tit.isEmpty()) c.setDstitle(tit);

                    while (true) {
                        System.out.print("ID Modelo (" + c.getCdtemplate() + "): ");
                        String tpl = sc.nextLine();
                        if (tpl.isEmpty()) break; // Pressionou ENTER, mantém o valor atual
                        try {
                            int idTpl = Integer.parseInt(tpl);
                            if (contractTemplateDAO.findById(idTpl) != null) {
                                c.setCdtemplate(idTpl);
                                break;
                            } else {
                                System.out.print("ERRO: Modelo não encontrado. Ver lista de modelos? (s/n): ");
                                if (sc.nextLine().equalsIgnoreCase("s")) {
                                    contractTemplateDAO.listAll().forEach(t -> 
                                        System.out.println("ID: " + t.getCdtemplate() + " | Nome: " + t.getNmtemplate())
                                    );
                                }
                            }
                        } catch (Exception e) { System.out.println("Entrada inválida."); }
                    }

                    while (true) {
                        System.out.print("ID Índice de Reajuste (" + c.getCdindex() + "): ");
                        String idx = sc.nextLine();
                        if (idx.isEmpty()) break; // Pressionou ENTER, mantém o valor atual
                        try {
                            int idIdx = Integer.parseInt(idx);
                            if (indexDAO.findById(idIdx) != null) {
                                c.setCdindex(idIdx);
                                break;
                            } else {
                                System.out.print("ERRO: Índice não encontrado. Ver lista de índices? (s/n): ");
                                if (sc.nextLine().equalsIgnoreCase("s")) {
                                    indexDAO.listAll().forEach(i -> 
                                        System.out.println("ID: " + i.getCdindex() + " | Nome: " + i.getNmindex())
                                    );
                                }
                            }
                        } catch (Exception e) { System.out.println("Entrada inválida."); }
                    }

                    contractDAO.updateContract(c);
                    break;
                } else {
                    System.out.print("ERRO: Contrato não encontrado. Deseja ver a lista de contratos? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        contractDAO.getActiveContractsList().forEach(System.out::println);
                    }
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }
    }

    private static void excluirContrato() {
        System.out.println("\n--- EXCLUIR CONTRATO ---");
        while (true) {
            System.out.print("ID Contrato (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                int idC = Integer.parseInt(input);
                Contracts c = contractDAO.findById(idC);
                if (c != null) {
                    System.out.println("ATENÇÃO: Excluir o contrato apagará TODAS as parcelas, notificações e comissões vinculadas a ele.");
                    System.out.print("Confirmar exclusão do contrato '" + c.getDstitle() + "'? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        if (contractDAO.deleteContract(idC)) {
                            System.out.println("Contrato excluído com sucesso!");
                            System.out.print("Deseja voltar o status do Imóvel ID " + c.getCdproperty() + " para 'Disponível' (1)? (s/n): ");
                            if (sc.nextLine().equalsIgnoreCase("s")) {
                                Properties p = propertyDAO.findById(c.getCdproperty());
                                if (p != null) { p.setCdstatus(1); propertyDAO.updateProperty(p); }
                            }
                        }
                    } else System.out.println("Operação cancelada.");
                    break;
                } else {
                    System.out.print("ERRO: Contrato não encontrado. Listar contratos? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) contractDAO.getActiveContractsList().forEach(System.out::println);
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }
    }

    private static void relatorioFinanceiroContrato() {
        System.out.println("\n--- EXTRATO FINANCEIRO ---");
        while (true) {
            System.out.print("ID Contrato (ou 0 para cancelar): ");
            String input = sc.nextLine();
            if (input.isEmpty() || input.equals("0")) return;
            try {
                int idC = Integer.parseInt(input);
                if (contractDAO.contractExists(idC)) {
                    contractDAO.gerarExtrato(idC);
                    break;
                } else {
                    System.out.print("ERRO: Contrato não encontrado. Deseja ver a lista de contratos disponíveis? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        contractDAO.getActiveContractsList().forEach(System.out::println);
                    }
                }
            } catch (Exception e) { System.out.println("Entrada inválida."); }
        }
    }

    // =========================================================
    // SUBMENUS DE TABELAS DE DOMÍNIO E AUXILIARES
    // =========================================================

    private static void menuLocalizacao() {
        System.out.println("\n--- LOCALIZAÇÃO ---");
        System.out.println("1. Países  2. Cidades  3. Bairros  0. Voltar");
        System.out.print("Escolha: ");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) crudCountry();
            else if (op == 2) crudCity();
            else if (op == 3) crudDistrict();
        } catch(Exception e){}
    }

    private static void menuAtributosImoveis() {
        System.out.println("\n--- DOMÍNIOS DE IMÓVEIS ---");
        System.out.println("1. Tipos  2. Finalidades  3. Status  0. Voltar");
        System.out.print("Escolha: ");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) crudPropertyType();
            else if (op == 2) crudPropertyPurpose();
            else if (op == 3) crudPropertyStatus();
        } catch(Exception e){}
    }

    private static void menuAtributosContratuais() {
        System.out.println("\n--- DOMÍNIOS CONTRATUAIS ---");
        System.out.println("1. Modelos (Templates)  2. Cláusulas  3. Índices  4. Papéis  0. Voltar");
        System.out.print("Escolha: ");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) crudContractTemplate();
            else if (op == 2) crudClause();
            else if (op == 3) crudIndex();
            else if (op == 4) crudRole();
        } catch(Exception e){}
    }

    private static void menuOutros() {
        System.out.println("\n--- OUTROS DOMÍNIOS ---");
        System.out.println("1. Contas Bancárias  2. Notificações  0. Voltar");
        System.out.print("Escolha: ");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) crudBankAccount();
            else if (op == 2) crudNotification();
        } catch(Exception e){}
    }

    // =========================================================
    // MÉTODOS CRUD COMPACTOS PARA TABELAS AUXILIARES
    // =========================================================

    private static void crudCountry() {
        System.out.println("\n[Países] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) {
                Countries c = new Countries();
                System.out.print("Nome: "); c.setNmcountry(sc.nextLine());
                System.out.print("Sigla: "); c.setSgcountry(sc.nextLine());
                countryDAO.insert(c);
            } else if (op == 2) {
                countryDAO.listAll().forEach(c -> System.out.println(c.getCdcountry() + " - " + c.getNmcountry() + " (" + c.getSgcountry() + ")"));
            } else if (op == 3) {
                System.out.print("ID: "); Countries c = countryDAO.findById(Integer.parseInt(sc.nextLine()));
                if (c != null) {
                    System.out.print("Novo Nome (" + c.getNmcountry() + "): "); String n = sc.nextLine(); if (!n.isEmpty()) c.setNmcountry(n);
                    System.out.print("Nova Sigla (" + c.getSgcountry() + "): "); String s = sc.nextLine(); if (!s.isEmpty()) c.setSgcountry(s);
                    countryDAO.update(c);
                }
            } else if (op == 4) {
                System.out.print("ID para excluir: "); countryDAO.delete(Integer.parseInt(sc.nextLine()));
            }
        } catch(Exception e){ System.out.println("Operação cancelada."); }
    }

    private static void crudCity() {
        System.out.println("\n[Cidades] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) {
                Cities c = new Cities();
                System.out.print("Nome: "); c.setNmcity(sc.nextLine());
                System.out.print("ID Estado: "); c.setCdstate(Integer.parseInt(sc.nextLine()));
                cityDAO.insert(c);
            } else if (op == 2) {
                cityDAO.listAll().forEach(c -> System.out.println(c.getCdcity() + " - " + c.getNmcity() + " (Est ID: " + c.getCdstate() + ")"));
            } else if (op == 3) {
                System.out.print("ID: "); Cities c = cityDAO.findById(Integer.parseInt(sc.nextLine()));
                if (c != null) {
                    System.out.print("Novo Nome (" + c.getNmcity() + "): "); String n = sc.nextLine(); if (!n.isEmpty()) c.setNmcity(n);
                    System.out.print("Novo ID Estado (" + c.getCdstate() + "): "); String s = sc.nextLine(); if (!s.isEmpty()) c.setCdstate(Integer.parseInt(s));
                    cityDAO.update(c);
                }
            } else if (op == 4) {
                System.out.print("ID para excluir: "); cityDAO.delete(Integer.parseInt(sc.nextLine()));
            }
        } catch(Exception e){ System.out.println("Operação cancelada."); }
    }

    private static void crudDistrict() {
        System.out.println("\n[Bairros] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) {
                Districts d = new Districts();
                System.out.print("Nome: "); d.setNmdistrict(sc.nextLine());
                System.out.print("ID Cidade: "); d.setCdcity(Integer.parseInt(sc.nextLine()));
                districtDAO.insert(d);
            } else if (op == 2) {
                districtDAO.listAll().forEach(d -> System.out.println(d.getCddistrict() + " - " + d.getNmdistrict() + " (Cid ID: " + d.getCdcity() + ")"));
            } else if (op == 3) {
                System.out.print("ID: "); Districts d = districtDAO.findById(Integer.parseInt(sc.nextLine()));
                if (d != null) {
                    System.out.print("Novo Nome (" + d.getNmdistrict() + "): "); String n = sc.nextLine(); if (!n.isEmpty()) d.setNmdistrict(n);
                    System.out.print("Novo ID Cidade (" + d.getCdcity() + "): "); String c = sc.nextLine(); if (!c.isEmpty()) d.setCdcity(Integer.parseInt(c));
                    districtDAO.update(d);
                }
            } else if (op == 4) {
                System.out.print("ID para excluir: "); districtDAO.delete(Integer.parseInt(sc.nextLine()));
            }
        } catch(Exception e){ System.out.println("Operação cancelada."); }
    }

    private static void crudPropertyType() {
        System.out.println("\n[Tipos de Imóvel] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Property_Types t = new Property_Types(); System.out.print("Tipo: "); t.setNmtype(sc.nextLine()); propertyTypeDAO.insert(t); }
            else if (op == 2) { propertyTypeDAO.listAll().forEach(t -> System.out.println(t.getCdtype() + " - " + t.getNmtype())); }
            else if (op == 3) { System.out.print("ID: "); Property_Types t = propertyTypeDAO.findById(Integer.parseInt(sc.nextLine())); if(t!=null){ System.out.print("Tipo ("+t.getNmtype()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) t.setNmtype(n); propertyTypeDAO.update(t); } }
            else if (op == 4) { System.out.print("ID: "); propertyTypeDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudPropertyPurpose() {
        System.out.println("\n[Finalidades de Imóvel] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Property_Purposes p = new Property_Purposes(); System.out.print("Finalidade: "); p.setNmpurpose(sc.nextLine()); propertyPurposeDAO.insert(p); }
            else if (op == 2) { propertyPurposeDAO.listAll().forEach(p -> System.out.println(p.getCdpurpose() + " - " + p.getNmpurpose())); }
            else if (op == 3) { System.out.print("ID: "); Property_Purposes p = propertyPurposeDAO.findById(Integer.parseInt(sc.nextLine())); if(p!=null){ System.out.print("Finalidade ("+p.getNmpurpose()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) p.setNmpurpose(n); propertyPurposeDAO.update(p); } }
            else if (op == 4) { System.out.print("ID: "); propertyPurposeDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudPropertyStatus() {
        System.out.println("\n[Status de Imóvel] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Property_Status s = new Property_Status(); System.out.print("Status: "); s.setNmstatus(sc.nextLine()); propertyStatusDAO.insert(s); }
            else if (op == 2) { propertyStatusDAO.listAll().forEach(s -> System.out.println(s.getCdstatus() + " - " + s.getNmstatus())); }
            else if (op == 3) { System.out.print("ID: "); Property_Status s = propertyStatusDAO.findById(Integer.parseInt(sc.nextLine())); if(s!=null){ System.out.print("Status ("+s.getNmstatus()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) s.setNmstatus(n); propertyStatusDAO.update(s); } }
            else if (op == 4) { System.out.print("ID: "); propertyStatusDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudContractTemplate() {
        System.out.println("\n[Modelos de Contrato] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Contract_Templates t = new Contract_Templates(); System.out.print("Nome: "); t.setNmtemplate(sc.nextLine()); System.out.print("Versão: "); t.setDsversion(sc.nextLine()); System.out.print("Ativo (S/N): "); t.setFgactive(sc.nextLine()); contractTemplateDAO.insert(t); }
            else if (op == 2) { contractTemplateDAO.listAll().forEach(t -> System.out.println(t.getCdtemplate() + " - " + t.getNmtemplate() + " [Ver: " + t.getDsversion() + " Ativo: " + t.getFgactive() + "]")); }
            else if (op == 3) { System.out.print("ID: "); Contract_Templates t = contractTemplateDAO.findById(Integer.parseInt(sc.nextLine())); if(t!=null){ System.out.print("Nome ("+t.getNmtemplate()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) t.setNmtemplate(n); System.out.print("Versão ("+t.getDsversion()+"): "); String v = sc.nextLine(); if(!v.isEmpty()) t.setDsversion(v); contractTemplateDAO.update(t); } }
            else if (op == 4) { System.out.print("ID: "); contractTemplateDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudClause() {
        System.out.println("\n[Cláusulas] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Clauses c = new Clauses(); System.out.print("Texto: "); c.setDstext(sc.nextLine()); System.out.print("ID Tópico: "); c.setCdtopic(Integer.parseInt(sc.nextLine())); clauseDAO.insert(c); }
            else if (op == 2) { clauseDAO.listAll().forEach(c -> System.out.println(c.getCdclause() + " - Tópico: " + c.getCdtopic() + " - " + c.getDstext())); }
            else if (op == 3) { System.out.print("ID: "); Clauses c = clauseDAO.findById(Integer.parseInt(sc.nextLine())); if(c!=null){ System.out.print("Texto ("+c.getDstext()+"): "); String txt = sc.nextLine(); if(!txt.isEmpty()) c.setDstext(txt); System.out.print("ID Tópico ("+c.getCdtopic()+"): "); String top = sc.nextLine(); if(!top.isEmpty()) c.setCdtopic(Integer.parseInt(top)); clauseDAO.update(c); } }
            else if (op == 4) { System.out.print("ID: "); clauseDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudIndex() {
        System.out.println("\n[Índices Financeiros] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Indexes i = new Indexes(); System.out.print("Nome: "); i.setNmindex(sc.nextLine()); indexDAO.insert(i); }
            else if (op == 2) { indexDAO.listAll().forEach(i -> System.out.println(i.getCdindex() + " - " + i.getNmindex())); }
            else if (op == 3) { System.out.print("ID: "); Indexes i = indexDAO.findById(Integer.parseInt(sc.nextLine())); if(i!=null){ System.out.print("Nome ("+i.getNmindex()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) i.setNmindex(n); indexDAO.update(i); } }
            else if (op == 4) { System.out.print("ID: "); indexDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudRole() {
        System.out.println("\n[Papéis Contratuais] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Roles r = new Roles(); System.out.print("Papel: "); r.setNmrole(sc.nextLine()); roleDAO.insert(r); }
            else if (op == 2) { roleDAO.listAll().forEach(r -> System.out.println(r.getCdrole() + " - " + r.getNmrole())); }
            else if (op == 3) { System.out.print("ID: "); Roles r = roleDAO.findById(Integer.parseInt(sc.nextLine())); if(r!=null){ System.out.print("Papel ("+r.getNmrole()+"): "); String n = sc.nextLine(); if(!n.isEmpty()) r.setNmrole(n); roleDAO.update(r); } }
            else if (op == 4) { System.out.print("ID: "); roleDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){}
    }

    private static void crudBankAccount() {
        System.out.println("\n[Contas Bancárias] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Bank_Accounts b = new Bank_Accounts(); System.out.print("Agência: "); b.setNragency(sc.nextLine()); System.out.print("Conta: "); b.setNraccount(sc.nextLine()); System.out.print("PIX: "); b.setNrpixkey(sc.nextLine()); System.out.print("ID Usuário: "); b.setCduser(Integer.parseInt(sc.nextLine())); bankAccountDAO.insert(b); }
            else if (op == 2) { bankAccountDAO.listAll().forEach(b -> System.out.println(b.getCdbankaccount() + " - Ag: " + b.getNragency() + " Cc: " + b.getNraccount() + " PIX: " + b.getNrpixkey() + " [User: " + b.getCduser() + "]")); }
            else if (op == 3) { 
                System.out.print("ID: "); Bank_Accounts b = bankAccountDAO.findById(Integer.parseInt(sc.nextLine())); 
                if (b != null) { 
                    System.out.print("Agência ("+b.getNragency()+"): "); String ag = sc.nextLine(); if(!ag.isEmpty()) b.setNragency(ag); 
                    System.out.print("Conta ("+b.getNraccount()+"): "); String cc = sc.nextLine(); if(!cc.isEmpty()) b.setNraccount(cc); 
                    System.out.print("PIX ("+b.getNrpixkey()+"): "); String px = sc.nextLine(); if(!px.isEmpty()) b.setNrpixkey(px); 
                    System.out.print("ID Usuário ("+b.getCduser()+"): "); String u = sc.nextLine(); if(!u.isEmpty()) b.setCduser(Integer.parseInt(u)); 
                    bankAccountDAO.update(b); 
                } 
            }
            else if (op == 4) { System.out.print("ID: "); bankAccountDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){ System.out.println("Operação cancelada."); }
    }

    private static void crudNotification() {
        System.out.println("\n[Notificações] 1.Novo 2.Listar 3.Atualizar 4.Excluir");
        try {
            int op = Integer.parseInt(sc.nextLine());
            if (op == 1) { Notifications n = new Notifications(); System.out.print("Mensagem: "); n.setDsmessage(sc.nextLine()); System.out.print("Data (AAAA-MM-DD): "); n.setDtsend(LocalDate.parse(sc.nextLine())); System.out.print("ID Contrato: "); n.setCdcontract(Integer.parseInt(sc.nextLine())); System.out.print("ID Usuário: "); n.setCduser(Integer.parseInt(sc.nextLine())); n.setFgread(false); notificationDAO.insert(n); }
            else if (op == 2) { notificationDAO.listAll().forEach(n -> System.out.println(n.getCdnotification() + " - " + n.getDtsend() + " | Lido: " + n.isFgread() + " | Msg: " + n.getDsmessage())); }
            else if (op == 3) { 
                System.out.print("ID: "); Notifications n = notificationDAO.findById(Integer.parseInt(sc.nextLine())); 
                if(n!=null){ 
                    System.out.print("Mensagem ("+n.getDsmessage()+"): "); String m = sc.nextLine(); if(!m.isEmpty()) n.setDsmessage(m); 
                    System.out.print("Data ("+n.getDtsend()+"): "); String d = sc.nextLine(); if(!d.isEmpty()) n.setDtsend(LocalDate.parse(d)); 
                    System.out.print("Lida (true/false) ("+n.isFgread()+"): "); String r = sc.nextLine(); if(!r.isEmpty()) n.setFgread(Boolean.parseBoolean(r)); 
                    notificationDAO.update(n); 
                } 
            }
            else if (op == 4) { System.out.print("ID: "); notificationDAO.delete(Integer.parseInt(sc.nextLine())); }
        } catch(Exception e){ System.out.println("Operação cancelada ou data inválida."); }
    }
}