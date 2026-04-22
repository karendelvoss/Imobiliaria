package model;

import java.time.LocalDate;

public class Notifications {
    private int cdnotification; // Primary Key
    private String dsmessage;    // text - Corrigido dsmensage
    private LocalDate dtsend;   // date
    private int cdcontract;     // Foreign Key para Contracts
    private int cduser;         // Foreign Key para Users
    private boolean fgread;     // Se a notificação foi lida
    // Getters e Setters
    
    public int getCdnotification() {
        return cdnotification;
    }
    public void setCdnotification(int cdnotification) {
        this.cdnotification = cdnotification;
    }
    public String getDsmessage() {
        return dsmessage;
    }
    public void setDsmessage(String dsmessage) {
        this.dsmessage = dsmessage;
    }
    public LocalDate getDtsend() {
        return dtsend;
    }
    public void setDtsend(LocalDate dtsend) {
        this.dtsend = dtsend;
    }
    public int getCdcontract() {
        return cdcontract;
    }
    public void setCdcontract(int cdcontract) {
        this.cdcontract = cdcontract;
    }
    public int getCduser() {
        return cduser;
    }
    public void setCduser(int cduser) {
        this.cduser = cduser;
    }
    public boolean isFgread() {
        return fgread;
    }
    public void setFgread(boolean fgread) {
        this.fgread = fgread;
    }
}