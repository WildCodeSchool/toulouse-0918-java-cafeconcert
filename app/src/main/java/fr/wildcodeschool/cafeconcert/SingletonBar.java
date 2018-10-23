package fr.wildcodeschool.cafeconcert;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class SingletonBar {
    ArrayList<Bar> bars = new ArrayList<>();

    private static final SingletonBar ourInstance = new SingletonBar();

    static SingletonBar getInstance() {
        return ourInstance;
    }

    private SingletonBar() {
    }

    public void initBars(String mUId) {

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference refUser = firebaseDatabase.getReference("users").child(mUId).child("bars");

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bars.clear();
                for (DataSnapshot barSnapshot : dataSnapshot.getChildren()) {
                    final Bar bar = barSnapshot.getValue(Bar.class);
                    bars.add(bar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public ArrayList<Bar> getFavorites() {
        ArrayList<Bar> favorites = new ArrayList<>();
        for (Bar bar : bars) {
            if (bar.getIsLiked() == 1) {
                favorites.add(bar);
            }
        }
        return favorites;
    }
}
