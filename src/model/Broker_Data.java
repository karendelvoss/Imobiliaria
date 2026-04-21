package model;

public class Broker_Data {
    private String nrcreci; // varchar no modelo
    private int cduser;     // Chave Estrangeira para Users

    // Getters e Setters

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