package fr.wildcodeschool.cafeconcert;



import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class BarListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private GestureDetectorCompat mGestureObject;
    private DrawerLayout drawer;
    private ArrayList<Bar> bars;
    private Location mUserLocation = new Location("User");
    private LocationManager mLocationManager = null;
    private boolean filter = false;
    private boolean mFilterDistance = false;
    BarAdapter adapter;
    private ListView listBar;

    final static int CLOSEST_BAR_NUMBERS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        bars = new ArrayList<>();
        listBar= findViewById(R.id.list_bar);

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

        getUserLocation();
    }

    public ArrayList<Bar> pickClosestBars(ArrayList<Bar> myBars, int range) {
        ArrayList<Bar> closestBars = new ArrayList<>();
        range = Math.min(range, myBars.size());

        for (int i = 0 ; i < range ; i ++) {
            closestBars.add(arrayFilterByDistance(myBars).get(i));
        }
        return closestBars;
    }

    public ArrayList<Bar> arrayFilterByDistance(ArrayList<Bar> myBars) {

        for (Bar bar : myBars) {
            bar.setDistanceFromUser(mUserLocation.distanceTo(bar.getBarLocation()));
        }

        for (int i = 0 ; i <= myBars.size()-1 ; i ++ ) {
            for (int j = i ; j <= myBars.size()-1 ; j ++) {
                if (myBars.get(j).getDistanceFromUser() < myBars.get(i).getDistanceFromUser() ) {
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
        initBarList();

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
                    pickClosestBars(bars, CLOSEST_BAR_NUMBERS));
        } else if (mFilterDistance && filter) {
            adapter = new BarAdapter(BarListActivity.this,
                    pickClosestBars(MainActivity.arrayFilter(bars), CLOSEST_BAR_NUMBERS));
        } else if (!mFilterDistance && filter) {
            adapter = new BarAdapter(BarListActivity.this,
                    arrayFilterByDistance(MainActivity.arrayFilter(bars)));
        } else {
            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(bars));
        }
        listBar.setAdapter(adapter);
    }

    public void initBarList() {

        FirebaseDatabase baseEnFeu = FirebaseDatabase.getInstance();
        DatabaseReference refBar = baseEnFeu.getReference("cafeconcert");

        refBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bars.clear();
                for (DataSnapshot barSnapshot : dataSnapshot.getChildren()){
                    Bar bar = barSnapshot.getValue(Bar.class);
                    bar.setInitIsLiked(2, BarListActivity.this);
                    bar.setContext(BarListActivity.this);
                    bar.setBarLocation();


                    bars.add(bar);
                }
                initBarVisualisation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkMenuCreated(DrawerLayout drawer) {

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
                checkboxFilter.setChecked(filter);
                final CheckBox distanceCheckboxfilter = findViewById(R.id.checkbox_distance);
                distanceCheckboxfilter.setChecked(mFilterDistance);

                checkboxFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (checkboxFilter.isChecked() && !mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    arrayFilterByDistance(MainActivity.arrayFilter(bars)));
                        } else if (checkboxFilter.isChecked() && mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(MainActivity.arrayFilter(bars), CLOSEST_BAR_NUMBERS));
                        } else if (!checkboxFilter.isChecked() && mFilterDistance) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(bars, CLOSEST_BAR_NUMBERS));
                        } else {
                            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(bars));
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

                        if (distanceCheckboxfilter.isChecked() && !filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(bars, CLOSEST_BAR_NUMBERS));
                        } else if (distanceCheckboxfilter.isChecked() && filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    pickClosestBars(MainActivity.arrayFilter(bars), CLOSEST_BAR_NUMBERS));
                        } else if (!distanceCheckboxfilter.isChecked() && filter) {
                            adapter = new BarAdapter(BarListActivity.this,
                                    MainActivity.arrayFilter(arrayFilterByDistance(bars)));
                        } else {
                            adapter = new BarAdapter(BarListActivity.this, arrayFilterByDistance(bars));
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
