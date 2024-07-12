package com.example.unfriendster;

public class Post {
    private String title;
    private String content;
    private String photoUrl; // Add this field

    public Post(String title, String content, String photoUrl) {
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
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
        this.photoUrl = photoUrl;
    }
}