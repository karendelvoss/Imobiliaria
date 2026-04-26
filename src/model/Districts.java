package model;

/**
 * Representa a entidade de Bairros (Districts).
 */
public class Districts {
    private int cddistrict;
    private String nmdistrict;
    private int cdcity;

    public int getCddistrict() {
        return cddistrict;
    }

    public void setCddistrict(int cddistrict) {
        this.cddistrict = cddistrict;
    }

    public String getNmdistrict() {
        return nmdistrict;
    }

    public void setNmdistrict(String nmdistrict) {
        this.nmdistrict = nmdistrict;
    }

    public int getCdcity() {
        return cdcity;
    }

    public void setCdcity(int cdcity) {
        this.cdcity = cdcity;
    }
}