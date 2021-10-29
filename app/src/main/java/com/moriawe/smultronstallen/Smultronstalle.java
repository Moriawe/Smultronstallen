package com.moriawe.smultronstallen;

import com.google.firebase.firestore.GeoPoint;

public class Smultronstalle {

    private GeoPoint adress;

    private String name;
    private String comment;
    private String picture;

    private String dateCreated;

    private boolean shared;
    private String addedBy;

    // Empty constructor for the Firebase Firestore database
    public Smultronstalle() {

    }

    // Constructor for creating objects/users in CreateAccount
    public Smultronstalle(String name, String comment, GeoPoint adress, String dateCreated, boolean shared, String addedBy) {
        this.name = name;
        this.comment = comment;
        this.adress = adress;
        this.dateCreated = dateCreated;
        this.shared = shared;
        this.addedBy = addedBy;
    }

    // Getters & Setters


    public GeoPoint getAdress() {
        return adress;
    }

    public void setAdress(GeoPoint adress) {
        this.adress = adress;
    }

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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
