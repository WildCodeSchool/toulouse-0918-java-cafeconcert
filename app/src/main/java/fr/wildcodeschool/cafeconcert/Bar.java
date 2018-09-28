package fr.wildcodeschool.cafeconcert;

public class Bar {

    /* Declaration variables*/
    private String barName;
    private String phoneNumber;
    private double geoPoint;
    private double geoShape;
    private String webUrl;
    private int isLiked; // 1 if liked, 0 if disliked, 2 if neutral

    /*Constructor*/
    public Bar(String barName, String phoneNumber, double geoPoint, double geoShape, String webUrl, int isLiked) {
        this.barName = barName;
        this.phoneNumber = phoneNumber;
        this.geoShape = geoShape;
        this.geoPoint = geoPoint;
        this.webUrl = webUrl;
        this.isLiked = isLiked;
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
        isLiked = isLiked;
    }


}
