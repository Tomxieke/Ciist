package com.ciist.entities;

/**
 * Created by 中联软科 on 2015/11/24.
 */
public class UserInfo {
    private String Selfids;
    private String User_zh;
    private String Dids;
    private String Duties;
    private String Tetitle;
    private int Ismaster;
    private String Account;

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getSelfids() {
        return Selfids;
    }

    public void setSelfids(String selfids) {
        Selfids = selfids;
    }

    public String getUser_zh() {
        return User_zh;
    }

    public void setUser_zh(String user_zh) {
        User_zh = user_zh;
    }

    public String getDids() {
        return Dids;
    }

    public void setDids(String dids) {
        Dids = dids;
    }

    public String getDuties() {
        return Duties;
    }

    public void setDuties(String duties) {
        Duties = duties;
    }

    public String getTetitle() {
        return Tetitle;
    }

    public void setTetitle(String tetitle) {
        Tetitle = tetitle;
    }

    public int getIsmaster() {
        return Ismaster;
    }

    public void setIsmaster(int ismaster) {
        Ismaster = ismaster;
    }
}
