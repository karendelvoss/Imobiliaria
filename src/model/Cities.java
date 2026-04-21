package model;

public class Cities {
    private int cdcity;      // Primary Key
    private String nmcity;   // varchar(60)
    private int cdstate;     // Foreign Key para States

    // Getters e Setters
    
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