package model;

public class Contract_Templates {
    private int cdtemplate;    // Primary Key
    private String nmtemplate; // varchar(100)
    private String dsversion;  // varchar(10) - Corrigido de asversion
    private String fgactive;   // char(1) - Geralmente 'Y' ou 'N'

    // Getters e Setters
    
    public int getCdtemplate() {
        return cdtemplate;
    }
    public void setCdtemplate(int cdtemplate) {
        this.cdtemplate = cdtemplate;
    }
    public String getNmtemplate() {
        return nmtemplate;
    }
    public void setNmtemplate(String nmtemplate) {
        this.nmtemplate = nmtemplate;
    }
    public String getDsversion() {
        return dsversion;
    }
    public void setDsversion(String dsversion) {
        this.dsversion = dsversion;
    }
    public String getFgactive() {
        return fgactive;
    }
    public void setFgactive(String fgactive) {
        this.fgactive = fgactive;
    }
}