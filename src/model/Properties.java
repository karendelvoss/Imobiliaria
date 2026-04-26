package model;

/**
 * Representa a entidade de Imóveis (Properties).
 */
public class Properties {
    private int cdproperty;
    private String nrregistration;
    private String dsdescription;
    private double vltotalarea;
    private int cdaddress;
    private int cdtype;
    private int cdpurpose;
    private int cdstatus;

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

    public int getCdtype() {
        return cdtype;
    }

    public void setCdtype(int cdtype) {
        this.cdtype = cdtype;
    }

    public int getCdpurpose() {
        return cdpurpose;
    }

    public void setCdpurpose(int cdpurpose) {
        this.cdpurpose = cdpurpose;
    }

    public int getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(int cdstatus) {
        this.cdstatus = cdstatus;
    }
}