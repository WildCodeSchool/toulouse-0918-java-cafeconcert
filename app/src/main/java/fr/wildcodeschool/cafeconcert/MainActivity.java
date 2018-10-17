package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button_visiteur);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
        Button buttonScription = (Button) findViewById(R.id.button_inscription);
        buttonScription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InscriptionActivity.class));
            }
        });
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
                    Toast.makeText(MainActivity.this, "BONSOIR", Toast.LENGTH_SHORT).show(); // TODO WUT ?
                } else {
                    signInUser(email, password);
                }
            }
        });

        //#Language
        Configuration config = getBaseContext().getResources().getConfiguration();
        SharedPreferences languePreferences = getSharedPreferences("CAFE_CONCERT", MODE_PRIVATE);
        String lang = languePreferences.getString("Fav_langue", "");

        if (!config.locale.getLanguage().equals(lang)) {
            setLanguage(lang);
        }

    }

    //#Language
    public void setLanguage(String lang) {
        SharedPreferences languePreferences = getSharedPreferences("CAFE_CONCERT", MODE_PRIVATE);
        SharedPreferences.Editor editor = languePreferences.edit();
        editor.putString("Fav_langue", lang);
        editor.commit();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        recreate();
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
        mAuth.signOut(); // forcer la deconnexion de l'utilisateur
        updateUI(currentUser);
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

    public static ArrayList<Bar> arrayFilter(ArrayList<Bar> bars) {
        ArrayList<Bar> arrayFilter = new ArrayList<>();
        for (Bar monBar : bars) {
            if (monBar.getIsLiked() == 1) {
                arrayFilter.add(monBar);
            }
        }
        return arrayFilter;
    }

}