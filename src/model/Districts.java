package model;

public class Districts {
    private int cddistrict;   // Primary Key 
    private String nmdistrict; // varchar(60) 
    private int cdcity;       // Foreign Key para Cities 

    // Getters e Setters
    
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