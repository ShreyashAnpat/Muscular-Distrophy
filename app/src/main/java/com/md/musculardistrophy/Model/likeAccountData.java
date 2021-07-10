package com.md.musculardistrophy.Model;


public class likeAccountData {
    String timeStamp , userID , userName ;

    public likeAccountData(String timeStamp, String userID, String userName) {
        this.timeStamp = timeStamp;
        this.userID = userID;
        this.userName = userName;
    }

    public likeAccountData() {
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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
