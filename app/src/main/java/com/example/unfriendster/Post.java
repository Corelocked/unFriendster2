package com.example.unfriendster;

public class Post {
    private String title;
    private String content;
    private String photoUrl;

    // No-argument constructor required for Firebase deserialization
    public Post() {// Leave this empty
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl; // Assign the value here
    }

    public void setTitle(String title) {
        this.title = title; // Assign the value here
    }

    public void setContent(String content) {
        this.content = content; // Assign the value here
    }
}