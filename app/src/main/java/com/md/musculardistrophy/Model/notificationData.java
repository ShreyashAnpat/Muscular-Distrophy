package com.md.musculardistrophy.Model;

public class notificationData {
    String message ,postID ,postImage ,userID ;

    public notificationData(String message, String postID, String postImage, String userID) {
        this.message = message;
        this.postID = postID;
        this.postImage = postImage;
        this.userID = userID;
    }

    public notificationData() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
