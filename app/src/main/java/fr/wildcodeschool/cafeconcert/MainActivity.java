package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button_visiteur);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });
        Button buttonScription = (Button) findViewById(R.id.button_inscription);
        buttonScription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,InscriptionActivity.class));
            }
        });
        ArrayList<Bar> bars = creatingBars(MainActivity.this); //Instantiation of an arrayList of café-concert objects

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
    }

    /*Return an ArrayList "bars" composed with 6 instantiated "Bars" Objects.
    Useful for tests before implementation of an appropriate database */
    public static ArrayList<Bar> creatingBars(Context context) {

        ArrayList<Bar> bars = new ArrayList<Bar>();

        Bar cafePopulaire = new Bar("Le Café Populaire", "05 61 63 07 00", 43.60441137, 1.451458914, "https://www.facebook.com/cafepopulairetoulouse/", 0, R.drawable.photodecafe, context);
        Bar saintDesSeins = new Bar("Le Saint des Seins", "05 61 22 11 39", 43.60347105, 1.436443523, "www.lesaintdesseins.com", 1, R.drawable.photodecafe, context);
        Bar puertoHabana = new Bar("Puerto Habana", "05 61 54 45 61", 43.59900788, 1.45668714, "http://www.puerto-habana.com", 2, R.drawable.photodecafe, context);
        Bar citronBleu = new Bar("Citron Bleu", "05 62 17 54 06", 43.59882153, 1.442793398, "http://www.lecitronbleu.fr", 0, R.drawable.photodecafe, context);
        Bar maisonBlanche = new Bar("La Maison Blanche", "09 52 92 57 22", 43.60972002, 1.439207355, "www.cafe-maison-blanche.fr", 1, R.drawable.photodecafe, context);
        Bar carsonCity = new Bar("Le Carson City", "05 61 42 02 22", 43.59822031, 1.434274003, "http://www.restaurant-carsoncity.com", 2, R.drawable.photodecafe, context);

        bars.add(cafePopulaire);
        bars.add(saintDesSeins);
        bars.add(puertoHabana);
        bars.add(citronBleu);
        bars.add(maisonBlanche);
        bars.add(carsonCity);

        return bars;
    }

    /*Launch Googlemaps on Navigation mode.
     * User position as departure, bar coordonates as destination */
    public static void setNavigation(ImageView navigate, final Bar bar, final Context context) {

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?.34&daddr=" + bar.getGeoPoint()+ "," + bar.getGeoShape()));
                context.startActivity(intent);
            }
        });
    }
}