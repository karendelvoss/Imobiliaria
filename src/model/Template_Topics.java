package model;

/**
 * Representa o vínculo entre Modelos de Contrato (Templates) e Tópicos.
 */
public class Template_Topics {
    private int cdtopic;
    private int cdtemplate;

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