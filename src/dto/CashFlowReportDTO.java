package dto;

/**
 * Objeto de transferência de dados para o relatório de fluxo de caixa mensal.
 */
public class CashFlowReportDTO {
    private int mes;
    private int ano;
    private double valorRecebido;
    private double valorPendente;
    private double valorEmAtraso;

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(double valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public double getValorPendente() {
        return valorPendente;
    }

    public void setValorPendente(double valorPendente) {
        this.valorPendente = valorPendente;
    }

    public double getValorEmAtraso() {
        return valorEmAtraso;
    }

    public void setValorEmAtraso(double valorEmAtraso) {
        this.valorEmAtraso = valorEmAtraso;
    }

    /**
     * Calcula a receita total esperada no mês.
     * 
     * @return Soma do valor recebido e pendente.
     */
    public double getReceitaEsperada() {
        return valorRecebido + valorPendente;
    }

    /**
     * Calcula a taxa de inadimplência do mês.
     * 
     * @return Percentual de valores em atraso em relação à receita esperada.
     */
    public double getTaxaInadimplencia() {
        double esperada = getReceitaEsperada();
        if (esperada == 0) return 0;
        return (valorEmAtraso / esperada) * 100.0;
    }
}
