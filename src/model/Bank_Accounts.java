package model;

/**
 * Representa a entidade de Contas Bancárias.
 */
public class Bank_Accounts {
    private int cdbankaccount;
    private String nragency;
    private String nraccount;
    private String nrpixkey;
    private int cduser;

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