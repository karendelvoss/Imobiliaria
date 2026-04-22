package model;

public class User_Contract {
    private int cdcontract; // FK para Contracts
    private int cduser;     // FK para Users
    private int cdrole;     // FK para Roles
    private double vlparticipation; // Percentual de participação
    private int fgsignaturestatus;  // Status da assinatura

    // Getters e Setters

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
    public int getCdrole() {
        return cdrole;
    }
    public void setCdrole(int cdrole) {
        this.cdrole = cdrole;
    }
    public double getVlparticipation() {
        return vlparticipation;
    }
    public void setVlparticipation(double vlparticipation) {
        this.vlparticipation = vlparticipation;
    }
    public int getFgsignaturestatus() {
        return fgsignaturestatus;
    }
    public void setFgsignaturestatus(int fgsignaturestatus) {
        this.fgsignaturestatus = fgsignaturestatus;
    }
}