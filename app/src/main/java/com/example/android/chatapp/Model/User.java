package com.example.android.chatapp.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private String school;
    private String work;
    private String timeSeen;
    private Double Lat;
    private Double Lng;
    private String friend;
    private String me;
    private String friends;


    public User(String id, String username, String imageURL, String status, String search, String school,
                String work, String timeSeen, Double Lat, Double Lng, String friend, String me, String friends) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.school = school;
        this.work = work;
        this.timeSeen = timeSeen;
        this.Lat = Lat;
        this.Lng = Lng;
        this.friend = friend;
        this.me = me;
        this.friends = friends;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) { this.school = school; }

    public String getWork() {
        return work;
    }

    public void setWork(String work) { this.work = work; }

    public String getTimeSeen() {
        return timeSeen;
    }

    public void setTimeSeen(String timeSeen) { this.timeSeen = timeSeen; }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double Lat) { this.Lat = Lat; }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double Lng) { this.Lng = Lng; }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) { this.friend = friend; }

    public String getMe() {
        return me;
    }

    public void setMe(String me) { this.me = me; }

    public void setFriends(String friends) { this.friends = friends; }


}
