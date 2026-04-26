package service;

import dao.ContractDAO;
import dao.InstallmentDAO;
import dao.UserContractDAO;
import dao.IndexDAO;
import dto.AdjustmentReportDTO;
import dto.ParticipantDTO;
import model.Contracts;
import model.Indexes;
import model.Installments;
import model.InstallmentStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import dto.CashFlowReportDTO;
import java.util.List;

/**
 * Serviço responsável pela geração e apresentação de relatórios financeiros e de auditoria do sistema.
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
     * Gera um relatório detalhado das parcelas e valores de um contrato de locação.
     * 
     * @param contractId Identificador do contrato.
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
     * Gera um relatório de correção monetária para um contrato de venda ou de longo prazo.
     * 
     * @param contractId Identificador do contrato.
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
     * Lista todos os participantes vinculados a um contrato e seus respectivos papéis.
     * 
     * @param contractId Identificador do contrato.
     */
    public void gerarRelatorioPartesContrato(int contractId) {
        List<ParticipantDTO> participants = userContractDAO.findParticipantsByContractId(contractId);

        if (participants.isEmpty()) {
            System.out.println("\nNenhuma parte encontrada para o contrato ID: " + contractId);
            return;
        }

        System.out.println("\n=========================================================================================");
        System.out.printf("                                     RELATÓRIO DE PARTES (Contrato #%d)\n", contractId);
        System.out.println("=========================================================================================");
        System.out.printf("%-25s | %-18s | %-20s | %-20s\n",
                "Nome/Razão Social", "CPF/CNPJ", "Papel", "Contato Principal");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (ParticipantDTO p : participants) {
            System.out.printf("%-25s | %-18s | %-20s | %-20s\n",
                    p.getNomeRazaoSocial(),
                    p.getCpfCnpj(),
                    p.getPapelRole(),
                    p.getContatoPrincipal());
        }
        System.out.println("=========================================================================================");
    }

    /**
     * Apresenta os reajustes de valor previstos para todos os contratos ativos no ano atual.
     */
    public void gerarRelatorioReajustesDoMes() {
        List<AdjustmentReportDTO> adjustments = automationService.findUpcomingAdjustments();

        if (adjustments.isEmpty()) {
            System.out.println("\nNenhum reajuste de contrato previsto para o ano atual.");
            return;
        }

        System.out.println("\n=====================================================================================================================================================================================================================");
        System.out.println("                                                                      RELATÓRIO DE REAJUSTES PREVISTOS PARA O ANO                                                                      ");
        System.out.println("=====================================================================================================================================================================================================================");
        System.out.printf("%-11s | %-12s | %-12s | %-15s | %-15s | %-12s | %-13s | %-16s | %-18s | %-13s | %-13s\n",
                "ID Contrato", "Data Início", "Data Fim", "Status Contrato", "Índice Reajuste", "Data Gatilho", "Data Reajuste", "Tipo Notificação", "Status Notificação", "Valor Atual", "Valor Ajustado");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (AdjustmentReportDTO dto : adjustments) {
            String dtFim = dto.getDataFimContrato() != null ? dto.getDataFimContrato().format(dateFormatter) : "N/A";
            System.out.printf("%-11d | %-12s | %-12s | %-15s | %-15s | %-12s | %-13s | %-16s | %-18s | R$ %-10.2f | R$ %-10.2f\n",
                    dto.getContractId(),
                    dto.getDataInicioContrato().format(dateFormatter),
                    dtFim,
                    dto.getStatusContrato(),
                    dto.getIndiceReajuste(),
                    dto.getDataGatilho().format(dateFormatter),
                    dto.getDataReajuste().format(dateFormatter),
                    dto.getTipoNotificacao(),
                    dto.getStatusNotificacao(),
                    dto.getValorAtual(),
                    dto.getValorEstimado());
        }
        System.out.println("=====================================================================================================================================================================================================================");
    }

    /**
     * Gera o relatório de fluxo de caixa mensal e inadimplência para um ano específico.
     * 
     * @param year Ano de referência.
     * @param contractId Identificador do contrato (0 para relatório geral).
     */
    public void gerarRelatorioFluxoCaixa(int year, int contractId) {
        List<CashFlowReportDTO> data = installmentDAO.getMonthlyCashFlowReport(year, contractId);

        if (data.isEmpty()) {
            System.out.println("\nNenhum dado financeiro encontrado para o ano de " + year + 
                               (contractId > 0 ? " no contrato #" + contractId : ""));
            return;
        }

        System.out.println("\n=================================================================================================================");
        String title = (contractId > 0) ? "FLUXO DE CAIXA E ADIMPLÊNCIA - CONTRATO #" + contractId : "FLUXO DE CAIXA E ADIMPLÊNCIA - GERAL (Todos os Contratos)";
        System.out.printf("                               %s - ANO %d\n", title, year);
        System.out.println("=================================================================================================================");
        System.out.printf("%-10s | %-18s | %-18s | %-18s | %-18s | %-15s\n",
                "Mês/Ano", "Receita Esperada", "Valor Recebido", "Valor Pendente", "Valor em Atraso", "Inadimplência");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        double totalRecebidoAno = 0;
        double totalEsperadoAno = 0;

        for (CashFlowReportDTO dto : data) {
            double esperada = dto.getReceitaEsperada();
            totalRecebidoAno += dto.getValorRecebido();
            totalEsperadoAno += esperada;

            System.out.printf("%02d/%-7d | R$ %-15.2f | R$ %-15.2f | R$ %-15.2f | R$ %-15.2f | %-13.2f%%\n",
                    dto.getMes(), dto.getAno(), esperada, dto.getValorRecebido(), 
                    dto.getValorPendente(), dto.getValorEmAtraso(), dto.getTaxaInadimplencia());
        }

        double taxaAnual = totalEsperadoAno > 0 ? (totalRecebidoAno / totalEsperadoAno) * 100.0 : 0;
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.printf("TOTAIS ANO:  Receita: R$ %-15.2f | Recebido: R$ %-15.2f | Adimplência Geral: %.2f%%\n",
                totalEsperadoAno, totalRecebidoAno, taxaAnual);
        System.out.println("=================================================================================================================");
    }
}