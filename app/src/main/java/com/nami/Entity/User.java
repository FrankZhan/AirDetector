package com.nami.Entity;

public class User {

    private String userID;
    private String name;
    private String password;

    public User(){
        userID = null;
        name = null;
        password = null;
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
