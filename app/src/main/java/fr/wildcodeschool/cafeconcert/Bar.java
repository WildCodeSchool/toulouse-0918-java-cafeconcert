package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import java.util.Date;

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
    private float distanceFromUser;
    
    /*Constructor*/
    public Bar(String barName, String phoneNumber, double geoPoint, double geoShape, String webUrl, int isLiked, String picture, Context context) {
        this.barName = barName;
        this.phoneNumber = phoneNumber;
        this.geoShape = geoShape;
        this.geoPoint = geoPoint;
        this.webUrl = webUrl;
        this.picture = picture;
        this.context = context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.contains(this.barName)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(this.barName, isLiked);
            editor.commit();
            this.isLiked = isLiked;
        } else {
            this.isLiked = sharedPreferences.getInt(this.barName, 2);
        }
    }

    public Bar() {}

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

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

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