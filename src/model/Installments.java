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
    private int fk_Contracts_cdcontract; // Foreign Key para Contracts
    private LocalDate dtlastadjustment; // data da última correção

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
    public int getFk_Contracts_cdcontract() {
        return fk_Contracts_cdcontract;
    }
    public void setFk_Contracts_cdcontract(int fk_Contracts_cdcontract) {
        this.fk_Contracts_cdcontract = fk_Contracts_cdcontract;
    }
    public LocalDate getDtlastadjustment() {
        return dtlastadjustment;
    }
    public void setDtlastadjustment(LocalDate dtlastadjustment) {
        this.dtlastadjustment = dtlastadjustment;
    }
}