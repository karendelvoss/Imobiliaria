package model;

/**
 * Representa a finalidade do imóvel (ex: Residencial, Comercial).
 */
public class Property_Purposes {
    private int cdpurpose;
    private String nmpurpose;

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