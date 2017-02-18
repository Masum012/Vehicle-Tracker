package com.example.abdulazizsorkar.devicehandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Marker marker;
    public LocationManager locationManager;
    public Criteria criteria;
    public static Location mCurrentLocation;
    public String provider;
    Button btLogout,userInfo;
    UserLocalStore userLocalStore;
    public int count=0,check=0;
    public Button currentPositionButton;

    GetMyLocation myLocation;
    public static LocationManager updateManager;
    public static Criteria updateCriteria;
    public static Location updateCurrentLocation;
    public static String updateProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myLocation = new GetMyLocation(this);
        setUpMapIfNeeded();


        currentPositionButton = (Button) findViewById(R.id.currentPosition);
        currentPositionButton.setOnClickListener(this);
        
        btLogout = (Button) findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);

        userInfo = (Button) findViewById(R.id.userInfo);
        userInfo.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //showCurrentLocation();
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.currentPosition:
                if(check==0)
                {
                    AlarmManager alarmManager=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000, pendingIntent);
                    check=1;
                    currentPositionButton.setText("Stop Updating Location");
                }
                else if(check==1)
                {
                    AlarmManager alarmManager=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    alarmManager.cancel(pendingIntent);
                    check=0;
                    currentPositionButton.setText("Start Updating Location");
                }
            break;
            case R.id.btLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.userInfo:
                startActivity(new Intent(this, UserInfo.class));
                break;


            default:
                break;
        }
        
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticated()==true)
            //displayUserDetails();
            ;
        else
        {
            count++;
            if(count<2)
                startActivity(new Intent(this,Login.class));
            else
            {
                finish();
            }

        }
    }

    public boolean authenticated()
    {
        return userLocalStore.getUserLoggedIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {

        if (myLocation.canGetLocation()) {
            mCurrentLocation = myLocation.getLocationValue();
        }
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
       // Toast.makeText(this,""+provider,Toast.LENGTH_LONG).show();
        if(provider!=null)
        mCurrentLocation = locationManager.getLastKnownLocation(provider);*/
        //Toast.makeText(this,""+provider,Toast.LENGTH_LONG).show();
        if(marker!=null)
            marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())), 3000,null);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 14f));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);

    }

    public void showCurrentLocation()
    {
        Toast.makeText(this,"Current Location :\n"+mCurrentLocation.getLatitude()+" , "+mCurrentLocation.getLongitude()+"\n has stored to server",Toast.LENGTH_LONG).show();
    }

   /* public void storeCurrentLocation()
    {
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        VehicleLocation vehicleLocation = new VehicleLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),mydate);

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(vehicleLocation, new GetUserCallback() {
            @Override
            public void done(User returnLocation) {
               showCurrentLocation();
            }
        });
    }*/



}
