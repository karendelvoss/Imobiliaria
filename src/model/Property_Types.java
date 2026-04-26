package model;

/**
 * Representa o tipo de imóvel (ex: Casa, Apartamento).
 */
public class Property_Types {
    private int cdtype;
    private String nmtype;

    public int getCdtype() {
        return cdtype;
    }

    public void setCdtype(int cdtype) {
        this.cdtype = cdtype;
    }

    public String getNmtype() {
        return nmtype;
    }

    public void setNmtype(String nmtype) {
        this.nmtype = nmtype;
    }
}