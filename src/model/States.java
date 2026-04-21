package model;

public class States {
    private int cdstate;      // Primary Key
    private String nmstate;   // varchar(50)
    private String sgstate;   // char(2) - alterado de int para String
    private int cdcountry;    // Foreign Key para Countries

    // Getters e Setters
    
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