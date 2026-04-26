package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Parcelas (Installments).
 */
public class Installments {
    private int cdinstallment;
    private LocalDate dtdue;
    private double vlbase;
    private double vladjusted;
    private int cdstatus;
    private LocalDate dtpayment;
    private int nrinstallment;
    private int fk_Contracts_cdcontract;
    private LocalDate dtlastadjustment;
    private double vlpenalty;
    private double vlinterest;

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

    public double getVlpenalty() {
        return vlpenalty;
    }

    public void setVlpenalty(double vlpenalty) {
        this.vlpenalty = vlpenalty;
    }

    public double getVlinterest() {
        return vlinterest;
    }

    public void setVlinterest(double vlinterest) {
        this.vlinterest = vlinterest;
    }
}