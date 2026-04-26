package model;

/**
 * Representa a entidade de Estados.
 */
public class States {
    private int cdstate;
    private String nmstate;
    private String sgstate;
    private int cdcountry;

    public int getCdstate() {
        return cdstate;
    }

    public void setCdstate(int cdstate) {
        this.cdstate = cdstate;
    }

    public String getNmstate() {
        return nmstate;
    }

    public void setNmstate(String nmstate) {
        this.nmstate = nmstate;
    }

    public String getSgstate() {
        return sgstate;
    }

    public void setSgstate(String sgstate) {
        this.sgstate = sgstate;
    }

    public int getCdcountry() {
        return cdcountry;
    }

    public void setCdcountry(int cdcountry) {
        this.cdcountry = cdcountry;
    }
}