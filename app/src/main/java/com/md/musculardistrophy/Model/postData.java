package com.md.musculardistrophy.Model;

public class postData {
    String Caption , PostType,Profile,TimeStamp,UID,post,username , postID;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public postData(String caption, String postType, String profile, String timeStamp, String UID, String post, String username , String postID) {
        this.Caption = caption;
        this.PostType = postType;
        this.Profile = profile;
        this.TimeStamp = timeStamp;
        this.UID = UID;
        this.post = post;
        this.username = username;
        this.postID = postID;

    }

    public postData() {
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getPostType() {
        return PostType;
    }

    public void setPostType(String postType) {
        PostType = postType;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
