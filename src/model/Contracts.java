package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Contratos.
 */
public class Contracts {
    private int cdcontract;
    private LocalDate dtcreation;
    private String dstitle;
    private int cdtemplate;
    private int cdproperty;
    private int cdindex;
    private LocalDate dtlimit;
    private int cdstatus;
    private int cdnotary;

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

    public LocalDate getDtlimit() {
        return dtlimit;
    }

    public void setDtlimit(LocalDate dtlimit) {
        this.dtlimit = dtlimit;
    }

    public int getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(int cdstatus) {
        this.cdstatus = cdstatus;
    }

    public int getCdnotary() {
        return cdnotary;
    }

    public void setCdnotary(int cdnotary) {
        this.cdnotary = cdnotary;
    }
}