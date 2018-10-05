package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Bar {

    /* Declaration variables*/
    private String barName;
    private String phoneNumber;
    private double geoPoint;
    private double geoShape;
    private String webUrl;
    private int isLiked; // 1 if liked, 0 if disliked, 2 if neutral
    private int picture; // Pour un drawable le type est 'int'
    private Context context;

    /*Constructor*/
    public Bar(String barName, String phoneNumber, double geoPoint, double geoShape, String webUrl, int isLiked, int picture, Context context) {
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(this.barName, isLiked);
        editor.commit();

    }

    public int getPicture() { return picture; }

    public void setPicture(int picture) { this.picture = picture; }
}