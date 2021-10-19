package com.moriawe.smultronstallen;

public class AppUser {

    private String firstName;
    private String lastName;
    private String lastLoggedIn;
    private String nickName;
    private String email;

    public AppUser() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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
}
