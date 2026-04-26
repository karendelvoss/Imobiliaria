package model;

/**
 * Representa a entidade de Profissões (Occupations).
 */
public class Occupations {
    private int cdoccupation;
    private String nmoccupation;

    public int getCdoccupation() {
        return cdoccupation;
    }

    public void setCdoccupation(int cdoccupation) {
        this.cdoccupation = cdoccupation;
    }

    public String getNmoccupation() {
        return nmoccupation;
    }

    public void setNmoccupation(String nmoccupation) {
        this.nmoccupation = nmoccupation;
    }
}