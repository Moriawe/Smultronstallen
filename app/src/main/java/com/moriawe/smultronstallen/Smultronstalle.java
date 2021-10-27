package com.moriawe.smultronstallen;

public class Smultronstalle {

    private String name;
    private String comment;
    private String picture;
    private String address;
    private String lastUpdated;
    private boolean share;

    // Empty constructor for the Firebase Firestore database
    public Smultronstalle() {

    }

    // Constructor for creating objects/users in CreateAccount
    public Smultronstalle(String name, String comment, String address, String lastUpdated, boolean share) {
        this.name = name;
        this.comment = comment;
        this.address = address;
        this.lastUpdated = lastUpdated;
        this.share = share;
    }

    // Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
