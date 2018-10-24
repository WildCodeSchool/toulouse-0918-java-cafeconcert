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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
    SingletonBar singleton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Prepare animation
        final ImageView ivlogo = findViewById(R.id.iv_logoapp);
        final RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        ivlogo.setAnimation(null);

        // Setting page items
        Button btnGuestConnexion = findViewById(R.id.button_visiteur);
        btnGuestConnexion.setOnClickListener(new View.OnClickListener() {
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

        Button btLogin = findViewById(R.id.button_connexion);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etLogin = findViewById(R.id.edit_mail);
                EditText etPassword = findViewById(R.id.edit_password);
                String email = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.please_give_password, Toast.LENGTH_SHORT).show();
                } else {
                    ivlogo.startAnimation(anim);
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
                            //String uId = user.getUid();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, R.string.authentification_fail,
                                    Toast.LENGTH_SHORT).show();
                            final ImageView ivlogo = findViewById(R.id.iv_logoapp);
                            ivlogo.setAnimation(null);
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
           launchApplication(user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void launchApplication(FirebaseUser user) {


        //TODO : Prévoir un mode visiteur ici (condition)
        singleton.setUserID(user.getUid());
        singleton.initBars(user.getUid()); //TODO a delete quand on aura adapté la méthode initBars
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    private void setUserAsGuestOrRegistered() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        singleton = SingletonBar.getInstance();
        if(currentUser != null) {

        } else {

        }

    }

    /*Launch Googlemaps on Navigation mode.
     * User position as departure, bar coordonates as destination */
    public static void setNavigation(ImageView navigate, final Bar bar, final Context context) {

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?.34&daddr=" + bar.getGeoPoint() + "," + bar.getGeoShape()));
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