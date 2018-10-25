package fr.wildcodeschool.cafeconcert;

import android.content.Context;

public class Bar {

    /* Declaration variables*/
    private String barName;
    private String phoneNumber;
    private double geoPoint;
    private double geoShape;
    private String webUrl;
    private int isLiked; // 1 if liked, 0 if disliked, 2 if neutral
    private String picture; // Pour un drawable le type est 'int'
    private Context context;
    private String address;
    private String logo;
    private float distanceFromUser;
    private String barId;

    /*Constructor*/
    public Bar() {
    }

    public String getBarId() {
        return barId;
    }

    public void setBarId(String barID) {
        this.barId = barID;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    /*Getters and setters*/
    public String getBarName() {
        return barName;
    }

    public void setBarName(String name) {
        this.barName = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getGeoShape() {
        return geoShape;
    }

    public void setGeoShape(double geoShape) {
        this.geoShape = geoShape;
    }

    public double getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(double geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }
}