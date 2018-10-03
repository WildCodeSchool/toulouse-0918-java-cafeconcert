package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final static double TOULOUSE_LATITUDE = 43.6043;
    final static double TOULOUSE_LONGITUDE = 1.4437;
    final static double TOULOUSE_LATITUDE_BORDURES_BOT = 43.565428;
    final static double TOULOUSE_LONGITUDE_BORDURES_BOT = 1.411854;
    final static double TOULOUSE_LATITUDE_BORDURES_TOP = 43.642094;
    final static double TOULOUSE_LONGITUDE_BORDURES_TOP = 1.480995;
    final static int ZOOM_LVL_BY_DEFAULT = 13;
    final static float ZOOM_LVL_ON_USER = 15.76f;

    private GoogleMap mMap;
    private GestureDetectorCompat mGestureObject;
    private MotionEvent mMotionEvent;
    private LocationManager mLocationManager = null;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Setting map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Setting button to go to BarListActivity
        final ImageView goList = findViewById(R.id.goList);
        transitionBetweenActivity(goList, MapsActivity.this, BarListActivity.class);
    }

    /* Init a Listener on the ImageView triggerTransition. When touched, start the destination */
    public static void transitionBetweenActivity(ImageView triggerTransition, final Context context, final Class destination) {
        //onTouch du Drawable à droite (fleche), go sur l'activity list bar
        triggerTransition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(context, destination);
                    context.startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    //TODO commenter cette méthode pour rendre son utilité explicite. A quoi elle sert ?
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Setting map borders
        LatLngBounds toulouseBounds = new LatLngBounds(
                new LatLng(TOULOUSE_LATITUDE_BORDURES_BOT, TOULOUSE_LONGITUDE_BORDURES_BOT), new LatLng(TOULOUSE_LATITUDE_BORDURES_TOP, TOULOUSE_LONGITUDE_BORDURES_TOP));
        mMap.setLatLngBoundsForCameraTarget(toulouseBounds);
        // By default, map zoom on Toulouse
        LatLng toulouse = new LatLng(TOULOUSE_LATITUDE, TOULOUSE_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toulouse, ZOOM_LVL_BY_DEFAULT));
        // Set user localisation and ask permission to get it
        checkUserLocationPermission();
        //TODO placer également cet appel dans le OnCreate.

        //Configuration map
        UiSettings mMapConfig = mMap.getUiSettings();
        mMapConfig.setZoomControlsEnabled(true);
        mMapConfig.setCompassEnabled(true);

        ArrayList<Bar> bars = MainActivity.creatingBars(); //Instantiation of an arrayList of café-concert objects
        CreateMarkers(bars);

    }

    /* Creating bars markers on the map with a list of bars set as arguments
     */
    public void CreateMarkers(ArrayList<Bar> bars) {
        for (Bar monBar : bars) {
            LatLng barposition = new LatLng(monBar.getGeoPoint(), monBar.getGeoShape());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(barposition);
            markerOptions.title(monBar.getBarName());
            markerOptions.snippet(monBar.getPhoneNumber() + "\r\n" + monBar.getWebUrl());

            mMap.addMarker(markerOptions);

        }
    }

    /* If all required permissions are granted, set a marker on User Position*/
    private void initLocation() {
        // Get the last known position of the user
        mMap.setMyLocationEnabled(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            moveCamera(location);
                        }
                    }
                });



        mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // initialisation de la vérification du déplacement par GPS et par réseau WIFI
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    /* Center the camera on the User Location*/
    private void moveCamera(Location userLocation) {
        LatLng latLong = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, ZOOM_LVL_ON_USER));
    }

    /* Check if User has accepted GPS location. If not, trigger "onRequestPermissionsresult".
    * If user has already refused it, draw a toast with a warning.
    */
    private void checkUserLocationPermission() { //Méthode qui teste si le GPS est bien activé
        // vérification de l'autorisation d'accéder à la position GPS
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // l'autorisation n'est pas acceptée
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.gps_has_been_refused,
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                // L'autorisation n'a jamais été réclamée, on la demande à l'utilisateur
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        } else {
            initLocation();
        }
    }

    /* Whenever permission for location GPS is asked, this method does the job.
    * If user refuses, draw a toast with a warning.
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                } else {
                    // Autorisation has been refused
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.gps_has_been_refused,
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }
        }
    }


}
