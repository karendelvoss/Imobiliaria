package model;

import java.time.LocalDate;

public class Installments {
    private int cdinstallment;   // Primary Key
    private LocalDate dtdue;      // data de vencimento
    private double vlbase;        // valor original
    private double vladjusted;    // valor após reajuste
    private int cdstatus;         // status da parcela (ex: Pago, Pendente)
    private LocalDate dtpayment;  // data em que foi paga
    private int nrinstallment;    // número da parcela (ex: 1, 2, 3...)
    private int cdcontract;       // Foreign Key para Contracts

    // Getters e Setters

    public int getCdinstallment() {
        return cdinstallment;
    }
    public void setCdinstallment(int cdinstallment) {
        this.cdinstallment = cdinstallment;
    }
    public LocalDate getDtdue() {
        return dtdue;
    }
    public void setDtdue(LocalDate dtdue) {
        this.dtdue = dtdue;
    }
    public double getVlbase() {
        return vlbase;
    }
    public void setVlbase(double vlbase) {
        this.vlbase = vlbase;
    }
    public double getVladjusted() {
        return vladjusted;
    }
    public void setVladjusted(double vladjusted) {
        this.vladjusted = vladjusted;
    }
    public int getCdstatus() {
        return cdstatus;
    }
    public void setCdstatus(int cdstatus) {
        this.cdstatus = cdstatus;
    }
    public LocalDate getDtpayment() {
        return dtpayment;
    }
    public void setDtpayment(LocalDate dtpayment) {
        this.dtpayment = dtpayment;
    }
    public int getNrinstallment() {
        return nrinstallment;
    }
    public void setNrinstallment(int nrinstallment) {
        this.nrinstallment = nrinstallment;
    }
    public int getCdcontract() {
        return cdcontract;
    }
    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }
}