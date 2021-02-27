package com.example.android.chatapp.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private String date;
    private String type;
    private String invitationTitle;
    private String mid;

    public Chat(String sender, String receiver, String message, boolean isseen, String date, String type, String invitationTitle, String mid) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.date = date;
        this.type = type;
        this.invitationTitle = invitationTitle;
        this.mid = mid;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInvitationTitle() {
        return invitationTitle;
    }

    public void setInvitationTitle(String invitationTitle) {
        this.invitationTitle = invitationTitle;
    }

    public String getMid() { return mid; }

    public void setMid(String mid) { this.mid = mid; }
}
