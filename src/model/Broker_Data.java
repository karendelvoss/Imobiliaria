package model;

/**
 * Representa a entidade de Dados do Corretor (Broker_Data).
 */
public class Broker_Data {
    private String nrcreci;
    private int cduser;

    public String getNrcreci() {
        return nrcreci;
    }

    public void setNrcreci(String nrcreci) {
        this.nrcreci = nrcreci;
    }

    public int getCduser() {
        return cduser;
    }

    public void setCduser(int cduser) {
        this.cduser = cduser;
    }
}