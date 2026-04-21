package model;
import java.time.LocalDate;

public class Commissions {
    private int cdcommission;  // Primary Key
    private double vlcommission; // decimal(10,2)
    private LocalDate dtpayment; // data de pagamento
    private int cdcontract;    // Foreign Key para Contracts
    private int cduser;        // Foreign Key para Users (Corretor)

    // Getters e Setters

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