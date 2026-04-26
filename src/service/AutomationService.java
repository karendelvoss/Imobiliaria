package service;

import dao.ContractDAO;
import dao.IndexDAO;
import dao.InstallmentDAO;
import dto.AdjustmentReportDTO;
import model.ContractRenewalType;
import model.ContractStatus;
import model.Contracts;
import model.Indexes;
import model.InstallmentStatus;
import model.Installments;
import model.NotificationChannel;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por automações de negócio proativas, como reajustes anuais e renovações de contrato.
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
     * Identifica contratos elegíveis para reajuste de valor no ano corrente.
     * 
     * @return Lista de DTOs com dados para relatório de ajustes.
     */
    public List<AdjustmentReportDTO> findUpcomingAdjustments() {
        List<AdjustmentReportDTO> reportList = new ArrayList<>();
        List<Contracts> contracts = contractDAO.findAllWithAdjustmentIndex();

        for (Contracts contract : contracts) {
            // REGRA: Verifica se o contrato tem aniversário no ano atual.
            // O reajuste ocorre no ano seguinte à criação.
            if (contract.getDtcreation().getYear() < LocalDate.now().getYear()) {
                Installments lastPendingInstallment = installmentDAO.findLastPendingInstallmentByContractId(contract.getCdcontract());

                if (lastPendingInstallment != null) {
                    // Simula o reajuste para obter o valor estimado
                    Installments simulatedInstallment = createInstallmentCopy(lastPendingInstallment);
                    financialService.applyInflationAdjustment(simulatedInstallment, contract.getCdindex());

                    Indexes index = indexDAO.findById(contract.getCdindex());
                    String indexName = (index != null) ? index.getNmindex() : "Desconhecido";

                    AdjustmentReportDTO dto = new AdjustmentReportDTO();
                    dto.setContractId(contract.getCdcontract());
                    dto.setDataInicioContrato(contract.getDtcreation());
                    dto.setDataFimContrato(contract.getDtlimit());
                    dto.setStatusContrato(ContractStatus.fromCode(contract.getCdstatus()).getDescription());
                    dto.setIndiceReajuste(indexName);
                    
                    // Data de reajuste do contrato: Aniversário no ano atual
                    LocalDate anniversaryDate = contract.getDtcreation().withYear(LocalDate.now().getYear());
                    dto.setDataReajuste(anniversaryDate); 
                    
                    // Gatilho de notificação: 1 mês antes do aniversário do contrato
                    dto.setDataGatilho(anniversaryDate.minusMonths(1)); 
                    
                    dto.setTipoNotificacao(NotificationChannel.EMAIL.getDescription());
                    dto.setValorAtual(lastPendingInstallment.getVlbase());
                    dto.setValorEstimado(simulatedInstallment.getVladjusted());
                    dto.setStatusNotificacao("Agendada");
                    
                    reportList.add(dto);
                }
            }
        }
        
        // Ordena por data de reajuste para facilitar a visualização anual
        reportList.sort((d1, d2) -> d1.getDataReajuste().compareTo(d2.getDataReajuste()));
        return reportList;
    }

    // Cria uma cópia do objeto para evitar alterar o original em memória durante a simulação.
    private Installments createInstallmentCopy(Installments original) {
        Installments copy = new Installments();
        // Copia apenas os campos necessários para o cálculo
        copy.setVlbase(original.getVlbase());
        copy.setCdstatus(original.getCdstatus());
        copy.setDtlastadjustment(original.getDtlastadjustment());
        copy.setVladjusted(original.getVladjusted());
        return copy;
    }

    /**
     * Job mensal para processar reajustes em contratos de longo prazo.
     * Deve atualizar as parcelas pendentes com o novo valor reajustado.
     */
    public void processMonthlyAdjustments() {
        Month currentMonth = LocalDate.now().getMonth();
        List<Contracts> contracts = contractDAO.findAllWithAdjustmentIndex();

        for (Contracts contract : contracts) {
            // Verifica aniversário: (Mês_Atual - Mês_Início) % 12 == 0 e ano > ano_criação
            if (contract.getDtcreation().getMonth() == currentMonth && contract.getDtcreation().getYear() < LocalDate.now().getYear() && contract.getCdstatus() != ContractStatus.FINALIZADO.getCode()) {
                
                // Busca todas as parcelas pendentes para aplicar o reajuste
                List<Installments> pendingInstallments = installmentDAO.findPendingInstallmentsByContractId(contract.getCdcontract());
                int age = LocalDate.now().getYear() - contract.getDtcreation().getYear();
                int thresholdParcel = age * 12; // Ex: no 1º ano, só reajusta a parcela 13 em diante.
                
                for (Installments installment : pendingInstallments) {
                    // A lei do inquilinato exige que o valor seja mantido por 12 meses.
                    // Portanto, a parcela 12 pertence ao ano original e não sofre reajuste.
                    if (installment.getNrinstallment() > thresholdParcel) {
                        financialService.applyAndPersistAdjustment(contract, installment);
                    }
                }
            }
        }
    }

    /**
     * Retorna a lista de contratos que expiram no mês e ano atuais.
     * 
     * @return Lista de contratos vencendo.
     */
    public List<Contracts> getExpiringContracts() {
        return contractDAO.findExpiringContracts(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
    }

    /**
     * Gerencia a expiração de um contrato, decidindo entre encerramento ou renovação.
     * 
     * @param contract Objeto do contrato.
     * @param type Tipo de renovação.
     * @param newInstallmentsCount Quantidade de novas parcelas.
     */
    public void handleContractExpiration(Contracts contract, ContractRenewalType type, int newInstallmentsCount) {
        if (type == ContractRenewalType.ENCERRAMENTO) {
            contract.setCdstatus(ContractStatus.FINALIZADO.getCode());
            contractDAO.updateContract(contract);
            System.out.println("Contrato encerrado.");
            return;
        }

        if (type == ContractRenewalType.INDETERMINADO) {
            contract.setCdstatus(ContractStatus.INDETERMINADO.getCode());
            contractDAO.updateContract(contract);
            newInstallmentsCount = 12; // Blocos de 12 meses
        }

        // Calcula o novo valor base (aplicando o reajuste dos últimos 12 meses, se houver índice)
        double newBaseValue = 0;
        Installments lastInstallment = installmentDAO.findLastPendingInstallmentByContractId(contract.getCdcontract());
        if (lastInstallment == null) { // Se não tem pendente, busca a última paga (simplificado pegando a última pelo nrinstallment)
            List<Installments> all = installmentDAO.findByContractId(contract.getCdcontract());
            if (!all.isEmpty()) {
                lastInstallment = all.get(all.size() - 1);
            }
        }
        
        if (lastInstallment != null) {
            newBaseValue = lastInstallment.getVladjusted() > 0 ? lastInstallment.getVladjusted() : lastInstallment.getVlbase();
            
            // Se a renovação está acontecendo no mês de aniversário, as novas parcelas
            // já devem nascer com o valor reajustado (pois o Job de reajuste não as enxergou).
            if (contract.getDtcreation().getMonth() == LocalDate.now().getMonth() && contract.getCdindex() > 0) {
                Installments simulated = createInstallmentCopy(lastInstallment);
                simulated.setCdstatus(InstallmentStatus.PENDENTE.getCode());
                simulated.setDtlastadjustment(null); // Força a simulação
                simulated = financialService.applyInflationAdjustment(simulated, contract.getCdindex());
                if (simulated.getVladjusted() > newBaseValue) {
                    newBaseValue = simulated.getVladjusted();
                }
            }
        } else {
            System.err.println("Erro: Nenhuma parcela anterior encontrada para referenciar o valor base.");
            return;
        }

        // Gera as novas parcelas
        List<Installments> newInstallments = new ArrayList<>();
        LocalDate startDate = lastInstallment.getDtdue().plusMonths(1);
        int startNr = lastInstallment.getNrinstallment() + 1;

        for (int i = 0; i < newInstallmentsCount; i++) {
            Installments inst = new Installments();
            inst.setFk_Contracts_cdcontract(contract.getCdcontract());
            inst.setVlbase(newBaseValue);
            inst.setVladjusted(newBaseValue); // Começa igual à base
            inst.setCdstatus(InstallmentStatus.PENDENTE.getCode());
            inst.setDtdue(startDate.plusMonths(i));
            inst.setNrinstallment(startNr + i);
            // Copia os valores de multa e juros da última parcela original (em vez de hardcoded)
            inst.setVlpenalty(lastInstallment.getVlpenalty() > 0 ? lastInstallment.getVlpenalty() : 0.02);
            inst.setVlinterest(lastInstallment.getVlinterest() > 0 ? lastInstallment.getVlinterest() : 0.00033);
            newInstallments.add(inst);
        }

        installmentDAO.insertBatch(newInstallments);
        System.out.println(newInstallmentsCount + " novas parcelas geradas para a renovação.");
    }
}