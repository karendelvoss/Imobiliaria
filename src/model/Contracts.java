package model;

import java.time.LocalDate;

public class Contracts {
    private int cdcontract;    // Primary Key
    private LocalDate dtcreation; // date
    private String dstitle;    // varchar(100) - Corrigido de astitie
    private int cdtemplate;    // Foreign Key para Contract_Templates
    private int cdproperty;    // Foreign Key para Properties
    private int cdindex;       // Foreign Key para Indexes

    // Getters e Setters
    public int getCdcontract() {
        return cdcontract;
    }
    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }
    public LocalDate getDtcreation() {
        return dtcreation;
    }
    public void setDtcreation(LocalDate dtcreation) {
        this.dtcreation = dtcreation;
    }
    public String getDstitle() {
        return dstitle;
    }
    public void setDstitle(String dstitle) {
        this.dstitle = dstitle;
    }
    public int getCdtemplate() {
        return cdtemplate;
    }
    public void setCdtemplate(int cdtemplate) {
        this.cdtemplate = cdtemplate;
    }
    public int getCdproperty() {
        return cdproperty;
    }
    public void setCdproperty(int cdproperty) {
        this.cdproperty = cdproperty;
    }
    public int getCdindex() {
        return cdindex;
    }
    public void setCdindex(int cdindex) {
        this.cdindex = cdindex;
    }
}