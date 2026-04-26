package model;

/**
 * Representa a entidade de Endereços.
 */
public class Addresses {
    private int cdaddress;
    private String cdzipcode;
    private String nmstreet;
    private String nraddress;
    private String dscomplement;
    private int cddistrict;

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