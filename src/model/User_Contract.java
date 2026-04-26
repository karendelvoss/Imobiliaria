package model;

/**
 * Representa o vínculo entre Usuários, Contratos e seus Papéis.
 */
public class User_Contract {
    private int cdcontract;
    private int cduser;
    private int cdrole;
    private double vlparticipation;
    private int fgsignaturestatus;

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