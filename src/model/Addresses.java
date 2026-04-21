package model;

public class Addresses {
    private int cdaddress;     // Primary Key 
    private String cdzipcode;  // char(8) - melhor como String 
    private String nmstreet;   // varchar(100) 
    private String nraddress;  // varchar(10) no modelo 
    private String dscomplement; // varchar(50) - corrigido o nome 
    private int cddistrict;    // Foreign Key para Districts 

    // Getters e Setters

    public int getCdaddress() {
        return cdaddress;
    }
    public void setCdaddress(int cdaddress) {
        this.cdaddress = cdaddress;
    }
    public String getCdzipcode() {
        return cdzipcode;
    }
    public void setCdzipcode(String cdzipcode) {
        this.cdzipcode = cdzipcode;
    }
    public String getNmstreet() {
        return nmstreet;
    }
    public void setNmstreet(String nmstreet) {
        this.nmstreet = nmstreet;
    }
    public String getNraddress() {
        return nraddress;
    }
    public void setNraddress(String nraddress) {
        this.nraddress = nraddress;
    }
    public String getDscomplement() {
        return dscomplement;
    }
    public void setDscomplement(String dscomplement) {
        this.dscomplement = dscomplement;
    }
    public int getCddistrict() {
        return cddistrict;
    }
    public void setCddistrict(int cddistrict) {
        this.cddistrict = cddistrict;
    }
}