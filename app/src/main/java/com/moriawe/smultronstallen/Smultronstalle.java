package com.moriawe.smultronstallen;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Smultronstalle {

    private GeoPoint geoAddress;

    private String name;
    private String comment;
    private String address;
    private String picture;

    private String dateCreated;

    private boolean shared;
    private String addedBy;
    private String creatorsUserID;

    // Empty constructor for the Firebase Firestore database
    public Smultronstalle() {

    }

    // Constructor for creating objects/users in CreateAccount
    public Smultronstalle(String name, String comment, String address, GeoPoint geoAddress, String dateCreated, boolean shared, String addedBy, String creatorsUserID) {
        this.name = name;
        this.comment = comment;
        this.address = address;
        this.geoAddress = geoAddress;
        this.dateCreated = dateCreated;
        this.shared = shared;
        this.addedBy = addedBy;
        this.creatorsUserID = creatorsUserID;
    }

    // HELPERMETHOD - Returns the streetname and number back as a string from the smultronstalle address.
    protected String getAddressFromGeo(Context context) {

        double latitude = geoAddress.getLatitude();
        double longitude = geoAddress.getLongitude();

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String placeAdress;

        try {

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //Fetches streetname and number
            String street = addresses.get(0).getThoroughfare();
            String streetNum = addresses.get(0).getSubThoroughfare();

            placeAdress = street + " " + streetNum;

        } catch (IOException e) {
            e.printStackTrace();
            placeAdress = Double.toString(latitude) + Double.toString(longitude);
        }

        return placeAdress;
    }

    // Getters & Setters


    public GeoPoint getGeoAddress() {
        return geoAddress;
    }

    public void setGeoAddress(GeoPoint geoAddress) {
        this.geoAddress = geoAddress;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getCreatorsUserID() {
        return creatorsUserID;
    }

    public void setCreatorsUserID(String creatorsUserID) {
        this.creatorsUserID = creatorsUserID;
    }
}
