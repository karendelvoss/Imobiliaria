package model;

/**
 * Representa a entidade de Imóveis (Properties).
 * Os campos cdtype, cdpurpose e cdstatus são TEXTO (embarcado no MongoDB).
 */
public class Properties {
    private int cdproperty;
    private String nrregistration;
    private String dsdescription;
    private double vltotalarea;
    private int cdaddress;
    private String cdtype;      // TEXTO - embarcado (ex: "Apartamento", "Casa")
    private String cdpurpose;   // TEXTO - embarcado (ex: "Residencial", "Comercial")
    private String cdstatus;    // TEXTO - embarcado (ex: "Alugado", "Disponível")

    public int getCdproperty() {
        return cdproperty;
    }

    public void setCdproperty(int cdproperty) {
        this.cdproperty = cdproperty;
    }

    public String getNrregistration() {
        return nrregistration;
    }

    public void setNrregistration(String nrregistration) {
        this.nrregistration = nrregistration;
    }

    public String getDsdescription() {
        return dsdescription;
    }

    public void setDsdescription(String dsdescription) {
        this.dsdescription = dsdescription;
    }

    public double getVltotalarea() {
        return vltotalarea;
    }

    public void setVltotalarea(double vltotalarea) {
        this.vltotalarea = vltotalarea;
    }

    public int getCdaddress() {
        return cdaddress;
    }

    public void setCdaddress(int cdaddress) {
        this.cdaddress = cdaddress;
    }

    public String getCdtype() {
        return cdtype;
    }

    public void setCdtype(String cdtype) {
        this.cdtype = cdtype;
    }

    public String getCdpurpose() {
        return cdpurpose;
    }

    public void setCdpurpose(String cdpurpose) {
        this.cdpurpose = cdpurpose;
    }

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }
}
