package fr.wildcodeschool.cafeconcert;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
  // TODO : à supprimer quand la méthode de création de bar sera à jour
    public ArrayList<Bar> creatingBars() {

        ArrayList<Bar> bars = new ArrayList<Bar>();

        Bar cafePopulaire = new Bar("Le Café Populaire", "05 61 63 07 00", 43.60441137, 1.451458914, "https://www.facebook.com/cafepopulairetoulouse/", 0);
        Bar saintDesSeins = new Bar("Le Saint des Seins", "05 61 22 11 39", 43.60347105, 1.436443523, "www.lesaintdesseins.com", 1);
        Bar puertoHabana = new Bar("Puerto Habana", "05 61 54 45 61", 43.59900788, 1.45668714, "http://www.puerto-habana.com", 2);
        Bar citronBleu = new Bar("Citron Bleu", "05 62 17 54 06", 43.59882153, 1.442793398, "http://www.lecitronbleu.fr", 0);
        Bar maisonBlanche = new Bar("La Maison Blanche", "09 52 92 57 22", 43.60972002, 1.439207355, "www.cafe-maison-blanche.fr", 1);
        Bar carsonCity = new Bar("Le Carson City", "05 61 42 02 22", 43.59822031, 1.434274003, "http://www.restaurant-carsoncity.com", 2);

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
                new LatLng(43.565428, 1.411854), new LatLng(43.642094 , 1.480995));
        mMap.setLatLngBoundsForCameraTarget(toulouseBounds);

        // Zoomer sur Toulouse à partir d'un point
        LatLng toulouse = new LatLng(43.6043, 1.4437);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toulouse, 13));



        ArrayList<Bar> bars = creatingBars(); //Instantiation of an arrayList of café-concert objects
        CreateMarkers(bars);
        // Todo : Supprimer creatingBars et remplacer par la nouvelle méthode de création de bars


    }

    // Creation des marqueurs via une liste de bars - Assigne un marqueur pour chaque bar à partir de son nom, sa position

    public void CreateMarkers(ArrayList<Bar> bars){
        for (Bar monBar :bars) {
            LatLng barposition = new LatLng(monBar.getGeoPoint(), monBar.getGeoShape());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(barposition);
            markerOptions.title(monBar.getBarName());
            markerOptions.snippet(monBar.getPhoneNumber()+ "\r\n" + monBar.getWebUrl());

            mMap.addMarker(markerOptions);

        }
    }


}
