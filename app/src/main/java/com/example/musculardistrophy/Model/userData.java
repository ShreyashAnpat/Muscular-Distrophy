package com.example.musculardistrophy.Model;

public class userData {
    String Profile;
    String location;
    String phoneNumber;
    String userName;
    String userID;

    public userData(String profile, String location, String phoneNumber, String userName , String userID) {
        Profile = profile;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.userID = userID ;
    }

    public userData() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
