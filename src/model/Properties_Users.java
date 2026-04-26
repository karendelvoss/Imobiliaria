package model;

/**
 * Representa o vínculo entre Imóveis e Usuários (Proprietários).
 */
public class Properties_Users {
    private int cdproperty;
    private int cduser;

    public int getCdproperty() {
        return cdproperty;
    }

    public void setCdproperty(int cdproperty) {
        this.cdproperty = cdproperty;
    }

    public int getCduser() {
        return cduser;
    }

    public void setCduser(int cduser) {
        this.cduser = cduser;
    }
}