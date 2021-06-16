package com.example.musculardistrophy.Model;

public class messageData {
    String date;
    String message;
    String seen;
    String senderID;
    String time;
    String timeStamp ;

    public messageData(String date, String message, String seen, String senderID, String time, String timeStamp) {
        this.date = date;
        this.message = message;
        this.seen = seen;
        this.senderID = senderID;
        this.time = time;
        this.timeStamp = timeStamp;
    }

    public messageData() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
