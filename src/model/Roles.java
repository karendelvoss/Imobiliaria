package model;

public class Roles {
    private int cdrole;   // Primary Key
    private String nmrole; // varchar(50) - alterado de int para String

    // Getters e Setters
    
    public int getCdrole() {
        return cdrole;
    }
    public void setCdrole(int cdrole) {
        this.cdrole = cdrole;
    }
    public String getNmrole() {
        return nmrole;
    }
    public void setNmrole(String nmrole) {
        this.nmrole = nmrole;
    }
    
    // Getters e Setters
}