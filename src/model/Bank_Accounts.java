package model;

public class Bank_Accounts {
    private int cdbankaccount; // Primary Key
    private String nragency;   // varchar(30) no modelo 
    private String nraccount;  // varchar(30) no modelo 
    private String nrpixkey;   // varchar(100) no modelo 
    private int cduser;        // Foreign Key para Users   

    // Getters e Setters
    
    public int getCdbankaccount() {
        return cdbankaccount;
    }
    public void setCdbankaccount(int cdbankaccount) {
        this.cdbankaccount = cdbankaccount;
    }
    public String getNragency() {
        return nragency;
    }
    public void setNragency(String nragency) {
        this.nragency = nragency;
    }
    public String getNraccount() {
        return nraccount;
    }
    public void setNraccount(String nraccount) {
        this.nraccount = nraccount;
    }
    public String getNrpixkey() {
        return nrpixkey;
    }
    public void setNrpixkey(String nrpixkey) {
        this.nrpixkey = nrpixkey;
    }
    public int getCduser() {
        return cduser;
    }
    public void setCduser(int cduser) {
        this.cduser = cduser;
    }
}