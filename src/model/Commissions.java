package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Comissões.
 */
public class Commissions {
    private int cdcommission;
    private double vlcommission;
    private LocalDate dtpayment;
    private int cdcontract;
    private int cduser;

    public int getCdcommission() {
        return cdcommission;
    }

    public void setCdcommission(int cdcommission) {
        this.cdcommission = cdcommission;
    }

    public double getVlcommission() {
        return vlcommission;
    }

    public void setVlcommission(double vlcommission) {
        this.vlcommission = vlcommission;
    }

    public LocalDate getDtpayment() {
        return dtpayment;
    }

    public void setDtpayment(LocalDate dtpayment) {
        this.dtpayment = dtpayment;
    }

    public int getCdcontract() {
        return cdcontract;
    }

    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }

    public int getCduser() {
        return cduser;
    }

    public void setCduser(int cduser) {
        this.cduser = cduser;
    }
}