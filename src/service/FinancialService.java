package service;

import dao.IndexRateDAO;
import model.Installments;
import model.Index_Rates;
import model.InstallmentStatus;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import dao.InstallmentDAO;
import dao.ReadjustmentLogDAO;
import model.Contracts;
import model.ReadjustmentLog;

/**
 * Serviço responsável por lógicas de negócio financeiras, incluindo cálculo de multas, juros e reajustes por inflação.
 */
public class FinancialService {

    private final IndexRateDAO indexRateDAO;
    private final InstallmentDAO installmentDAO;
    private final ReadjustmentLogDAO readjustmentLogDAO;

    public FinancialService() {
        this.indexRateDAO = new IndexRateDAO();
        this.installmentDAO = new InstallmentDAO();
        this.readjustmentLogDAO = new ReadjustmentLogDAO();
    }

    /**
     * Calcula juros e multa para uma parcela em atraso.
     * 
     * @param installment Objeto da parcela.
     * @return Parcela com o campo vladjusted atualizado, se aplicável.
     */
    public Installments calculateLateFeesAndInterest(Installments installment) {
        // Verifica se a parcela está em atraso e pendente de pagamento
        if (installment.getDtdue().isBefore(LocalDate.now()) && installment.getCdstatus() == InstallmentStatus.PENDENTE.getCode()) {
            
            long overdueDays = ChronoUnit.DAYS.between(installment.getDtdue(), LocalDate.now());
            if (overdueDays <= 0) return installment;

            double penalty = installment.getVlbase() * installment.getVlpenalty();
            double interest = (installment.getVlbase() * installment.getVlinterest()) * overdueDays;
            double newTotalValue = installment.getVlbase() + penalty + interest;
            installment.setVladjusted(newTotalValue);

            System.out.printf("CÁLCULO DE JUROS: Parcela #%d - Atraso: %d dias | Multa: R$%.2f | Juros: R$%.2f | Novo Total: R$%.2f\n",
                installment.getNrinstallment(), overdueDays, penalty, interest, newTotalValue);
        }
        
        return installment;
    }

    /**
     * Aplica o reajuste anual a uma parcela com base no índice financeiro acumulado de 12 meses.
     * 
     * @param installment Objeto da parcela.
     * @param cdindex Identificador do índice financeiro.
     * @return Parcela com o valor reajustado em memória.
     */
    public Installments applyInflationAdjustment(Installments installment, int cdindex) {
        LocalDate baseDate = LocalDate.now();
        // Busca as taxas dos últimos 12 meses
        List<Index_Rates> rates = indexRateDAO.findLast12MonthsRates(cdindex, baseDate);

        // Fallback de Índice: Se encontrar 11, busca a janela imediatamente anterior com 12 meses
        if (rates.size() == 11) {
            System.out.println("Fallback de Índice acionado: Último mês não homologado. Deslocando janela em 1 mês.");
            baseDate = baseDate.minusMonths(1);
            rates = indexRateDAO.findLast12MonthsRates(cdindex, baseDate);
        }

        // Alerta de Vacância
        if (!rates.isEmpty()) {
            Index_Rates lastRate = rates.get(rates.size() - 1);
            LocalDate lastRateDate = LocalDate.of(lastRate.getRefyear(), lastRate.getRefmonth(), 1);
            LocalDate endOfRateMonth = lastRateDate.withDayOfMonth(lastRateDate.lengthOfMonth());
            long defasagem = ChronoUnit.DAYS.between(endOfRateMonth, LocalDate.now());
            
            if (defasagem > 45) {
                System.err.println("ALERTA DE VACÂNCIA: A base de índices está defasada em " + defasagem + " dias (mais de 45 dias).");
            }
        }

        if (rates.size() == 12 && installment.getCdstatus() == InstallmentStatus.PENDENTE.getCode()) {
            if (installment.getDtlastadjustment() == null || 
                ChronoUnit.MONTHS.between(installment.getDtlastadjustment(), LocalDate.now()) >= 11) {
                
                double accumulatedRate = 1.0;
                for (Index_Rates rate : rates) {
                    accumulatedRate *= (1 + rate.getVlrate());
                }
                
                // Ex: V_novo = V_antigo * PROD(1 + i_t)
                double currentValue = installment.getVladjusted() > 0 ? installment.getVladjusted() : installment.getVlbase();
                double adjustedValue = currentValue * accumulatedRate;
                
                installment.setVladjusted(adjustedValue);
                // Não salva no banco, apenas memória
            }
        }
        return installment;
    }

    /**
     * Aplica o reajuste em uma parcela, persiste no banco de dados e gera o log de auditoria.
     * 
     * @param contract Objeto do contrato associado.
     * @param installment Objeto da parcela.
     */
    public void applyAndPersistAdjustment(Contracts contract, Installments installment) {
        if (installment.getCdstatus() != InstallmentStatus.PENDENTE.getCode()) {
            return;
        }

        double oldVal = installment.getVladjusted() > 0 ? installment.getVladjusted() : installment.getVlbase();
        
        // Simula o ajuste na memória
        Installments simulated = applyInflationAdjustment(installment, contract.getCdindex());
        
        if (simulated.getVladjusted() > oldVal) {
            double newVal = simulated.getVladjusted();
            
            // Atualiza a parcela
            installment.setVladjusted(newVal);
            installment.setDtlastadjustment(LocalDate.now());
            installmentDAO.update(installment);
            
            // Grava o log de auditoria
            ReadjustmentLog log = new ReadjustmentLog();
            log.setCdcontract(contract.getCdcontract());
            log.setCdinstallment(installment.getCdinstallment());
            log.setCdindex(contract.getCdindex());
            log.setVlold(oldVal);
            log.setVlnew(newVal);
            log.setDtreadjustment(LocalDate.now());
            
            readjustmentLogDAO.insert(log);
            
            System.out.printf("REAJUSTE SALVO: Contrato #%d | Parcela #%d | Velho: R$%.2f | Novo: R$%.2f\n",
                contract.getCdcontract(), installment.getNrinstallment(), oldVal, newVal);
        }
    }
}