package com.example.all_habits;

/**
 * Gets the coordinates of location
 */
public class locationCoordinate {

    //initialize
    private double longitude;
    private double latitude;

    public locationCoordinate (double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }
    //gets latitude
    public double getLatitude() { return latitude; }

    //sets latitude
    public void setLatitude(double latitude) { this.latitude = latitude; }

    //gets longitude
    public double getLongitude() { return longitude; }

    //sets longitude
    public void setLongitude(double longitude) { this.longitude = longitude; }

}
