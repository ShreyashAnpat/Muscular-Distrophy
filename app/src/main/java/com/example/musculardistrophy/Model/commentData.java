package com.example.musculardistrophy.Model;

public class commentData {
    String Comment;
    String TimeStamp;
    String Profile;
    String userID;
    String userName;

    public String getCommentID() {
        return commentID;
    }

    String commentID;

    public commentData(String comment, String timeStamp, String profile, String userID, String userName , String commentID) {
        Comment = comment;
        TimeStamp = timeStamp;
        this.Profile = profile;
        this.userID = userID;
        this.userName = userName;
        this.commentID = commentID ;
    }

    public commentData() {
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        this.Profile = profile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
