package model;

public class Occupations {
    private int cdoccupation; // Primary Key 
    private String nmoccupation; // varchar(100) 

    // Getters e Setters

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