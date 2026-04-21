package model;

public class Template_Topics {
    private int cdtopic;    // FK para Topics
    private int cdtemplate; // FK para Contract_Templates

    // Getters e Setters

    public int getCdtopic() {
        return cdtopic;
    }
    public void setCdtopic(int cdtopic) {
        this.cdtopic = cdtopic;
    }
    public int getCdtemplate() {
        return cdtemplate;
    }
    public void setCdtemplate(int cdtemplate) {
        this.cdtemplate = cdtemplate;
    }
}