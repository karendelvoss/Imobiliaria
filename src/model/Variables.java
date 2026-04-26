package model;

/**
 * Representa as variáveis dinâmicas usadas para preenchimento de templates de contrato.
 */
public class Variables {
    private int cdvariable;
    private String nmvariable;
    private String vlvariable;
    private int tpvariable;
    private boolean fgtriggeralert;
    private int cdcontract;

    public int getCdvariable() {
        return cdvariable;
    }

    public void setCdvariable(int cdvariable) {
        this.cdvariable = cdvariable;
    }

    public String getNmvariable() {
        return nmvariable;
    }

    public void setNmvariable(String nmvariable) {
        this.nmvariable = nmvariable;
    }

    public String getVlvariable() {
        return vlvariable;
    }

    public void setVlvariable(String vlvariable) {
        this.vlvariable = vlvariable;
    }

    public int getTpvariable() {
        return tpvariable;
    }

    public void setTpvariable(int tpvariable) {
        this.tpvariable = tpvariable;
    }

    public boolean isFgtriggeralert() {
        return fgtriggeralert;
    }

    public void setFgtriggeralert(boolean fgtriggeralert) {
        this.fgtriggeralert = fgtriggeralert;
    }

    public int getCdcontract() {
        return cdcontract;
    }

    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }
}