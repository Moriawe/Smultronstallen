package com.moriawe.smultronstallen;

public class Smultronstalle {

    private String name;
    private String added;
    private String comment;
    private String picture;
    private boolean share;

    public Smultronstalle() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
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
}
