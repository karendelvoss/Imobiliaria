package model;

/**
 * Representa a entidade de Tópicos de contrato.
 */
public class Topics {
    private int cdtopic;
    private String nmtopic;
    private int nrorder;

    public int getCdtopic() {
        return cdtopic;
    }

    public void setCdtopic(int cdtopic) {
        this.cdtopic = cdtopic;
    }

    public String getNmtopic() {
        return nmtopic;
    }

    public void setNmtopic(String nmtopic) {
        this.nmtopic = nmtopic;
    }

    public int getNrorder() {
        return nrorder;
    }

    public void setNrorder(int nrorder) {
        this.nrorder = nrorder;
    }
}
