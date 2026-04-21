// Nova classe baseada no diagrama
package model;

public class Clauses {
    private int cdclause; // Primary Key
    private String dstext; // text - O conteúdo da cláusula
    private int cdtopic;   // Foreign Key para Topics

    // Getters e Setters
    
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
}