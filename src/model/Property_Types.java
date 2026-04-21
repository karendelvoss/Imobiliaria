package model;

public class Property_Types {
    private int cdtype;   // PK 
    private String nmtype; // varchar - Ex: Casa, Apartamento, Terreno 

    // Getters e Setters
    
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