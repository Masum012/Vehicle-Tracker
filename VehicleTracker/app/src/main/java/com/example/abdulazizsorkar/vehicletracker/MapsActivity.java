package com.example.abdulazizsorkar.vehicletracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends ActionBarActivity implements  PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static double latitude,longitude;

    //Variable declaration for Sliding Menu
    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private VehicleLocation lastLocation=null;
    public LocationManager locationManager;
    public Criteria criteria;
    public Location mCurrentLocation;
    public String provider;



    Double fromLat,toLat,fromLng,toLng;
    Marker tempMarker;
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<Marker> markers = new ArrayList<Marker>();


    //List<Overlay> mapOverlays;
    //GeoPoint point1, point2;
    public LocationManager locManager;
    public Drawable drawable;
    public Document document;

    public LatLng fromPosition;
    public LatLng toPosition;
    public MarkerOptions markerOptions;
    public Location location;
    DistanceOfTwoPlace distanceOfTwoPlace =  new DistanceOfTwoPlace();

    Button next,previous,stopAnimation;

    double distance = 0;
    String dString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


        //For map action
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Button zoomInButton = (Button) findViewById(R.id.zoomIn);
                Button zoomOutButton = (Button) findViewById(R.id.zoomOut);

                if (zoomInButton.getVisibility() == View.VISIBLE) {
                    zoomInButton.setVisibility(View.GONE);
                    zoomOutButton.setVisibility(View.GONE);
                } else {
                    zoomInButton.setVisibility(View.VISIBLE);
                    zoomOutButton.setVisibility(View.VISIBLE);
                }
            }
        });

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);
        stopAnimation = (Button) findViewById(R.id.stopAnimation);

        next.setVisibility(View.GONE);
        previous.setVisibility(View.GONE);
        stopAnimation.setVisibility(View.GONE);

        //Action for Sliding Menu

        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        listSliding = new ArrayList<>();

        listSliding.add(new ItemSlideMenu(R.drawable.ic_normal_map, "Normal Map"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_satellite_map, "Satellite"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_traffic_map, "Traffic"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_settings,"Locate Vehicle"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_road,"Find Road"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_road,"Animation View"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_road,"Street View"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_about, "About"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_exit, "Exit"));
        adapter = new SlidingMenuAdapter(this,listSliding);

        listViewSliding.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*setTitle(listSliding.get(0).getTitle());
        listViewSliding.setItemChecked(0, true);
        drawerLayout.closeDrawer(listViewSliding);
        replaceFragment(0);
        message("Selected " + 0);*/

        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //message("Selected " + i);
                setTitle(listSliding.get(i).getTitle());
                listViewSliding.setItemChecked(i, true);

                replaceFragment(i, findViewById(i));

                drawerLayout.closeDrawer(listViewSliding);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_opened,R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    /////////////////////////////////////////////////////////////
    public void searchingPlace(View view)
    {
        EditText editText = (EditText) findViewById(R.id.palce);
        String loaction = editText.getText().toString();

        if(loaction==null || loaction.equals("")) {
        Toast.makeText(this,"Enter place to search",Toast.LENGTH_LONG).show();
    }
        else
        {
            Geocoder geocoder = new Geocoder(this);

            List<Address> addressList = null;

            try {
                addressList = geocoder.getFromLocationName(loaction,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            LatLng latLng = new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title("Your Searched Position"));
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

///////////////////////////////////////////////////////////////
public String getAddress(double lat, double lng,int check) {
        String add=null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            addresses.get(0);
            add = addresses.get(0).getFeatureName() +", "+addresses.get(0).getLocality() +", "+ addresses.get(0).getCountryName();
            if(check==1)
                Toast.makeText(this, "Address -> " + add, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    return add;
}


////////////////////////////////////////////////////////////////
    public void changeMapType(int type)
    {
        switch (type)
        {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

    }

////////////////////////////////////////////////////////////////////////
    public void ZoomMap(View view)
    {
        if(view.getId() == R.id.zoomIn)
        {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() == R.id.zoomOut)
            mMap.animateCamera(CameraUpdateFactory.zoomOut());

    }

//////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

/////////////////////////////////////////////////////////////////////
    private void setUpMap() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);

        //if(provider!=null)
        mCurrentLocation = locationManager.getLastKnownLocation(provider);

        if(marker!=null)
            marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())), 3000,null);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 14f));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //mMap.setMyLocationEnabled(true);
        //Toast.makeText(this,""+mCurrentLocation.getLongitude(),Toast.LENGTH_LONG).show();
    }



    /*
    *   adding of Sliding Menu in Home page
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            finish();
            return true;
        }
        
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    ////////////////////////////////////////////show some thing as message
    private void message(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void replaceFragment(int pos,View view)
    {
        //Fragment fragment;

        switch (pos){
            case 0:
                changeMapType(0);
                /*fragment = new Fragment1();*/
                break;
            case 1:
                changeMapType(1);
                //func(view);
                //fragment = new Fragment2();
                break;
            case 2:
                changeMapType(2);
               // fragment = new Fragment3();
                break;
            case 3:
                goToVehicleCurrentLocation();
                break;

            case 4:
                if(lastLocation!=null) {
                    //showRoadView();
                    showPath(0);

                }

                else
                    Toast.makeText(this,"See Vehicle Current Location First",Toast.LENGTH_LONG).show();
                break;
            case 5:
                if(lastLocation!=null) {
                    showPath(1);
                }
                else
                    Toast.makeText(this,"See Vehicle Current Location First",Toast.LENGTH_LONG).show();
                break;
            case 6:
                if(lastLocation!=null){
                    Intent StreetView=new Intent(MapsActivity.this,StreetView.class);
                    StreetView.putExtra("streetViewLat", lastLocation.latitude);
                    StreetView.putExtra("streetViewLng", lastLocation.longitude);
                    startActivity(StreetView);
                }
                else
                    Toast.makeText(this,"See Vehicle Current Location First",Toast.LENGTH_LONG).show();
                break;
            case 7:

                Intent intent = new Intent(this,About.class);
                Bundle bundle = new Bundle();

                bundle.putString("message","Hello World!!");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 8:
                finish();
                break;

            default:
                //fragment = new Fragment1();
                break;
        }

    }


//////////////////////////////////////////////////////////////////////
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /**
     *
     * @author VIJAYAKUMAR M
     * This class Get Route on the map
     *
     */

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


//////////////////////////////////////////////////////////////////////////////
    public void history(View view)
    {
        ServerRequests serverRequests = new ServerRequests(this);


        if(view.getId() == R.id.next)
        {
            serverRequests.counter--;
        }
        else if (view.getId() == R.id.previous)
        {
            serverRequests.counter++;
        }

        serverRequests.fetchUserDataInBackground(new GetUserCallback() {
            @Override
            public void done(VehicleLocation returnedLocation) {
                if (returnedLocation == null)
                    showErrorMessage();
                else {
                    lastLocation = new VehicleLocation(returnedLocation.latitude, returnedLocation.longitude,returnedLocation.dateTime);
                    locate(returnedLocation);
                    getAddress(returnedLocation.latitude, returnedLocation.longitude, 1);
                }
            }


        });
    }

/////////////////////////////////////////////////////////////////////////
    public void controlAnimation(View view)
    {
        animator.stopAnimation();
        tempMarker.remove();

        stopAnimation.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        previous.setVisibility(View.VISIBLE);
    }


///////////////////////////////////////////////////////////////////////
    private void goToVehicleCurrentLocation() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.counter=0;
        serverRequests.fetchUserDataInBackground(new GetUserCallback() {
            @Override
            public void done(VehicleLocation returnedLocation) {
                if (returnedLocation == null)
                    showErrorMessage();
                else {
                    lastLocation = new VehicleLocation(returnedLocation.latitude, returnedLocation.longitude,returnedLocation.dateTime);
                    locate(returnedLocation);
                    getAddress(returnedLocation.latitude, returnedLocation.longitude, 1);
                }
            }
        });


        next.setVisibility(View.VISIBLE);
        previous.setVisibility(View.VISIBLE);
    }


///////////////////////////////////////////////////////////////////
    public Marker marker;
    private void locate(VehicleLocation returnedLocation){

        if(marker!=null)
            marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(returnedLocation.getLatitude(), returnedLocation.getLongitude())).title("Vehicle Current Location").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(returnedLocation.getLatitude(), returnedLocation.getLongitude())), 3000,null);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(returnedLocation.getLatitude(), returnedLocation.getLongitude()), 14f));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Position"));
        //Toast.makeText(this, "Your Vehicle Current Location is here", Toast.LENGTH_SHORT).show();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int d =(int) distance;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                if(d!=0)
                builder1.setMessage(lastLocation.dateTime+"\n"+getAddress(lastLocation.latitude,lastLocation.longitude,0) + "\nDistance is "+d +" km");
                else
                    builder1.setMessage(lastLocation.dateTime+"\n"+getAddress(lastLocation.latitude, lastLocation.longitude, 0) + "");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();


                return true;
            }
        });

    }

    private void showErrorMessage() {
        Toast.makeText(this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
    }



    /////////////////////////////////////////////////////////////////////////////////
    public double showPath(final int cmd)
    {

        LatLng start = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        LatLng end = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());


        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(start).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(end).title("Vehicle Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

       // LatLng start = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait...",
                "Fetching Route Information...", true);
        final Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .waypoints(start, end)
                .alternativeRoutes(true)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong, Try again.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onRoutingStart() {

                    }


                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                         progressDialog.dismiss();
                        Route x = arrayList.get(i);
                        List<LatLng> myLoc = x.getPoints();

                        distance = 0;
                        int j;
                        for (j = 1; j < myLoc.size(); j++) {
                            distance = distance + distanceOfTwoPlace.calculationByDistance(myLoc.get(j).latitude, myLoc.get(j).longitude, myLoc.get(j - 1).latitude, myLoc.get(j - 1).longitude);
                        }
                        PolylineOptions polyoptions = new PolylineOptions();
                        polyoptions.color(Color.BLUE);
                        polyoptions.width(10);
                        polyoptions.addAll(arrayList.get(i).getPoints());
                        mMap.addPolyline(polyoptions);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));

                        if(cmd==1)
                        {
                            stopAnimation.setVisibility(View.VISIBLE);
                            next.setVisibility(View.GONE);
                            previous.setVisibility(View.GONE);
                            latLngs=arrayList.get(i).getPoints();
                            animator.startAnimation(false, latLngs);
                        }
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .build();
        routing.execute();
        return distance;
    }











    //animating marker


    /**
     * Adds a list of markers to the map.
     */
    public void addPolylineToMap(List<LatLng> latLngs) {
        PolylineOptions options = new PolylineOptions();
        for (LatLng latLng : latLngs) {
            options.add(latLng);
        }
        options.color(Color.BLUE);
        options.width(10);
        mMap.addPolyline(options);
    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        mMap.clear();
        markers.clear();
    }


    private Animator animator = new Animator();
    private final Handler mHandler = new Handler();

    public class Animator implements Runnable {

        private static final int ANIMATE_SPEEED = 300;
        private static final int ANIMATE_SPEEED_TURN = 300;
        private static final int BEARING_OFFSET = 20;

        private final Interpolator interpolator = new LinearInterpolator();

        private boolean animating = false;

        private List<LatLng> latLngs = new ArrayList<LatLng>();

        int currentIndex = 0;

        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stopAnimation() {
            animating=false;
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = latLngs.get(0);
            LatLng secondPos = latLngs.get(1);

            setInitialCameraPosition(markerPos, secondPos);

        }

        private void setInitialCameraPosition(LatLng markerPos,
                                              LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos, secondPos);

            trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));


            tempMarker=trackingMarker;

            float mapZoom = mMap.getCameraPosition().zoom >=16 ? mMap.getCameraPosition().zoom : 16;

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mapZoom)
                            .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();

        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(latLngs.get(0));
            return mMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }

        public void startAnimation(boolean showPolyLine,List<LatLng> latLngs) {
            if (trackingMarker!=null) {
                trackingMarker.remove();
            }
            this.animating = true;
            this.latLngs=latLngs;
            if (latLngs.size()>2) {
                initialize(showPolyLine);
            }

        }

        public boolean isAnimating() {
            return this.animating;
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
            LatLng intermediatePosition = SphericalUtil.interpolate(beginLatLng, endLatLng, t);

            Double mapZoomDouble = 18.5-( Math.abs((0.5- t))*5);
            float mapZoom =  mapZoomDouble.floatValue();

            System.out.println("mapZoom = " + mapZoom);

            trackingMarker.setPosition(intermediatePosition);

            if (showPolyline) {
                updatePolyLine(intermediatePosition);
            }

            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + latLngs.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                if (currentIndex<latLngs.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    Double heading = SphericalUtil.computeHeading(beginLatLng, endLatLng);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(endLatLng)
                                    .bearing(heading.floatValue() /*+ BEARING_OFFSET*/) // .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();

                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    //start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(this, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    tempMarker.remove();
                    stopAnimation();
                }

            }
        }



        private LatLng getEndLatLng() {
            return latLngs.get(currentIndex+1);
        }

        private LatLng getBeginLatLng() {
            return latLngs.get(currentIndex);
        }

    };

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        if (markers.size()>=index+1) {
            highLightMarker(markers.get(index));
        }
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {
        if (marker!=null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.showInfoWindow();
        }

    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    public static float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);
        return beginL.bearingTo(endL);
    }

    public static Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }


}



