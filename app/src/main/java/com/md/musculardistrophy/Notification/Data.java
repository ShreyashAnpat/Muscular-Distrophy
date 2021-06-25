package com.md.musculardistrophy.Notification;

public class Data {
    String title;
    String msg;

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    String notificationImage ;

    public Data() {
    }

    public Data(String title, String msg , String notificationImage) {
        this.title = title;
        this.msg = msg;
        this.notificationImage = notificationImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
