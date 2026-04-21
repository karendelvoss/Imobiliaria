package model;

public class Variables {
    private int cdvariable;      // Primary Key
    private String nmvariable;   // varchar(50) - Nome da tag (ex: {{nome_cliente}})
    private String vlvariable;   // varchar(255) - Valor real
    private String tpvariable;   // varchar - Tipo (Texto, Moeda, Data)
    private boolean fgtriggeralert; // boolean - Disparar alerta?
    private int cdcontract;      // Foreign Key para Contracts

    // Getters e Setters

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
    public String getTpvariable() {
        return tpvariable;
    }
    public void setTpvariable(String tpvariable) {
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