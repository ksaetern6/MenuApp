package com.example.menuapp.models;

public class commentModel {
    String comment, uid, email, username;
    long rating;

    public commentModel() {

    }

    public commentModel(String comment, String uid, String email, String username, long rating) {
        //this.commentID = commentID;
        this.comment = comment;
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
