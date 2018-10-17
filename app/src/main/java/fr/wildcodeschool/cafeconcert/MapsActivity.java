package fr.wildcodeschool.cafeconcert;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    final static double TOULOUSE_LATITUDE = 43.6043;
    final static double TOULOUSE_LONGITUDE = 1.4437;
    final static double TOULOUSE_LATITUDE_BORDURES_BOT = 43.565428;
    final static double TOULOUSE_LONGITUDE_BORDURES_BOT = 1.411854;
    final static double TOULOUSE_LATITUDE_BORDURES_TOP = 43.642094;
    final static double TOULOUSE_LONGITUDE_BORDURES_TOP = 1.480995;
    final static int POPUP_POSITION_X = 0;
    final static int POPUP_POSITION_Y = 0;
    final static int MARKER_HEIGHT = 72;
    final static int MARKER_WIDTH = 72;
    final static int ZOOM_LVL_BY_DEFAULT = 13;
    final static float ZOOM_LVL_ON_USER = 15.76f;

    private PopupWindow popUp;
    private GoogleMap mMap;
    private ArrayList<Bar> bars = new ArrayList<>();
    private ArrayList<Bar> filterBars;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private GestureDetectorCompat mGestureObject;
    private MotionEvent mMotionEvent;
    private DrawerLayout drawer;
    private LocationManager mLocationManager = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean filter = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Setting map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //#BurgerMenu Here I take the new toolbar to set it in my activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //#BurgerMenu
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_map);
        checkMenuCreated(drawer);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        filter = sharedPreferences.getBoolean("filter", false);

        //#Language
        final TextView tvLangues = findViewById(R.id.tv_langues);

        tvLangues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences languePreferences = getSharedPreferences("CAFE_CONCERT", MODE_PRIVATE);
                String lang = languePreferences.getString("Fav_langue", "");
                Configuration config = getBaseContext().getResources().getConfiguration();
                setLanguage(lang.equals("fr") ? "en" : "fr");
            }
        });
    }

    //#Language
    public void setLanguage(String lang) {
        final SharedPreferences languePreferences = getSharedPreferences("CAFE_CONCERT", MODE_PRIVATE);
        SharedPreferences.Editor editor = languePreferences.edit();
        editor.putString("Fav_langue", lang);
        editor.commit();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        drawer.closeDrawer(GravityCompat.START);
        recreate();
    }

    public void initBar() {

        FirebaseDatabase baseEnFeu = FirebaseDatabase.getInstance();
        DatabaseReference refBar = baseEnFeu.getReference("cafeconcert");

        refBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bars.clear();

                for (DataSnapshot barSnapshot : dataSnapshot.getChildren()) {
                    Bar bar = barSnapshot.getValue(Bar.class);
                    bar.setInitIsLiked(2, MapsActivity.this);
                    bar.setContext(MapsActivity.this);
                    bar.setPicture(R.drawable.photodecafe);
                    bars.add(bar);
                }
                initMarkers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //#BurgerMenu
    public void checkMenuCreated(DrawerLayout drawer) {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
                checkboxFilter.setChecked(filter);

                checkboxFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (checkboxFilter.isChecked()) {
                            mMap.clear();
                            createMarkers(arrayFilter(bars));
                        } else {
                            mMap.clear();
                            createMarkers(bars);
                        }
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("filter", checkboxFilter.isChecked());
                        editor.commit();
                        filter = checkboxFilter.isChecked();
                    }
                });
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    //#ShareMenu : Inflate the share menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    //#ShareMenu : Send a text
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = getString(R.string.share_text);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //#BurgerMenu links
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
        //filterSwitch();
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.nav_map:
                break;
            case R.id.nav_bar_list:
                startActivity(new Intent(this, BarListActivity.class));
                break;
            case R.id.app_bar_switch:
                checkboxFilter.setChecked(!checkboxFilter.isChecked());
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<Bar> arrayFilter(ArrayList<Bar> bars) {
        ArrayList<Bar> arrayFilter = new ArrayList<>();
        for (Bar monBar : bars) {
            if (monBar.getIsLiked() == 1) {
                arrayFilter.add(monBar);
            }
        }
        return arrayFilter;
    }

    //#BurgerMenu For not leaving the activity immediately
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /**
         * Manipulates the map once avalable.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

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

        //Configuration map
        UiSettings mMapConfig = mMap.getUiSettings();
        mMapConfig.setZoomControlsEnabled(true);
        mMapConfig.setCompassEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        filter = sharedPreferences.getBoolean("filter", false);
        //Instantiation of an arrayList of café-concert objects
        //bars = (MainActivity.creatingBars(this));
        initBar();
    }

    public void initMarkers() {
        if (filter) {
            createMarkers(arrayFilter(bars));
        } else {
            createMarkers(bars);
        }
    }

    /* Generate a bitmap to be used as custom marker.
     * Is different depending on bar status (liked/disliked/neutral) */
    private Bitmap setCustomsMarkers(Bar monBar) {


        Bitmap initialLikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialNeutralMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_ping);
        Bitmap neutralMarker = Bitmap.createScaledBitmap(initialNeutralMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        switch (monBar.getIsLiked()) {
            case 1:
                return likeMarker;

            case 0:
                return dislikeMarker;

            default:
                return neutralMarker;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureObject != null) {
            this.mGestureObject.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    /* Creating bars markers on the map with a list of bars set as arguments
     */
    public void createMarkers(ArrayList<Bar> bars) {

        for (final Bar monBar : bars) {
            LatLng barposition = new LatLng(monBar.getGeoPoint(), monBar.getGeoShape());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(barposition);
            markerOptions.snippet(null);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(setCustomsMarkers(monBar)));
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(monBar);
            mMarkers.add(marker);
            boolean focus = false;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                popupBuilder(marker);
                return false;
            }
        });
    }

    public void adaptLikesButton(ImageView like, ImageView dontLike, Bar bar, Marker marker) {


        Bitmap initialLikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nDislikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_dislike_icon);
        Bitmap neutralDislikeMarker = Bitmap.createScaledBitmap(nDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nLikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_like_icon);
        Bitmap neutralLikeMarker = Bitmap.createScaledBitmap(nLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialNeutralMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_ping);
        Bitmap neutralMarker = Bitmap.createScaledBitmap(initialNeutralMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        like.setImageBitmap(neutralLikeMarker);
        dontLike.setImageBitmap(neutralDislikeMarker);

        //0 dislike, 1 like, 2 neutral
        if (bar.getIsLiked() == 1) {
            like.setImageBitmap(likeMarker);
            dontLike.setImageBitmap(neutralDislikeMarker);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(likeMarker));
        } else if (bar.getIsLiked() == 0) {
            dontLike.setImageBitmap(dislikeMarker);
            like.setImageBitmap(neutralLikeMarker);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(dislikeMarker));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(neutralMarker));
        }
    }

    public void setUserOpinion(final ImageView like, final ImageView dontLike, final Bar bar, final Marker marker) {

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.getIsLiked() != 1) {
                    bar.setIsLiked(1);
                    adaptLikesButton(like, dontLike, bar, marker);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, bar, marker);
                }
                if (filter) {
                    mMap.clear();
                    createMarkers(arrayFilter(bars));
                }
            }

        });


        dontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.getIsLiked() != 0) {
                    bar.setIsLiked(0);
                    adaptLikesButton(like, dontLike, bar, marker);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, bar, marker);
                }
                if (filter) {
                    mMap.clear();
                    createMarkers(arrayFilter(bars));
                    popUp.dismiss();
                }
            }
        });


    }

    private void popupBuilder(Marker marker) {

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = (int) Math.round(size.x * 0.6);
        // int height = (int) Math.round(size.y * 0.6);


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.custom_info_adapter, null);

        //creation fenetre popup
        boolean focusable = true;
        popUp = new PopupWindow(popUpView, width, ListPopupWindow.WRAP_CONTENT, focusable);

        //show popup
        popUp.showAtLocation(popUpView, Gravity.CENTER, POPUP_POSITION_X, POPUP_POSITION_Y);
        final Bar bar = (Bar) marker.getTag();
        TextView barName = popUpView.findViewById(R.id.barTitlePopup);
        ImageView phone = popUpView.findViewById(R.id.phoneButton);
        ImageView web = popUpView.findViewById(R.id.webButton);
        ImageView navigate = popUpView.findViewById(R.id.mapButton);
        ImageView photoBar = popUpView.findViewById(R.id.photoBar);
        ImageView like = popUpView.findViewById(R.id.likeButton);
        ImageView dontLike = popUpView.findViewById(R.id.dontLikeButton);
        adaptLikesButton(like, dontLike, bar, marker);
        setUserOpinion(like, dontLike, bar, marker);
        navigate.setImageResource(R.mipmap.navigate);
        photoBar.setImageResource(R.mipmap.fonddecran);
        phone.setImageResource(R.mipmap.phonelogo);
        web.setImageResource(R.mipmap.globeicon);
        popUpView.setBackgroundResource(R.drawable.fondpopup);
        barName.setText(bar.getBarName());

        //Navigation button
        MainActivity.setNavigation(navigate, bar, MapsActivity.this);

        //Phone button
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + bar.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        //Website button
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bar.getWebUrl();
                if (bar.getWebUrl().isEmpty()) {
                    Toast.makeText(MapsActivity.this, R.string.no_website, Toast.LENGTH_LONG);
                } else {
                    if (url.charAt(0) == 'w') {
                        url = "http://" + url;
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    MapsActivity.this.startActivity(i);
                }
            }
        });
    }

    /* If all required permissions are granted, set a marker on User Position*/

    @SuppressWarnings("MissingPermission")
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
    private void checkUserLocationPermission() {
        //Méthode qui teste si le GPS est bien activé
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


