package model;

/**
 * Representa os papéis que um usuário pode assumir no contrato (ex: Locatário, Locador).
 */
public class Roles {
    private int cdrole;
    private String nmrole;

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
}