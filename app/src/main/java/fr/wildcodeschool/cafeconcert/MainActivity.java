package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button btLogin = findViewById(R.id.button_connexion);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etLogin = findViewById(R.id.edit_mail);
                EditText etPassword = findViewById(R.id.edit_password);
                String email = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "BONSOIR", Toast.LENGTH_SHORT).show();
                } else {
                    signInUser(email, password);
                }
            }
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // TODO : faire une requête pour récupérer les données supplementaire de l'utilisateur
                            String uId = user.getUid();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //mAuth.signOut(); // forcer la deconnexion de l'utilisateur
        updateUI(currentUser);
    }

    /*Return an ArrayList "bars" composed with 6 instantiated "Bars" Objects.
    Useful for tests before implementation of an appropriate database */
    public static ArrayList<Bar> creatingBars(Context context) {

        ArrayList<Bar> bars = new ArrayList<Bar>();

        Bar cafePopulaire = new Bar("Le Café Populaire", "05 61 63 07 00", 43.60441137, 1.451458914, "https://www.facebook.com/cafepopulairetoulouse/", 0, R.drawable.common_full_open_on_phone, context);
        Bar saintDesSeins = new Bar("Le Saint des Seins", "05 61 22 11 39", 43.60347105, 1.436443523, "www.lesaintdesseins.com", 1, R.drawable.common_full_open_on_phone, context);
        Bar puertoHabana = new Bar("Puerto Habana", "05 61 54 45 61", 43.59900788, 1.45668714, "http://www.puerto-habana.com", 2, R.drawable.common_full_open_on_phone, context);
        Bar citronBleu = new Bar("Citron Bleu", "05 62 17 54 06", 43.59882153, 1.442793398, "http://www.lecitronbleu.fr", 0, R.drawable.common_full_open_on_phone, context);
        Bar maisonBlanche = new Bar("La Maison Blanche", "09 52 92 57 22", 43.60972002, 1.439207355, "www.cafe-maison-blanche.fr", 1, R.drawable.common_full_open_on_phone, context);
        Bar carsonCity = new Bar("Le Carson City", "05 61 42 02 22", 43.59822031, 1.434274003, "http://www.restaurant-carsoncity.com", 2, R.drawable.common_full_open_on_phone, context);

        bars.add(cafePopulaire);
        bars.add(saintDesSeins);
        bars.add(puertoHabana);
        bars.add(citronBleu);
        bars.add(maisonBlanche);
        bars.add(carsonCity);

        return bars;
    }


}