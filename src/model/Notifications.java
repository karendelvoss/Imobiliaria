package model;

import java.time.LocalDate;

public class Notifications {
    private int cdnotification; // Primary Key
    private String dsmessage;    // text - Corrigido dsmensage
    private LocalDate dtsend;   // date
    private int cdcontract;     // Foreign Key para Contracts
    private int cduser;         // Foreign Key para Users
    private int fgstatus;       // Status da notificação (Agendada, Enviada, Erro)
    private int tpnotification; // Tipo da notificação (E-mail, SMS)
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
    public int getFgstatus() {
        return fgstatus;
    }
    public void setFgstatus(int fgstatus) {
        this.fgstatus = fgstatus;
    }
    public int getTpnotification() {
        return tpnotification;
    }
    public void setTpnotification(int tpnotification) {
        this.tpnotification = tpnotification;
    }
}