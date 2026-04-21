package model;

public class Property_Status {
    private int cdstatus;   // PK 
    private String nmstatus; // varchar - Ex: Disponível, Alugado, Vendido 

    // Getters e Setters

    public int getCdstatus() {
        return cdstatus;
    }
    public void setCdstatus(int cdstatus) {
        this.cdstatus = cdstatus;
    }
    public String getNmstatus() {
        return nmstatus;
    }
    public void setNmstatus(String nmstatus) {
        this.nmstatus = nmstatus;
    }
}