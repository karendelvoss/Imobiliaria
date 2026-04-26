package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Usuários (Pessoas Físicas ou Jurídicas).
 */
public class Users {
    private int cduser;
    private LocalDate dtbirth;
    private boolean fgdocument;
    private String document;
    private String nmuser;
    private String nrcellphone;
    private int cdaddress;
    private int cdoccupation;
    private String dsissuingbody;

    public int getCduser() {
        return cduser;
    }

    public void setCduser(int cduser) {
        this.cduser = cduser;
    }

    public LocalDate getDtbirth() {
        return dtbirth;
    }

    public void setDtbirth(LocalDate dtbirth) {
        this.dtbirth = dtbirth;
    }

    public boolean isFgdocument() {
        return fgdocument;
    }

    public void setFgdocument(boolean fgdocument) {
        this.fgdocument = fgdocument;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getNmuser() {
        return nmuser;
    }

    public void setNmuser(String nmuser) {
        this.nmuser = nmuser;
    }

    public String getNrcellphone() {
        return nrcellphone;
    }

    public void setNrcellphone(String nrcellphone) {
        this.nrcellphone = nrcellphone;
    }

    public int getCdaddress() {
        return cdaddress;
    }

    public void setCdaddress(int cdaddress) {
        this.cdaddress = cdaddress;
    }

    public int getCdoccupation() {
        return cdoccupation;
    }

    public void setCdoccupation(int cdoccupation) {
        this.cdoccupation = cdoccupation;
    }

    public String getDsissuingbody() {
        return dsissuingbody;
    }

    public void setDsissuingbody(String dsissuingbody) {
        this.dsissuingbody = dsissuingbody;
    }
}