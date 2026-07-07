package model;

/**
 * Representa a entidade de Endereços.
 *
 * No modelo MongoDB, endereços são embarcados como subdocumentos dentro de
 * {@code users} e {@code properties}. Os campos de localização (district,
 * city, state, country) são armazenados diretamente como texto no subdocumento,
 * substituindo as referências por ID (cddistrict) do modelo relacional.
 */
public class Addresses {
    private int cdaddress;
    private String cdzipcode;
    private String nmstreet;
    private String nraddress;
    private String dscomplement;
    private int cddistrict; // mantido para retrocompatibilidade durante migração
    private String district;
    private String city;
    private String state;
    private String country;

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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
