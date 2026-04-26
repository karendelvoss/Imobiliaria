package model;

/**
 * Representa a entidade de Cidades.
 */
public class Cities {
    private int cdcity;
    private String nmcity;
    private int cdstate;

    public int getCdcity() {
        return cdcity;
    }

    public void setCdcity(int cdcity) {
        this.cdcity = cdcity;
    }

    public String getNmcity() {
        return nmcity;
    }

    public void setNmcity(String nmcity) {
        this.nmcity = nmcity;
    }

    public int getCdstate() {
        return cdstate;
    }

    public void setCdstate(int cdstate) {
        this.cdstate = cdstate;
    }
}