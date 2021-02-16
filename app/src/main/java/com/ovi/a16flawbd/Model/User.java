package com.ovi.a16flawbd.Model;

public class User {
    private String id;
    private String imageUrl;
    private String username;

    public User(String id, String imageUrl, String username) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
