package com.moriawe.smultronstallen;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Smultronstalle {

    private GeoPoint adress;

    private String name;
    private String comment;
    private String picture;

    private String dateCreated;

    private boolean shared;
    private String addedBy;
    private String userID;

    // Empty constructor for the Firebase Firestore database
    public Smultronstalle() {

    }

    // Constructor for creating objects/users in CreateAccount
    public Smultronstalle(String name, String comment, GeoPoint adress, String dateCreated, boolean shared, String addedBy, String userID) {
        this.name = name;
        this.comment = comment;
        this.adress = adress;
        this.dateCreated = dateCreated;
        this.shared = shared;
        this.addedBy = addedBy;
        this.userID = userID;
    }

    // HELPERMETHOD - Returns the streetname and number back as a string from the smultronstalle address.
    protected String getAddressFromGeo(Context context) {

        double latitude = adress.getLatitude();
        double longitude = adress.getLongitude();

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String placeAdress;

        try {

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String street = addresses.get(0).getThoroughfare();
            String streetNum = addresses.get(0).getSubThoroughfare();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            placeAdress = street + " " + streetNum;

        } catch (IOException e) {
            e.printStackTrace();
            placeAdress = "";
        }

        return placeAdress;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
