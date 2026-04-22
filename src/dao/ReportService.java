package service;

import dao.ContractDAO;
import dao.InstallmentDAO;
import dao.UserContractDAO;
import dto.AdjustmentReportDTO;
import service.AutomationService;
import dao.IndexDAO;
import model.Contracts;
import dto.ParticipantDTO;
import model.Indexes;
import model.Installments;
import model.InstallmentStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Camada de Serviço para Lógicas de Geração de Relatórios.
 * Orquestra DAOs e outros serviços para consolidar e apresentar dados.
 */
public class ReportService {

    private final InstallmentDAO installmentDAO;
    private final FinancialService financialService;
    private final ContractDAO contractDAO;
    private final IndexDAO indexDAO;
    private final UserContractDAO userContractDAO;
    private final AutomationService automationService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportService() {
        this.installmentDAO = new InstallmentDAO();
        this.financialService = new FinancialService();
        this.contractDAO = new ContractDAO();
        this.indexDAO = new IndexDAO();
        this.userContractDAO = new UserContractDAO();
        this.automationService = new AutomationService();
    }

    /**
     * FASE 2: Gera e exibe o relatório financeiro para um contrato de locação.
     * @param contractId O ID do contrato.
     */
    public void gerarRelatorioFinanceiroLocacao(int contractId) {
        List<Installments> installments = installmentDAO.findByContractId(contractId);

        if (installments.isEmpty()) {
            System.out.println("\nNenhuma parcela encontrada para o contrato ID: " + contractId);
            return;
        }

        System.out.println("\n=================================================================================================================");
        System.out.printf("                                     RELATÓRIO FINANCEIRO - LOCAÇÃO (Contrato #%d)\n", contractId);
        System.out.println("=================================================================================================================");
        System.out.printf("%-10s | %-15s | %-12s | %-11s | %-13s | %-18s | %-12s\n",
                "ID Parcela", "Vencimento", "Valor Base", "Dias Atraso", "Juros + Multa", "Valor Atualizado", "Status");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        for (Installments inst : installments) {
            financialService.calculateLateFeesAndInterest(inst);

            long overdueDays = 0;
            double totalFees = 0.0;
            double finalValue = inst.getVlbase();
            InstallmentStatus status = InstallmentStatus.fromCode(inst.getCdstatus());
            String statusDescription = status.getDescription();

            if (inst.getDtdue().isBefore(LocalDate.now()) && status == InstallmentStatus.PENDENTE) {
                overdueDays = ChronoUnit.DAYS.between(inst.getDtdue(), LocalDate.now());
                finalValue = inst.getVladjusted();
                totalFees = finalValue - inst.getVlbase();
                statusDescription = InstallmentStatus.ATRASADO.getDescription();
            } else if (status == InstallmentStatus.PAGO) {
                finalValue = inst.getVladjusted() > 0 ? inst.getVladjusted() : inst.getVlbase();
            }

            System.out.printf("%-10d | %-15s | R$ %-10.2f | %-11d | R$ %-11.2f | R$ %-16.2f | %-12s\n",
                    inst.getCdinstallment(), inst.getDtdue().format(dateFormatter), inst.getVlbase(),
                    overdueDays, totalFees, finalValue, statusDescription);
        }
        System.out.println("=================================================================================================================");
    }

    /**
     * FASE 3: Gera e exibe o relatório financeiro para um contrato de venda.
     * @param contractId O ID do contrato.
     */
    public void gerarRelatorioFinanceiroVenda(int contractId) {
        Contracts contract = contractDAO.findById(contractId);
        if (contract == null) {
            System.out.println("\nContrato com ID " + contractId + " não encontrado.");
            return;
        }

        Indexes index = indexDAO.findById(contract.getCdindex());
        String indexName = (index != null) ? index.getNmindex() : "N/A";

        List<Installments> installments = installmentDAO.findByContractId(contractId);
        if (installments.isEmpty()) {
            System.out.println("\nNenhuma parcela encontrada para o contrato ID: " + contractId);
            return;
        }

        System.out.println("\n=======================================================================================================");
        System.out.printf("                                RELATÓRIO FINANCEIRO - VENDA (Contrato #%d)\n", contractId);
        System.out.println("=======================================================================================================");
        System.out.printf("%-12s | %-15s | %-15s | %-15s | %-15s | %-20s\n",
                "ID Parcela", "Vencimento", "Valor Original", "Índice Correção", "Valor Corrigido", "Data Última Correção");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        for (Installments inst : installments) {
            // Aplica a lógica de negócio para correção monetária
            financialService.applyInflationAdjustment(inst, contract.getCdindex());

            double correctedValue = inst.getVladjusted() > 0 ? inst.getVladjusted() : inst.getVlbase();
            String lastAdjustmentDate = (inst.getDtlastadjustment() != null) 
                                        ? inst.getDtlastadjustment().format(dateFormatter) 
                                        : "Nunca corrigido";

            System.out.printf("%-12d | %-15s | R$ %-13.2f | %-15s | R$ %-13.2f | %-20s\n",
                    inst.getCdinstallment(), inst.getDtdue().format(dateFormatter), inst.getVlbase(),
                    indexName, correctedValue, lastAdjustmentDate);
        }
        System.out.println("=======================================================================================================");
    }

    /**
     * FASE 3: Gera e exibe o relatório sobre as partes de um contrato.
     * @param contractId O ID do contrato.
     */
    public void gerarRelatorioPartesContrato(int contractId) {
        List<ParticipantDTO> participants = userContractDAO.findParticipantsByContractId(contractId);

        if (participants.isEmpty()) {
            System.out.println("\nNenhuma parte encontrada para o contrato ID: " + contractId);
            return;
        }

        System.out.println("\n========================================================================================================================");
        System.out.printf("                                     RELATÓRIO DE PARTES (Contrato #%d)\n", contractId);
        System.out.println("========================================================================================================================");
        System.out.printf("%-25s | %-18s | %-20s | %-15s | %-20s | %-15s\n",
                "Nome/Razão Social", "CPF/CNPJ", "Papel", "Participação", "Contato Principal", "Status Assinatura");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (ParticipantDTO p : participants) {
            System.out.printf("%-25s | %-18s | %-20s | %-15.2f%% | %-20s | %-15s\n",
                    p.getNomeRazaoSocial(),
                    p.getCpfCnpj(),
                    p.getPapelRole(),
                    p.getPercentualParticipacao(),
                    p.getContatoPrincipal(),
                    p.getStatusAssinatura());
        }
        System.out.println("========================================================================================================================");
    }

    /**
     * FASE 3: Gera e exibe o relatório de reajustes previstos para o mês.
     */
    public void gerarRelatorioReajustesDoMes() {
        List<AdjustmentReportDTO> adjustments = automationService.findUpcomingAdjustments();

        if (adjustments.isEmpty()) {
            System.out.println("\nNenhum reajuste de contrato previsto para o mês atual.");
            return;
        }

        System.out.println("\n=======================================================================================================================================");
        System.out.println("                                     RELATÓRIO DE REAJUSTES PREVISTOS PARA O MÊS                                     ");
        System.out.println("=======================================================================================================================================");
        System.out.printf("%-12s | %-25s | %-15s | %-15s | %-15s | %-15s | %-15s\n",
                "ID Contrato", "Cláusula de Referência", "Data Gatilho", "Tipo Notificação", "Valor Atual", "Valor Estimado", "Status Notificação");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");

        for (AdjustmentReportDTO dto : adjustments) {
            System.out.printf("%-12d | %-25s | %-15s | %-15s | R$ %-13.2f | R$ %-13.2f | %-15s\n",
                    dto.getContractId(),
                    dto.getClausulaReferencia(),
                    dto.getDataGatilho().format(dateFormatter),
                    dto.getTipoNotificacao(),
                    dto.getValorAtual(),
                    dto.getValorEstimado(),
                    dto.getStatusNotificacao());
        }
        System.out.println("=======================================================================================================================================");
    }
}