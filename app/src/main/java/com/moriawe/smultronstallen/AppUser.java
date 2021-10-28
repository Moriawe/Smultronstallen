package com.moriawe.smultronstallen;

public class AppUser {

    private String nickName;
    private String email;
    private String accountCreated;
    private String lastLoggedIn;

    // Empty constructor for the Firebase Firestore database
    public AppUser() {

    }

    // Constructor for creating objects/users in CreateAccount
    public AppUser(String nickName, String email, String accountCreated, String lastLoggedIn) {
        this.nickName = nickName;
        this.email = email;
        this.accountCreated = accountCreated;
        this.lastLoggedIn = lastLoggedIn;
    }

    // Getters & Setters

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(String accountCreated) {
        this.accountCreated = accountCreated;
    }

}
