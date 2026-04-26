package model;

/**
 * Representa a entidade de Cláusulas de contrato.
 */
public class Clauses {
    private int cdclause;
    private String dstext;
    private int cdtopic;
    private int nrorder;

    public int getCdclause() {
        return cdclause;
    }

    public void setCdclause(int cdclause) {
        this.cdclause = cdclause;
    }

    public String getDstext() {
        return dstext;
    }

    public void setDstext(String dstext) {
        this.dstext = dstext;
    }

    public int getCdtopic() {
        return cdtopic;
    }

    public void setCdtopic(int cdtopic) {
        this.cdtopic = cdtopic;
    }

    public int getNrorder() {
        return nrorder;
    }

    public void setNrorder(int nrorder) {
        this.nrorder = nrorder;
    }
}