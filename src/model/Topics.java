package model;

public class Topics {
    private int cdtopic;   // Primary Key - Corrigido de cdtopics
    private String nmtopic; // varchar(100)

    // Getters e Setters

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
}
