package view;

import dao.*;
import service.ReportService;

/**
 * Ponto de entrada principal do sistema imobiliário.
 */
public class Main {

    private final UserView userView;
    private final PropertyView propertyView;
    private final ContractView contractView;
    private final DomainCrudView domainView;
    private final ReportView reportView;
    private final OccupationView occupationView;

    public Main(UserView userView, PropertyView propertyView, ContractView contractView,
                DomainCrudView domainView, ReportView reportView, OccupationView occupationView) {
        this.userView = userView;
        this.propertyView = propertyView;
        this.contractView = contractView;
        this.domainView = domainView;
        this.reportView = reportView;
        this.occupationView = occupationView;
    }

    /**
     * Inicializa a aplicação e suas dependências.
     * 
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        PropertyDAO propertyDAO = new PropertyDAO();
        ContractDAO contractDAO = new ContractDAO();
        ContractTemplateDAO templateDAO = new ContractTemplateDAO();
        IndexDAO indexDAO = new IndexDAO();
        OccupationDAO occupationDAO = new OccupationDAO();
        TopicDAO topicDAO = new TopicDAO();
        BrokerDataDAO brokerDataDAO = new BrokerDataDAO();
        StateDAO stateDAO = new StateDAO();
        AddressDAO addressDAO = new AddressDAO();
        IndexRateDAO indexRateDAO = new IndexRateDAO();

        DomainCrudView domainView = new DomainCrudView(
                new CountryDAO(), new CityDAO(), new DistrictDAO(),
                new PropertyTypeDAO(), new PropertyPurposeDAO(), new PropertyStatusDAO(),
                templateDAO, new ClauseDAO(), indexDAO, new RoleDAO(),
                new BankAccountDAO(), new NotificationDAO(),
                topicDAO, brokerDataDAO,
                stateDAO, addressDAO, indexRateDAO, userDAO, contractDAO,
                new InstallmentDAO(), new UserContractDAO(), new NotaryDAO(), occupationDAO);

        UserView uv = new UserView(userDAO);
        PropertyView pv = new PropertyView(propertyDAO, userDAO, uv);
        InstallmentDAO installmentDAO = new InstallmentDAO();
        
        Main app = new Main(
                uv,
                pv,
                new ContractView(contractDAO, propertyDAO, userDAO, templateDAO, indexDAO, topicDAO, uv, pv, installmentDAO, new BankAccountDAO(), new UserContractDAO(), addressDAO, new DistrictDAO(), new CityDAO(), new ClauseDAO(), new NotaryDAO(), occupationDAO),
                domainView,
                new ReportView(propertyDAO, new ReportService()),
                new OccupationView(occupationDAO));

        app.executar();
    }

    /**
     * Inicia o loop principal do menu do sistema.
     */
    public void executar() {
        int modulo = 0;
        while (modulo != 9) {
            imprimirCabecalho();
            modulo = ConsoleIO.lerIntSeguro("Selecione o módulo: ");
            switch (modulo) {
                case 1: menuCRUD(); break;
                case 2: contractView.menu(); break;
                case 3: reportView.menu(); break;
                case 9: System.out.println("Saindo..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void imprimirCabecalho() {
        System.out.println("\n========================================");
        System.out.println("     SISTEMA IMOBILIÁRIO INTEGRADO      ");
        System.out.println("========================================");
        System.out.println("1. MÓDULO DE CADASTROS (CRUD)");
        System.out.println("2. MÓDULO DE PROCESSOS DE NEGÓCIO");
        System.out.println("3. MÓDULO DE RELATÓRIOS");
        System.out.println("9. SAIR");
    }

    private void menuCRUD() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- GESTÃO DE CADASTROS (CRUD) ---");
            System.out.println("1. Usuários");
            System.out.println("2. Imóveis");
            System.out.println("3. Localização (Países, Estados, Cidades, Bairros, Endereços)");
            System.out.println("4. Domínios de Imóveis (Tipos, Finalidades, Status)");
            System.out.println("5. Domínios Contratuais (Modelos, Cláusulas, Índices, Papéis)");
            System.out.println("6. Outros (Profissões, Contas Bancárias, Notificações)");
            System.out.println("0. Voltar ao Menu Principal");
            op = ConsoleIO.lerIntSeguro("Escolha: ");
            switch (op) {
                case 1: userView.menu(); break;
                case 2: propertyView.menu(); break;
                case 3: domainView.menuLocalizacao(); break;
                case 4: domainView.menuAtributosImoveis(); break;
                case 5: domainView.menuAtributosContratuais(); break;
                case 6:
                    System.out.println("\n--- 6. OUTROS DOMÍNIOS ---");
                    System.out.println("1. Profissões");
                    System.out.println("2. Contas Bancárias");
                    System.out.println("3. Notificações");
                    int subOp = ConsoleIO.lerIntSeguro("Escolha: ");
                    if (subOp == 1) occupationView.menu();
                    else if (subOp == 2) domainView.crudBankAccount();
                    else if (subOp == 3) domainView.crudNotification();
                    break;
                case 0: break;
                default: System.out.println("Opção inválida!");
            }
        }
    }
}
