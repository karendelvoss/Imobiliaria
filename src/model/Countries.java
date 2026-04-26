package model;

/**
 * Representa a entidade de Países.
 */
public class Countries {
    private int cdcountry;
    private String nmcountry;
    private String sgcountry;

    public int getCdcountry() {
        return cdcountry;
    }

    public void setCdcountry(int cdcountry) {
        this.cdcountry = cdcountry;
    }

    public String getNmcountry() {
        return nmcountry;
    }

    public void setNmcountry(String nmcountry) {
        this.nmcountry = nmcountry;
    }

    public String getSgcountry() {
        return sgcountry;
    }

    public void setSgcountry(String sgcountry) {
        this.sgcountry = sgcountry;
    }
}