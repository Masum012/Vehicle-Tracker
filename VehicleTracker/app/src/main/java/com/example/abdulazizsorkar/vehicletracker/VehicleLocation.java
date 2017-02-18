package com.example.abdulazizsorkar.vehicletracker;

/**
 * Created by Abdul Aziz Sorkar on 1/18/2016.
 */
public class VehicleLocation {
    double latitude,longitude;
    String dateTime;


    public VehicleLocation(double latitude, double longitude,String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDateTime() {
        return dateTime;
    }
}
