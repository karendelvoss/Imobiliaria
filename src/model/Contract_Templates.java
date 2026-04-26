package model;

/**
 * Representa a entidade de Modelos de Contrato (Contract_Templates).
 */
public class Contract_Templates {
    private int cdtemplate;
    private String nmtemplate;
    private String dsversion;
    private boolean fgactive;

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

    public boolean isFgactive() {
        return fgactive;
    }

    public void setFgactive(boolean fgactive) {
        this.fgactive = fgactive;
    }
}