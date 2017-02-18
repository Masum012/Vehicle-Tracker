package com.example.abdulazizsorkar.vehicletracker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Abdul Aziz Sorkar on 2/6/2016.
 */
public class DistanceOfTwoPlace {

    /*public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong){
    *//*PRE: All the input values are in radians!*//*

        double latDiff = finalLat - initialLat;
        double longDiff = finalLong - initialLong;
        double earthRadius = 6371; //In Km if you want the distance in km

        double distance = 2*earthRadius*Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff/2.0),2)+Math.cos(initialLat)*Math.cos(finalLat)*Math.pow(Math.sin(longDiff/2),2)));

        return distance;

    }*/






    public double calculationByDistance(double initialLat, double initialLong,
                                        double finalLat, double finalLong){
        int R = 6371; // km
        double dLat = toRadians(finalLat-initialLat);
        double dLon = toRadians(finalLong- initialLong);
        double lat1 = toRadians(initialLat);
        double lat2 = toRadians(finalLat);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }
}
