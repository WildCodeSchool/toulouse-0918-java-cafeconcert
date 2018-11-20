package fr.wildcodeschool.cafeconcert;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class BarListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static int CLOSEST_BAR_NUMBERS = 5;
    BarAdapter adapter;
    private DrawerLayout drawer;
    private ArrayList<Bar> mBars = new ArrayList<>();
    private Location mUserLocation = new Location("User");
    private LocationManager mLocationManager = null;
    private boolean filter = false;
    private boolean mFilterDistance = false;
    private ListView listBar;
    private String mUId;
    private FirebaseAuth mAuth;
    private SingletonBar mSingleton;
    private String mToastlanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        mToastlanguage = getString(R.string.you_need_to_be_connected);
        listBar = findViewById(R.id.list_bar);

        //Get bars and user information
        mAuth = FirebaseAuth.getInstance();
        mSingleton = SingletonBar.getInstance();
        mUId = mSingleton.getUserID();
        mBars = mSingleton.getBars();

        //#BurgerMenu Here I take the new toolbar to set it in my activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_bar_list);
        checkMenuCreated(drawer);
        //If user is guest, he can connect. If he is yet connected, he can disconnect
        connexionOrDeconnexionFromMenuBurger(navigationView);

        View header = navigationView.getHeaderView(0);
        final ImageView image = header.findViewById(R.id.image_bar);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.startAnimation(AnimationUtils.loadAnimation(BarListActivity.this, R.anim.shake));
            }
        });

        getUserLocation();
    }

    public void connexionOrDeconnexionFromMenuBurger(NavigationView navigationView) {

        MenuItem connexion = navigationView.getMenu().findItem(R.id.connexion);
        MenuItem deconnexion = navigationView.getMenu().findItem(R.id.deconnexion);
        connexion.setVisible(false);
        if (checkIfGuest(mUId)) {
            deconnexion.setVisible(false);
            connexion.setVisible(true);
        }
    }

    public boolean checkIfGuest(String uId) {
        return uId.equals("guest");
    }

    public ArrayList<Bar> pickClosestBars(ArrayList<Bar> myBars, int range) {
        ArrayList<Bar> closestBars = new ArrayList<>();
        range = Math.min(range, myBars.size());

        for (int i = 0; i < range; i++) {
            closestBars.add(arrayFilterByDistance(myBars).get(i));
        }
        return closestBars;
    }

    public ArrayList<Bar> arrayFilterByDistance(ArrayList<Bar> myBars) {

        Location barLocation = new Location("Bar");
        barLocation.setTime(new Date().getTime());

        if (mUserLocation == null) {
            Toast.makeText(getApplicationContext(), R.string.location_impossible, Toast.LENGTH_LONG).show();
            return myBars;
        }

        for (Bar bar : myBars) {
            barLocation.setLatitude(bar.getGeoPoint());
            barLocation.setLongitude(bar.getGeoShape());
            bar.setDistanceFromUser(mUserLocation.distanceTo(barLocation));
        }

        for (int i = 0; i <= myBars.size() - 1; i++) {
            for (int j = i; j <= myBars.size() - 1; j++) {
                if (myBars.get(j).getDistanceFromUser() < myBars.get(i).getDistanceFromUser()) {
                    Collections.swap(myBars, i, j);
                }
            }
        }
        return myBars;
    }

    @SuppressWarnings("MissingPermission")
    public void getUserLocation() {

        mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        mUserLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mUserLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        initBarVisualisation();

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mUserLocation = location;
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

    public void initBarVisualisation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        filter = sharedPreferences.getBoolean("filter", false);
        mFilterDistance = sharedPreferences.getBoolean("distanceFilter", false);

        if (mFilterDistance && !filter) {
            adapter = new BarAdapter(BarListActivity.this,
                    pickClosestBars(mBars, CLOSEST_BAR_NUMBERS));
        } else if (mFilterDistance && filter) {
            adapter = new BarAdapter(BarListActivity.this,
                    pickClosestBars(MainActivity.arrayFilter(mBars), CLOSEST_BAR_NUMBERS));
        } else if (!mFilterDistance && filter) {
            adapter = new BarAdapter(BarListActivity.this,
                    arrayFilterByDistance(MainActivity.arrayFilter(mBars)));
        } else {
            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(mBars));
        }
        listBar.setAdapter(adapter);
    }

    public void checkMenuCreated(DrawerLayout drawer) {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
                checkboxFilter.setChecked(filter);
                final CheckBox distanceCheckboxfilter = findViewById(R.id.checkbox_distance);
                distanceCheckboxfilter.setChecked(mFilterDistance);

                // Guest restriction
                if (checkIfGuest(mUId)) {
                    checkboxFilter.setClickable(false);
                    distanceCheckboxfilter.setClickable(false);
                }

                checkboxFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        // Guest restriction
                        if (checkIfGuest(mUId)) {
                            Toast.makeText(getApplicationContext(), mToastlanguage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (checkboxFilter.isChecked() && !mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    arrayFilterByDistance(MainActivity.arrayFilter(mBars)));
                        } else if (checkboxFilter.isChecked() && mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(MainActivity.arrayFilter(mBars), CLOSEST_BAR_NUMBERS));
                        } else if (!checkboxFilter.isChecked() && mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(mBars, CLOSEST_BAR_NUMBERS));
                        } else {
                            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(mBars));
                        }
                        listBar.setAdapter(adapter);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BarListActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("filter", checkboxFilter.isChecked());
                        editor.commit();
                        filter = checkboxFilter.isChecked();
                    }
                });

                distanceCheckboxfilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        // Guest restriction
                        if (checkIfGuest(mUId)) {
                            Toast.makeText(getApplicationContext(), mToastlanguage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (distanceCheckboxfilter.isChecked() && !filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(mBars, CLOSEST_BAR_NUMBERS));
                        } else if (distanceCheckboxfilter.isChecked() && filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(MainActivity.arrayFilter(mBars), CLOSEST_BAR_NUMBERS));
                        } else if (!distanceCheckboxfilter.isChecked() && filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    MainActivity.arrayFilter(arrayFilterByDistance(mBars)));
                        } else {
                            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(mBars));
                        }
                        listBar.setAdapter(adapter);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BarListActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("distanceFilter", distanceCheckboxfilter.isChecked());
                        editor.commit();
                        mFilterDistance = distanceCheckboxfilter.isChecked();
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

    //#BurgerMenu
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

    //#BurgerMenu put links between activities
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
        CheckBox checkboxDistance = findViewById(R.id.checkbox_distance);
        switch (item.getItemId()) {
            case R.id.nav_profile:
                // Guest restriction
                if (checkIfGuest(mUId)) {
                    Toast.makeText(getApplicationContext(), mToastlanguage, Toast.LENGTH_LONG).show();
                    break;
                }
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.nav_map:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.nav_bar_list:
                startActivity(new Intent(this, BarListActivity.class));
                break;
            case R.id.app_bar_switch:
                checkboxFilter.setChecked(!checkboxFilter.isChecked());
                break;
            case R.id.app_bar_distance:
                checkboxDistance.setChecked(!checkboxDistance.isChecked());
                break;
            case R.id.deconnexion:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BarListActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("distanceFilter", false);
                editor.putBoolean("filter", false);
                editor.commit();
                mAuth.signOut();
                mSingleton.clear();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.connexion:
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //#BurgerMenu For not leaving the activity immediately
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(this, MapsActivity.class));
        }
    }
}
