package dto;

import java.time.LocalDate;

/**
 * DTO para carregar os dados do "Relatório de Reajustes do Mês".
 */
public class AdjustmentReportDTO {
    private int contractId;
    private String clausulaReferencia;
    private LocalDate dataGatilho;
    private String tipoNotificacao;
    private double valorAtual;
    private double valorEstimado;
    private String statusNotificacao;

    // Getters e Setters
    public int getContractId() {
        return contractId;
    }
    public void setContractId(int contractId) {
        this.contractId = contractId;
    }
    public String getClausulaReferencia() {
        return clausulaReferencia;
    }
    public void setClausulaReferencia(String clausulaReferencia) {
        this.clausulaReferencia = clausulaReferencia;
    }
    public LocalDate getDataGatilho() {
        return dataGatilho;
    }
    public void setDataGatilho(LocalDate dataGatilho) {
        this.dataGatilho = dataGatilho;
    }
    public String getTipoNotificacao() {
        return tipoNotificacao;
    }
    public void setTipoNotificacao(String tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }
    public double getValorAtual() {
        return valorAtual;
    }
    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }
    public double getValorEstimado() {
        return valorEstimado;
    }
    public void setValorEstimado(double valorEstimado) {
        this.valorEstimado = valorEstimado;
    }
    public String getStatusNotificacao() {
        return statusNotificacao;
    }
    public void setStatusNotificacao(String statusNotificacao) {
        this.statusNotificacao = statusNotificacao;
    }
}