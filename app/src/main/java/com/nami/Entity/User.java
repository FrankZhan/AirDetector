package com.nami.Entity;

public class User {

    private String userID;  // ID
    private String email;   // Email
    private String name;    // Username
    private String password;

    public User(){
        userID = null;
        name = null;
        password = null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUserID() {
        return userID;
    }
}
