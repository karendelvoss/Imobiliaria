package service;

import dao.ContractDAO;
import dao.IndexDAO;
import dao.InstallmentDAO;
import dto.AdjustmentReportDTO;
import model.Contracts;
import model.Indexes;
import model.Installments;
import model.NotificationStatus;
import model.NotificationType;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de Serviço para lógicas de negócio proativas e baseadas em tempo.
 */
public class AutomationService {

    private final ContractDAO contractDAO;
    private final InstallmentDAO installmentDAO;
    private final FinancialService financialService;
    private final IndexDAO indexDAO;

    public AutomationService() {
        this.contractDAO = new ContractDAO();
        this.installmentDAO = new InstallmentDAO();
        this.financialService = new FinancialService();
        this.indexDAO = new IndexDAO();
    }

    /**
     * Identifica todos os contratos que fazem "aniversário" no mês corrente
     * e são elegíveis para reajuste de valor.
     * @return Uma lista de DTOs com os dados para o relatório de próximas ações.
     */
    public List<AdjustmentReportDTO> findUpcomingAdjustments() {
        List<AdjustmentReportDTO> reportList = new ArrayList<>();
        Month currentMonth = LocalDate.now().getMonth();

        List<Contracts> contracts = contractDAO.findAllWithAdjustmentIndex();

        for (Contracts contract : contracts) {
            // REGRA: Verifica se o "aniversário" do contrato é no mês atual.
            // CORREÇÃO: Adicionada verificação para garantir que o reajuste só ocorra
            // a partir do primeiro aniversário (no ano seguinte à criação).
            if (contract.getDtcreation().getMonth() == currentMonth && contract.getDtcreation().getYear() < LocalDate.now().getYear()) {
                Installments lastPendingInstallment = installmentDAO.findLastPendingInstallmentByContractId(contract.getCdcontract());

                if (lastPendingInstallment != null) {
                    // Simula o reajuste para obter o valor estimado
                    Installments simulatedInstallment = createInstallmentCopy(lastPendingInstallment);
                    financialService.applyInflationAdjustment(simulatedInstallment, contract.getCdindex());

                    Indexes index = indexDAO.findById(contract.getCdindex());
                    String indexName = (index != null) ? index.getNmindex() : "Desconhecido";

                    AdjustmentReportDTO dto = new AdjustmentReportDTO();
                    dto.setContractId(contract.getCdcontract());
                    dto.setClausulaReferencia("Reajuste Anual " + indexName);
                    dto.setDataGatilho(lastPendingInstallment.getDtdue().minusDays(5)); // Gatilho D-5
                    dto.setTipoNotificacao(NotificationType.EMAIL.getDescription());
                    dto.setValorAtual(lastPendingInstallment.getVlbase());
                    dto.setValorEstimado(simulatedInstallment.getVladjusted());
                    dto.setStatusNotificacao(NotificationStatus.AGENDADA.getDescription());
                    
                    reportList.add(dto);
                }
            }
        }
        return reportList;
    }

    // Cria uma cópia do objeto para evitar alterar o original em memória durante a simulação.
    private Installments createInstallmentCopy(Installments original) {
        Installments copy = new Installments();
        // Copia apenas os campos necessários para o cálculo
        copy.setVlbase(original.getVlbase());
        copy.setCdstatus(original.getCdstatus());
        copy.setDtlastadjustment(original.getDtlastadjustment());
        return copy;
    }
}