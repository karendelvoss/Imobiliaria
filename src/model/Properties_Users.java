package model;

public class Properties_Users {
    private int cdproperty; // FK para Properties 
    private int cduser;     // FK para Users 

    // Getters e Setters
    
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