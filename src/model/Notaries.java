package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Dados Notariais (Notaries).
 */
public class Notaries {
    private int cdnotary;
    private int cdcity;
    private String book;
    private String leaf;
    private LocalDate dt;
    private int nrnotary;

    public int getCdnotary() {
        return cdnotary;
    }

    public void setCdnotary(int cdnotary) {
        this.cdnotary = cdnotary;
    }

    public int getCdcity() {
        return cdcity;
    }

    public void setCdcity(int cdcity) {
        this.cdcity = cdcity;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    public LocalDate getDt() {
        return dt;
    }

    public void setDt(LocalDate dt) {
        this.dt = dt;
    }

    public int getNrnotary() {
        return nrnotary;
    }

    public void setNrnotary(int nrnotary) {
        this.nrnotary = nrnotary;
    }
}
