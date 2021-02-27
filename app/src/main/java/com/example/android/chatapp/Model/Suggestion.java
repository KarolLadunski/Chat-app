package com.example.android.chatapp.Model;

public class Suggestion {
    private String title;
    private String description;
    private String spublisher;
    private String sid;
    private String coord1;
    private String coord2;
    private String skill;
    private int buttonColor =0xF0DC82;
    private String date;
    private String time;
    private String search;

    public Suggestion(String title, String description, String spublisher, String sid,
                      String coord1, String coord2, String skill, int buttonColor, String date, String time, String search) {
        this.title = title;
        this.description = description;
        this.spublisher = spublisher;
        this.sid = sid;
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.skill = skill;
        this.buttonColor = buttonColor;
        this.date = date;
        this.time = time;
        this.search = search;
    }

    public Suggestion() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpublisher() {
        return spublisher;
    }

    public void setSpublisher(String spublisher) {
        this.spublisher = spublisher;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCoord1() {
        return coord1;
    }

    public void setCoord1(String coord1) {
        this.coord1 = coord1;
    }

    public String getCoord2() {
        return coord2;
    }

    public void setCoord2(String coord2) {
        this.coord2 = coord2;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getButtonColor() { return buttonColor; }

    public void setButtonColor(int buttonColor) { this.buttonColor = buttonColor; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSearch() { return search; }

    public void setSearch(String search) {
        this.search = search;
    }




}

