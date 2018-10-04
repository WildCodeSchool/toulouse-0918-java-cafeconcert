package fr.wildcodeschool.cafeconcert;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static double TOULOUSE_LATITUDE = 43.6043;
    final static double TOULOUSE_LONGITUDE = 1.4437;
    final static double TOULOUSE_LATITUDE_BORDURES_BOT = 43.565428;
    final static double TOULOUSE_LONGITUDE_BORDURES_BOT = 1.411854;
    final static double TOULOUSE_LATITUDE_BORDURES_TOP = 43.642094;
    final static double TOULOUSE_LONGITUDE_BORDURES_TOP = 1.480995;
    final static int ZOOM_LVL = 13;
    private GoogleMap mMap;
    private GestureDetectorCompat mGestureObject;
    private MotionEvent mMotionEvent;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final ImageView goList = findViewById(R.id.goList);
        //onTouch du Drawable à droite (fleche), go sur l'activity list bar
        goList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(MapsActivity.this, BarListActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        //#BurgerMenu Here I take the new toolbar to set it in my activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //TODO: à ajouter liens dans le menus (le rendre fonctionnel)
    }

    //#BurgerMenu For not leaving the activity immediately
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // TODO : à supprimer quand la méthode de création de bar sera à jour
    public ArrayList<Bar> creatingBars() {

        ArrayList<Bar> bars = new ArrayList<Bar>();

        Bar cafePopulaire = new Bar("Le Café Populaire", "05 61 63 07 00", 43.60441137, 1.451458914, "https://www.facebook.com/cafepopulairetoulouse/", 0, R.drawable.common_full_open_on_phone);
        Bar saintDesSeins = new Bar("Le Saint des Seins", "05 61 22 11 39", 43.60347105, 1.436443523, "www.lesaintdesseins.com", 1, R.drawable.common_full_open_on_phone);
        Bar puertoHabana = new Bar("Puerto Habana", "05 61 54 45 61", 43.59900788, 1.45668714, "http://www.puerto-habana.com", 2, R.drawable.common_full_open_on_phone);
        Bar citronBleu = new Bar("Citron Bleu", "05 62 17 54 06", 43.59882153, 1.442793398, "http://www.lecitronbleu.fr", 0, R.drawable.common_full_open_on_phone);
        Bar maisonBlanche = new Bar("La Maison Blanche", "09 52 92 57 22", 43.60972002, 1.439207355, "www.cafe-maison-blanche.fr", 1, R.drawable.common_full_open_on_phone);
        Bar carsonCity = new Bar("Le Carson City", "05 61 42 02 22", 43.59822031, 1.434274003, "http://www.restaurant-carsoncity.com", 2, R.drawable.common_full_open_on_phone);

        bars.add(cafePopulaire);
        bars.add(saintDesSeins);
        bars.add(puertoHabana);
        bars.add(citronBleu);
        bars.add(maisonBlanche);
        bars.add(carsonCity);

        return bars;

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
        // mettre en place les bordures de la carte
        LatLngBounds toulouseBounds = new LatLngBounds(
                new LatLng(TOULOUSE_LATITUDE_BORDURES_BOT, TOULOUSE_LONGITUDE_BORDURES_BOT), new LatLng(TOULOUSE_LATITUDE_BORDURES_TOP, TOULOUSE_LONGITUDE_BORDURES_TOP));
        mMap.setLatLngBoundsForCameraTarget(toulouseBounds);
        // Zoomer sur Toulouse à partir d'un point
        LatLng toulouse = new LatLng(TOULOUSE_LATITUDE, TOULOUSE_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toulouse, ZOOM_LVL));


        ArrayList<Bar> bars = creatingBars(); //Instantiation of an arrayList of café-concert objects
        CreateMarkers(bars);
        // Todo : Supprimer creatingBars et remplacer par la nouvelle méthode de création de bars


    }

    // Creation des marqueurs via une liste de bars - Assigne un marqueur pour chaque bar à partir de son nom, sa position

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureObject != null) {
            this.mGestureObject.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


    //now create the gesture Object Class


}
