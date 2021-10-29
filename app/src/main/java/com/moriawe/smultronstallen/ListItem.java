package com.moriawe.smultronstallen;

public class ListItem {
    private final String textName;
    private final String textDate;
    private final String textImage;
    private final String textGeoPoint;

    public ListItem(String textName, String textDate, String textImage, String textGeoPoint) {
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
    public String getTextGeoPoint() { return textGeoPoint; }
}
