package model;

import java.time.LocalDate;

/**
 * Representa um registro de log de reajuste financeiro.
 */
public class ReadjustmentLog {
    private int cdlog;
    private int cdcontract;
    private int cdinstallment;
    private int cdindex;
    private double vlold;
    private double vlnew;
    private LocalDate dtreadjustment;

    public int getCdlog() {
        return cdlog;
    }

    public void setCdlog(int cdlog) {
        this.cdlog = cdlog;
    }

    public int getCdcontract() {
        return cdcontract;
    }

    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }

    public int getCdinstallment() {
        return cdinstallment;
    }

    public void setCdinstallment(int cdinstallment) {
        this.cdinstallment = cdinstallment;
    }

    public int getCdindex() {
        return cdindex;
    }

    public void setCdindex(int cdindex) {
        this.cdindex = cdindex;
    }

    public double getVlold() {
        return vlold;
    }

    public void setVlold(double vlold) {
        this.vlold = vlold;
    }

    public double getVlnew() {
        return vlnew;
    }

    public void setVlnew(double vlnew) {
        this.vlnew = vlnew;
    }

    public LocalDate getDtreadjustment() {
        return dtreadjustment;
    }

    public void setDtreadjustment(LocalDate dtreadjustment) {
        this.dtreadjustment = dtreadjustment;
    }
}
