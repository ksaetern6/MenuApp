package com.example.menuapp.models;

public class commentModel {
    String commentID, comment, timestamp, uid, email, username;

    public commentModel() {

    }

    public commentModel(String commentID, String comment, String timestamp, String uid, String email, String username) {
        this.commentID = commentID;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.email = email;
        this.username = username;
    }

    public String getCommentID() {
        return commentID;
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
