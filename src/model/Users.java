package model;
import java.time.LocalDate;

public class Users {
    private int cduser;           // Primary Key
    private LocalDate dtbirth;    // date
    private boolean fgdocument;   // Flag documento (true = CPF, false = CNPJ, etc.)
    private String document;      // varchar(20) - ex: CPF ou CNPJ
    private String nmuser;        // varchar(100) - Nome do usuário
    private String nrcellphone;   // varchar(15) - Telefone
    private int cdaddress;        // Foreign Key para Addresses
    private int cdoccupation;     // Foreign Key para Occupations

    // Getters e Setters

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
}