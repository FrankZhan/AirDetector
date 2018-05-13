package com.nami.Entity;

public class User {

    private int userID;  // ID
    private String email;   // Email
    private String name;    // Username

    public User(){
        userID = 0;
        name = "noUer";
        email = "noMail";
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


    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }


    public int getUserID() {
        return userID;
    }
}
