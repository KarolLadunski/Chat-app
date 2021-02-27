package com.example.android.chatapp.Model;

public class Interested {
    private String sid;
    private String iId;
    private String u1;
    private String u2;
    private String type;

    public Interested(String sid, String iId, String u1, String u2, String type) {

        this.iId = iId;
        this.sid = sid;
        this.u1 = u1;
        this.u2 = u2;
        this.type = type;
    }

    public Interested() {

    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getU1() {
        return u1;
    }

    public void setU1(String u1) {
        this.u1 = u1;
    }

    public String getU2() {
        return u2;
    }

    public void setU2(String u2) {
        this.u2 = u2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

