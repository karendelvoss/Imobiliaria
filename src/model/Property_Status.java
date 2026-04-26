package model;

/**
 * Representa o status de um imóvel (ex: Disponível, Alugado).
 */
public class Property_Status {
    private int cdstatus;
    private String nmstatus;

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