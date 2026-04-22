package model;

public class Index_Rates {
    private int cdrate;     // Primary Key
    private int refmonth;   // mês de referência
    private int refyear;    // ano de referência
    private double vlrate;  // decimal - valor da taxa
    private int fk_Indexes_cdindex;    // Foreign Key para Indexes

    // Getters e Setters

    public int getCdrate() {
        return cdrate;
    }
    public void setCdrate(int cdrate) {
        this.cdrate = cdrate;
    }
    public int getRefmonth() {
        return refmonth;
    }
    public void setRefmonth(int refmonth) {
        this.refmonth = refmonth;
    }
    public int getRefyear() {
        return refyear;
    }
    public void setRefyear(int refyear) {
        this.refyear = refyear;
    }
    public double getVlrate() {
        return vlrate;
    }
    public void setVlrate(double vlrate) {
        this.vlrate = vlrate;
    }
    public int getFk_Indexes_cdindex() {
        return fk_Indexes_cdindex;
    }
    public void setFk_Indexes_cdindex(int fk_Indexes_cdindex) {
        this.fk_Indexes_cdindex = fk_Indexes_cdindex;
    }

}