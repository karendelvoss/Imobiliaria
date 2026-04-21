package model;

public class Countries {
    private int cdcountry;    // Primary Key
    private String nmcountry; // varchar(60) - corrigido nmcounty
    private String sgcountry; // char(3) - alterado de int para String

    // Getters e Setters
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