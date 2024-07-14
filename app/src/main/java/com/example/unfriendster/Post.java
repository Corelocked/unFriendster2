package com.example.unfriendster;

import com.google.firebase.Timestamp;

public class Post {
    private String title;
    private String content;
    private String photoUrl;
    private String authorUid;
    private String authorProfilePictureUrl;
    private String authorName;
    private String authorUsername;
    private Timestamp timestamp;
    // No-argument constructor for Firebase
    public Post() {
        // Leave this empty
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getAuthorProfilePictureUrl() {
        return authorProfilePictureUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters with data validation
    public void setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
    }

    public void setContent(String content) {
        if (content != null && !content.isEmpty()) {
            this.content = content;
        }
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public void setAuthorProfilePictureUrl(String authorProfilePictureUrl) {
        this.authorProfilePictureUrl = authorProfilePictureUrl;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }




    // toString() method for better representation
    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorName='" + authorName + '\'' +
                // ... other fields
                '}';
    }

}