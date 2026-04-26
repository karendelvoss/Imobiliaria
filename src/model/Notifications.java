package model;

import java.time.LocalDate;

/**
 * Representa a entidade de Notificações enviadas aos usuários.
 */
public class Notifications {
    private int cdnotification;
    private String dsmessage;
    private LocalDate dtsend;
    private int cdcontract;
    private int cduser;
    private int cdnotificationtemplate;
    private int fgchannel;

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

    public int getCdnotificationtemplate() {
        return cdnotificationtemplate;
    }

    public void setCdnotificationtemplate(int cdnotificationtemplate) {
        this.cdnotificationtemplate = cdnotificationtemplate;
    }

    public int getFgchannel() {
        return fgchannel;
    }

    public void setFgchannel(int fgchannel) {
        this.fgchannel = fgchannel;
    }
}