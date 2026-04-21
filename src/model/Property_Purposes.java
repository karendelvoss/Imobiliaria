package model;

public class Property_Purposes {
    private int cdpurpose;   // PK 
    private String nmpurpose; // varchar - Ex: Residencial, Comercial 

    // Getters e Setters
    
    public int getCdpurpose() {
        return cdpurpose;
    }
    public void setCdpurpose(int cdpurpose) {
        this.cdpurpose = cdpurpose;
    }
    public String getNmpurpose() {
        return nmpurpose;
    }
    public void setNmpurpose(String nmpurpose) {
        this.nmpurpose = nmpurpose;
    }


}