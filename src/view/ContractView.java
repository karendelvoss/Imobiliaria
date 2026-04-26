package view;

import dao.BankAccountDAO;
import dao.ContractDAO;
import dao.ContractTemplateDAO;
import dao.IndexDAO;
import dao.AddressDAO;
import dao.CityDAO;
import dao.ClauseDAO;
import dao.DistrictDAO;
import dao.InstallmentDAO;
import dao.NotaryDAO;
import dao.OccupationDAO;
import dao.PropertyDAO;
import dao.TopicDAO;
import dao.UserContractDAO;
import dao.UserDAO;
import dao.VariableDAO;
import model.Bank_Accounts;
import model.Contracts;
import model.Installments;
import model.Properties;
import model.SignatureStatus;
import model.User_Contract;
import model.ContractRenewalType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static view.ConsoleIO.*;

/**
 * Interface de console para processos de negócio complexos envolvendo contratos.
 */
public class ContractView {

    private final ContractDAO contractDAO;
    private final PropertyDAO propertyDAO;
    private final UserDAO userDAO;
    private final ContractTemplateDAO templateDAO;
    private final IndexDAO indexDAO;
    private final TopicDAO topicDAO;
    
    private final UserView userView;
    private final PropertyView propertyView;
    private final InstallmentDAO installmentDAO;
    private final BankAccountDAO bankAccountDAO;
    private final UserContractDAO userContractDAO;
    private final AddressDAO addressDAO;
    private final DistrictDAO districtDAO;
    private final CityDAO cityDAO;
    private final VariableDAO variableDAO;
    private final ClauseDAO clauseDAO;
    private final dao.NotaryDAO notaryDAO;
    private final OccupationDAO occupationDAO;

    public ContractView(ContractDAO contractDAO, PropertyDAO propertyDAO, UserDAO userDAO,
                        ContractTemplateDAO templateDAO, IndexDAO indexDAO, TopicDAO topicDAO,
                        UserView userView, PropertyView propertyView, InstallmentDAO installmentDAO,
                        BankAccountDAO bankAccountDAO, UserContractDAO userContractDAO,
                        AddressDAO addressDAO, DistrictDAO districtDAO, CityDAO cityDAO,
                        VariableDAO variableDAO, ClauseDAO clauseDAO, dao.NotaryDAO notaryDAO, OccupationDAO occupationDAO) {
        this.contractDAO = contractDAO;
        this.propertyDAO = propertyDAO;
        this.userDAO = userDAO;
        this.templateDAO = templateDAO;
        this.indexDAO = indexDAO;
        this.topicDAO = topicDAO;
        this.userView = userView;
        this.propertyView = propertyView;
        this.installmentDAO = installmentDAO;
        this.bankAccountDAO = bankAccountDAO;
        this.userContractDAO = userContractDAO;
        this.addressDAO = addressDAO;
        this.districtDAO = districtDAO;
        this.cityDAO = cityDAO;
        this.variableDAO = variableDAO;
        this.clauseDAO = clauseDAO;
        this.notaryDAO = notaryDAO;
        this.occupationDAO = occupationDAO;
    }

    /**
     * Menu principal de processos de negócio.
     */
    public void menu() {
        System.out.println("\n--- PROCESSOS DE NEGÓCIO ---");
        System.out.println("1. EFETIVAR NOVO CONTRATO");
        System.out.println("2. VINCULAR PROPRIETÁRIO A IMÓVEL");
        System.out.println("3. DESVINCULAR PROPRIETÁRIO DE IMÓVEL");
        System.out.println("4. ALTERAR CONTRATO");
        System.out.println("5. EXCLUIR CONTRATO");
        System.out.println("6. ESTRUTURAR MODELO DE CONTRATO (VINCULAR TÓPICOS)");
        System.out.println("7. PROCESSAR REAJUSTES MENSAIS (JOB MANUAL)");
        System.out.println("8. PROCESSAR NOTIFICAÇÕES (JOB MANUAL)");
        System.out.println("9. REGISTRAR PAGAMENTO DE PARCELA");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: realizarNovoFluxo(); break;
            case 2: vincularDono(); break;
            case 3: desvincularDono(); break;
            case 4: alterar(); break;
            case 5: excluir(); break;
            case 6: estruturarModelo(); break;
            case 7: dispararJobReajuste(); break;
            case 8: dispararJobNotificacoes(); break;
            case 9: registrarPagamento(); break;
        }
    }
    
    private void registrarPagamento() {
        System.out.println("\n--- REGISTRAR PAGAMENTO DE PARCELA ---");

        int idContrato = lerIdValido("ID do Contrato",
            contractDAO::findById,
            () -> contractDAO.getActiveContractsList("geral").forEach(System.out::println));
        if (idContrato <= 0) { System.out.println("Operação cancelada."); return; }

        List<Installments> pendentes = installmentDAO.findPendingInstallmentsByContractId(idContrato);
        if (pendentes.isEmpty()) {
            System.out.println("Nenhuma parcela pendente encontrada para o contrato #" + idContrato);
            return;
        }

        System.out.println("\n--- PARCELAS PENDENTES (Contrato #" + idContrato + ") ---");
        System.out.printf("%-12s | %-8s | %-12s | %-14s | %-12s%n",
            "ID Parcela", "Nº", "Vencimento", "Valor", "Status");
        System.out.println("-----------------------------------------------------------------------");
        for (Installments inst : pendentes) {
            double valor = inst.getVladjusted() > 0 ? inst.getVladjusted() : inst.getVlbase();
            System.out.printf("ID: %d | Nº: %d | Vencimento: %s | Valor: R$ %.2f%n",
                inst.getCdinstallment(), inst.getNrinstallment(),
                inst.getDtdue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                valor);
        }

        int idParcela = lerIntSeguro("ID da Parcela a pagar (0 para cancelar): ");
        if (idParcela <= 0) { System.out.println("Operação cancelada."); return; }

        Installments alvo = null;
        for (Installments inst : pendentes) {
            if (inst.getCdinstallment() == idParcela) {
                alvo = inst;
                break;
            }
        }
        if (alvo == null) {
            System.out.println("Parcela inválida.");
            return;
        }

        boolean emAtraso = alvo.getDtdue().isBefore(LocalDate.now());
        if (emAtraso) {
            service.FinancialService financialService = new service.FinancialService();
            alvo = financialService.calculateLateFeesAndInterest(alvo);
        }

        // Confirma o pagamento (exibe o valor ATUALIZADO com juros/multa se houver)
        double valorFinal = alvo.getVladjusted() > 0 ? alvo.getVladjusted() : alvo.getVlbase();
        if (emAtraso) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(alvo.getDtdue(), LocalDate.now());
            System.out.printf("%n⚠  PARCELA EM ATRASO há %d dia(s)!%n", diasAtraso);
            System.out.printf("   Valor Original: R$ %.2f%n", alvo.getVlbase());
            System.out.printf("   Multa (%.0f%%):  R$ %.2f%n", alvo.getVlpenalty() * 100, alvo.getVlbase() * alvo.getVlpenalty());
            System.out.printf("   Juros Diários:  R$ %.2f%n", alvo.getVlbase() * alvo.getVlinterest() * diasAtraso);
            System.out.printf("   TOTAL A PAGAR:  R$ %.2f%n", valorFinal);
        } else {
            System.out.printf("%nConfirmar pagamento da Parcela #%d (R$ %.2f) vencida em %s?%n",
                alvo.getNrinstallment(), valorFinal,
                alvo.getDtdue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (!confirmar("Confirmar pagamento? (s/n): ")) { System.out.println("Pagamento cancelado."); return; }

        // Registra o pagamento com o valor atualizado
        alvo.setCdstatus(model.InstallmentStatus.PAGO.getCode());
        alvo.setDtpayment(LocalDate.now());
        installmentDAO.update(alvo);

        System.out.printf("%nPagamento da Parcela #%d registrado com sucesso! Valor cobrado: R$ %.2f (Data: %s)%n",
            alvo.getNrinstallment(), valorFinal,
            LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void dispararJobNotificacoes() {
        System.out.println("\n--- PROCESSAR NOTIFICAÇÕES (JOB MANUAL) ---");
        System.out.println("1. Lembretes de Vencimento de Aluguel (Inquilino)");
        System.out.println("2. Confirmação de Pagamento (Locador)");
        System.out.println("3. Avisos de Reajuste Anual (Locador + Inquilino)");
        System.out.println("4. Avisos de Vencimento de Contrato (Locador + Inquilino)");
        System.out.println("5. Processar TODOS os Jobs");

        service.NotificationService notificationService = new service.NotificationService();
        System.out.println("\n--- 1. Lembretes 2. Confirmação Pagto 3. Reajuste 4. Vencimento Contrato 5. TODOS ---");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: notificationService.processarLembretesDeAluguel(); break;
            case 2: notificationService.processarConfirmacaoDePagamento(); break;
            case 3: notificationService.processarAvisosDeReajuste(); break;
            case 4: notificationService.processarAvisosDeVencimentoContrato(); break;
            case 5: notificationService.processarTodosOsJobs(); break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void dispararJobReajuste() {
        System.out.println("\n--- PROCESSAR REAJUSTES MENSAIS ---");
        System.out.println("Iniciando rotina de verificação e reajuste de contratos ativos...");
        service.AutomationService automationService = new service.AutomationService();
        automationService.processMonthlyAdjustments();
        
        List<Contracts> expiringContracts = automationService.getExpiringContracts();
        if (!expiringContracts.isEmpty()) {
            System.out.println("\nATENÇÃO: " + expiringContracts.size() + " contrato(s) com data limite prevista para este mês.");
            for (Contracts c : expiringContracts) {
                System.out.println("\n-> Contrato ID: " + c.getCdcontract() + " | Título: " + c.getDstitle() + " | Vence em: " + c.getDtlimit());
                System.out.println("Selecione o destino para este contrato:");
                System.out.println("1. Encerrar Contrato");
                System.out.println("2. Renovar por Tempo Indeterminado");
                System.out.println("3. Renovar por Tempo Determinado");
                
                int op = lerIntSeguro("Opção: ");
                switch(op) {
                    case 1:
                        automationService.handleContractExpiration(c, ContractRenewalType.ENCERRAMENTO, 0);
                        break;
                    case 2:
                        automationService.handleContractExpiration(c, ContractRenewalType.INDETERMINADO, 0);
                        break;
                    case 3:
                        int meses = lerIntSeguro("Quantos meses de renovação? ");
                        automationService.handleContractExpiration(c, ContractRenewalType.DETERMINADO, meses);
                        break;
                    default:
                        System.out.println("Opção inválida. Nenhuma ação tomada para este contrato.");
                }
            }
        }
        
        System.out.println("\nRotina de reajuste mensal concluída!");
    }

    private void realizarNovoFluxo() {
        System.out.println("\n=== FLUXO DE CADASTRO DE CONTRATO ===");

        int idTpl = lerIdValido("ID do Modelo",
                templateDAO::findById,
                () -> templateDAO.listAll().forEach(
                        t -> System.out.println("ID: " + t.getCdtemplate() + " | " + t.getNmtemplate())));
        if (idTpl == -1) return;

        Contracts contract = new Contracts();
        contract.setCdtemplate(idTpl);
        
        String dtCreationStr = ler("Data de Criação (AAAA-MM-DD) ou [ENTER] para hoje: ");
        contract.setDtcreation(dtCreationStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dtCreationStr));
        contract.setDstitle(ler("Título: "));

        boolean isLocacao = confirmar("É locação? (s/n): ");
        if (isLocacao) {
            String dataStr = ler("Data limite (AAAA-MM-DD): ");
            if (!dataStr.isEmpty()) contract.setDtlimit(LocalDate.parse(dataStr));
        }

        int cdcontract = contractDAO.insertContract(contract);
        if (cdcontract <= 0) return;
        contract.setCdcontract(cdcontract);

        List<Integer> idsPartesContrato = new ArrayList<>();
        
        System.out.println("\n--- VINCULAR LOCADORES ---");
        do {
            int idUser = selecionarOuCriarUsuario(idsPartesContrato);
            if (idUser > 0) {
                idsPartesContrato.add(idUser);
                vincularParte(cdcontract, idUser, 2); // Locador
            }
        } while (confirmar("Outro Locador? (s/n): "));

        System.out.println("\n--- VINCULAR LOCATÁRIOS ---");
        do {
            int idUser = selecionarOuCriarUsuario(idsPartesContrato);
            if (idUser > 0) {
                idsPartesContrato.add(idUser);
                vincularParte(cdcontract, idUser, 1); // Locatário
            }
        } while (confirmar("Outro Locatário? (s/n): "));

        if (confirmar("Adicionar representante? (s/n): ")) {
            int idUser = selecionarOuCriarUsuario(idsPartesContrato);
            if (idUser > 0) vincularParte(cdcontract, idUser, 4);
        }

        System.out.println("\n--- VINCULAR TESTEMUNHAS ---");
        for (int i = 1; i <= 2; i++) {
            int idUser = selecionarOuCriarUsuario(idsPartesContrato);
            if (idUser > 0) {
                idsPartesContrato.add(idUser);
                vincularParte(cdcontract, idUser, 3);
            } else i--;
        }

        int cdproperty = lerIdValido("Selecionar Imóvel",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println),
                propertyView::cadastrar);

        if (cdproperty > 0) {
            contract.setCdproperty(cdproperty);
            contractDAO.updateContract(contract);
        }

        System.out.println("\n--- DADOS DO TABELIONATO ---");
        model.Notaries n = new model.Notaries();
        n.setCdcity(lerIdValido("Cidade do Tabelionato", cityDAO::findById, () -> cityDAO.listAll().forEach(System.out::println)));
        n.setNrnotary(lerIntSeguro("Número: "));
        n.setBook(ler("Livro: "));
        n.setLeaf(ler("Folha: "));
        n.setDt(LocalDate.now());
        contract.setCdnotary(notaryDAO.insertNotary(n));
        contractDAO.updateContract(contract);

        if (isLocacao) {
            gerarParcelasAutomaticas(contract);
        }

        int idIndex = lerIdValido("ID do Índice", indexDAO::findById, () -> indexDAO.listAll().forEach(System.out::println));
        if (idIndex > 0) {
            contract.setCdindex(idIndex);
            contractDAO.updateContract(contract);
        }

        service.ContractPdfService pdfService = new service.ContractPdfService(
            templateDAO, topicDAO, clauseDAO, contractDAO, propertyDAO, userDAO, userContractDAO,
            addressDAO, districtDAO, cityDAO, installmentDAO, bankAccountDAO, variableDAO, indexDAO, notaryDAO, occupationDAO
        );
        pdfService.generateContractPdf(cdcontract);
    }

    private void vincularParte(int cdcontract, int cduser, int cdrole) {
        User_Contract uc = new User_Contract();
        uc.setCdcontract(cdcontract);
        uc.setCduser(cduser);
        uc.setCdrole(cdrole);
        uc.setFgsignaturestatus(SignatureStatus.PENDENTE.getCode());
        uc.setVlparticipation(100.0);
        userContractDAO.insert(uc);
    }

    private void gerarParcelasAutomaticas(Contracts contract) {
        int qtdMeses = (int) java.time.temporal.ChronoUnit.MONTHS.between(contract.getDtcreation(), contract.getDtlimit());
        if (qtdMeses <= 0) qtdMeses = 1;

        double valorMensal = lerDouble("Valor aluguel: ");
        double multa = lerDouble("Multa atraso: ");
        double juros = lerDouble("Juros atraso: ");
        LocalDate dataVencimento = LocalDate.now().plusMonths(1);

        List<Installments> parcelas = new ArrayList<>();
        for (int i = 1; i <= qtdMeses; i++) {
            Installments inst = new Installments();
            inst.setNrinstallment(i);
            inst.setDtdue(dataVencimento.plusMonths(i - 1));
            inst.setVlbase(valorMensal);
            inst.setCdstatus(1);
            inst.setFk_Contracts_cdcontract(contract.getCdcontract());
            inst.setVlpenalty(multa);
            inst.setVlinterest(juros);
            parcelas.add(inst);
        }
        installmentDAO.insertBatch(parcelas);
    }

    private int selecionarOuCriarUsuario(List<Integer> idsIgnorar) {
        return lerIdValido("Selecionar Usuário",
                id -> idsIgnorar.contains(id) ? null : userDAO.findById(id),
                () -> userDAO.getAllUsersList().forEach(System.out::println),
                () -> userView.cadastrar());
    }

    private void vincularDono() {
        int idP = lerIdValido("ID Imóvel", propertyDAO::findById, () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        int idU = lerIdValido("ID Usuário", userDAO::findById, () -> userDAO.getAllUsersList().forEach(System.out::println));
        if (idP > 0 && idU > 0 && !propertyDAO.hasAlreadyThisOwner(idP, idU)) {
            propertyDAO.linkOwner(idP, idU);
        }
    }

    private void desvincularDono() {
        int idP = lerIdValido("ID Imóvel", propertyDAO::findById, () -> propertyDAO.getPropertiesWithOwners().forEach(System.out::println));
        if (idP <= 0) return;
        int idU = lerIdValido("ID Usuário", id -> propertyDAO.hasAlreadyThisOwner(idP, id) ? id : null, () -> propertyDAO.getOwnersByProperty(idP).forEach(System.out::println));
        if (idU > 0 && confirmar("Desvincular? (s/n): ")) {
            propertyDAO.unlinkOwner(idP, idU);
        }
    }

    private void alterar() {
        int idC = lerIdValido("ID Contrato", contractDAO::findById, () -> contractDAO.getActiveContractsList().forEach(System.out::println));
        if (idC <= 0) return;
        Contracts c = contractDAO.findById(idC);
        c.setDstitle(lerOuManter("Título", c.getDstitle()));
        contractDAO.updateContract(c);
    }

    private void excluir() {
        int idC = lerIdValido("ID Contrato", contractDAO::findById, () -> contractDAO.getActiveContractsList().forEach(System.out::println));
        if (idC <= 0) return;
        if (confirmar("Excluir contrato e todos os vínculos? (s/n): ")) {
            contractDAO.deleteContract(idC);
        }
    }

    private void estruturarModelo() {
        int idTpl = lerIdValido("ID Modelo", templateDAO::findById, () -> templateDAO.listAll().forEach(t -> System.out.println(t.getCdtemplate() + " - " + t.getNmtemplate())));
        if (idTpl <= 0) return;
        while (true) {
            int idTopic = lerIntSeguro("ID Tópico para vincular (0 para concluir): ");
            if (idTopic <= 0) break;
            templateDAO.linkTopic(idTpl, idTopic);
        }
    }
}
