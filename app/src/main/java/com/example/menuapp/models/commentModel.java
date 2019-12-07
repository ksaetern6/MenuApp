package com.example.menuapp.models;

public class commentModel {
    String comment, timestamp, uid, email, username;
    long rating;

    public commentModel() {

    }

    public commentModel(String comment, String timestamp, String uid, String email, String username, long rating) {
        //this.commentID = commentID;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.rating = rating;
    }

    public long getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
