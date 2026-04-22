package service;

import dao.IndexRateDAO;
import model.Installments;
import model.Index_Rates;
import model.InstallmentStatus;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Camada de Serviço para Lógicas de Negócio Financeiras.
 * Responsável por orquestrar DAOs e aplicar regras de negócio
 * como juros, multas e reajustes, mantendo o código limpo e
 * desacoplado da camada de acesso a dados (DAO) e da apresentação (View).
 */
public class FinancialService {

    // Constantes para as regras de juros e multa (boa prática - Sonar)
    // RF006: Multa de 2% sobre o valor base por atraso.
    private static final double LATE_FEE_PERCENTAGE = 0.02; 
    // RF006: Juros de 0.033% ao dia.
    private static final double DAILY_INTEREST_PERCENTAGE = 0.00033; 
    private final IndexRateDAO indexRateDAO;

    public FinancialService() {
        this.indexRateDAO = new IndexRateDAO();
    }

    /**
     * RF006 - Calcula juros e multa para uma parcela de locação em atraso.
     * A lógica é aplicada se a data de vencimento for anterior à data atual
     * e a parcela ainda estiver com status "Pendente".
     *
     * @param installment A parcela a ser calculada.
     * @return A própria parcela com o campo 'vladjusted' atualizado. 
     *         Retorna a parcela sem modificação se não estiver em atraso.
     */
    public Installments calculateLateFeesAndInterest(Installments installment) {
        // Verifica se a parcela está em atraso e pendente de pagamento
        if (installment.getDtdue().isBefore(LocalDate.now()) && installment.getCdstatus() == InstallmentStatus.PENDENTE.getCode()) {
            
            long overdueDays = ChronoUnit.DAYS.between(installment.getDtdue(), LocalDate.now());
            if (overdueDays <= 0) return installment;

            double penalty = installment.getVlbase() * LATE_FEE_PERCENTAGE;
            double interest = (installment.getVlbase() * DAILY_INTEREST_PERCENTAGE) * overdueDays;
            double newTotalValue = installment.getVlbase() + penalty + interest;
            installment.setVladjusted(newTotalValue);

            System.out.printf("CÁLCULO DE JUROS: Parcela #%d - Atraso: %d dias | Multa: R$%.2f | Juros: R$%.2f | Novo Total: R$%.2f\n",
                installment.getNrinstallment(), overdueDays, penalty, interest, newTotalValue);
        }
        
        return installment;
    }

    /**
     * RF007 - Aplica o reajuste anual a uma parcela com base em um índice. (Implementação futura)
     */
    public Installments applyInflationAdjustment(Installments installment, int cdindex) {
        // 1. Busca a taxa mais recente para o índice do contrato
        Index_Rates latestRate = indexRateDAO.findLatestById(cdindex);

        // 2. Verifica se a taxa existe e se a parcela está pendente
        if (latestRate != null && installment.getCdstatus() == InstallmentStatus.PENDENTE.getCode()) {
            
            // REGRA DE NEGÓCIO: Aplicar o reajuste apenas uma vez.
            // Verificamos se a parcela já foi reajustada.
            if (installment.getDtlastadjustment() == null) {
                double rateValue = latestRate.getVlrate(); // Ex: 0.0045
                double adjustedValue = installment.getVlbase() * (1 + rateValue);
                
                installment.setVladjusted(adjustedValue);
                installment.setDtlastadjustment(LocalDate.now()); // Marca a data do reajuste

                System.out.printf("REAJUSTE APLICADO: Parcela #%d | Taxa: %.4f%% | Valor Corrigido: R$%.2f\n",
                    installment.getNrinstallment(), rateValue * 100, adjustedValue);
            }
        }
        return installment;
    }
}