package fr.wildcodeschool.cafeconcert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InscriptionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //reprendre la firebase du projet pour y installer un nouvel utilisateur sur le noeud utilisateur
        database = FirebaseDatabase.getInstance();
        final ImageView ivlogo = findViewById(R.id.iv_logo);
        ivlogo.setAlpha(40); //value: [0-255]. Where 0 is fully transparent and 255 is fully opaque.
        final RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        ivlogo.setAnimation(null);
        final Button btLogin = findViewById(R.id.bValider);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etLogin = findViewById(R.id.etPseudo);
                EditText etPassword = findViewById(R.id.etMdp);
                EditText etPseudo = findViewById(R.id.pseudo_txt);
                String pseudo = etPseudo.getText().toString();
                String email = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty() || pseudo.isEmpty()) {
                    Toast.makeText(InscriptionActivity.this, R.string.please_password, Toast.LENGTH_SHORT).show();
                } else {
                    ivlogo.startAnimation(anim);
                    signUpUser(email, password, pseudo);

                }
            }
        });

    }

    private void signUpUser(String email, String password, final String pseudo) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(InscriptionActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String uId = user.getUid();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(pseudo)
                                    .setPhotoUri(Uri.parse(""))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("test", "User profile updated.");
                                            }
                                        }
                                    });

                            DatabaseReference refBar = database.getReference("cafeconcert");
                            final DatabaseReference refUser = database.getReference("users");
                            final DatabaseReference currentUser = refUser.child(uId).child("bars");

                            refBar.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot barSnapshot : dataSnapshot.getChildren()) {
                                        String barId = barSnapshot.getKey();
                                        Bar bar = barSnapshot.getValue(Bar.class);
                                        bar.setIsLiked(2);
                                        currentUser.child(barId).setValue(bar);

                                    }
                                    updateUI(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(InscriptionActivity.this, R.string.authentification_fail,
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(InscriptionActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // mAuth.signOut(); // forcer la deconnexion de l'utilisateur
        updateUI(currentUser);
    }
}