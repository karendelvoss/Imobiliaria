package dto;

import java.time.LocalDate;

/**
 * Objeto de transferência de dados para o relatório de reajustes contratuais.
 */
public class AdjustmentReportDTO {
    private int contractId;
    private LocalDate dataInicioContrato;
    private LocalDate dataFimContrato;
    private String statusContrato;
    private String indiceReajuste;
    private LocalDate dataGatilho;
    private LocalDate dataReajuste;
    private String tipoNotificacao;
    private double valorAtual;
    private double valorEstimado;
    private String statusNotificacao;

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public LocalDate getDataInicioContrato() {
        return dataInicioContrato;
    }

    public void setDataInicioContrato(LocalDate dataInicioContrato) {
        this.dataInicioContrato = dataInicioContrato;
    }

    public LocalDate getDataFimContrato() {
        return dataFimContrato;
    }

    public void setDataFimContrato(LocalDate dataFimContrato) {
        this.dataFimContrato = dataFimContrato;
    }

    public String getStatusContrato() {
        return statusContrato;
    }

    public void setStatusContrato(String statusContrato) {
        this.statusContrato = statusContrato;
    }

    public String getIndiceReajuste() {
        return indiceReajuste;
    }

    public void setIndiceReajuste(String indiceReajuste) {
        this.indiceReajuste = indiceReajuste;
    }

    public LocalDate getDataGatilho() {
        return dataGatilho;
    }

    public void setDataGatilho(LocalDate dataGatilho) {
        this.dataGatilho = dataGatilho;
    }

    public LocalDate getDataReajuste() {
        return dataReajuste;
    }

    public void setDataReajuste(LocalDate dataReajuste) {
        this.dataReajuste = dataReajuste;
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