package com.moriawe.smultronstallen;

import com.google.firebase.firestore.GeoPoint;

public class ListItem {
    private final String textName;
    private final String textDate;
    private final String textImage;
    private final GeoPoint textGeoPoint;

    public ListItem(String textName, String textDate, String textImage, GeoPoint textGeoPoint) {
        this.textName = textName;
        this.textDate = textDate;
        this.textImage = textImage;
        this.textGeoPoint = textGeoPoint;
    }
    public String getTextName() {
        return textName;
    }
    public String getTextDate() {
        return textDate;
    }
    public String getTextImage() { return textImage; }
    public GeoPoint getTextGeoPoint() { return textGeoPoint; }
}
