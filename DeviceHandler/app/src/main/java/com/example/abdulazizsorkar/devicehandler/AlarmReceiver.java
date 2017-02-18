package com.example.abdulazizsorkar.devicehandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Abdul Aziz Sorkar on 2/16/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    GetMyLocation myLocation;
    Location lastLocation;
    @Override
    public void onReceive(Context context, Intent intent) {
        myLocation = new GetMyLocation(context);
        lastLocation = myLocation.getLocationValue();
        storeCurrentLocation(context);
    }

    public void storeCurrentLocation(Context context)
    {
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        VehicleLocation vehicleLocation = new VehicleLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),mydate);

        ServerRequests serverRequests = new ServerRequests(context);
        serverRequests.storeUserDataInBackground(vehicleLocation, new GetUserCallback() {
            @Override
            public void done(User returnLocation) {
return;
            }
        });
    }
}
