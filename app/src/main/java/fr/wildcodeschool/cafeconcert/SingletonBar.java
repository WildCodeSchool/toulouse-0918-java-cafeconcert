package fr.wildcodeschool.cafeconcert;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class SingletonBar {
    private ArrayList<Bar> bars = new ArrayList<>();
    private String userID;

    private static final SingletonBar ourInstance = new SingletonBar();

    static SingletonBar getInstance() {
        return ourInstance;
    }

    private SingletonBar() {
    }

    public void initBars() {

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference refGuest = firebaseDatabase.getReference("cafeconcert");
        DatabaseReference refUser = firebaseDatabase.getReference("users");

        DatabaseReference myRef;
        if (this.userID.equals("guest")) {
            myRef = refGuest;
        } else {
            myRef = refUser.child(this.userID).child("bars");
        }

        myRef.addValueEventListener(new ValueEventListener() {
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Bar> getBars() {
        return bars;
    }

    public void setBars(ArrayList<Bar> bars) {
        this.bars = bars;
    }

    public void setNewPreferences(int newPreferences, Bar bar) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference refUsers = firebaseDatabase.getReference("users");
        DatabaseReference refBarsOfCurrentUser = refUsers.child(this.userID).child("bars");
        refBarsOfCurrentUser.child(bar.getBarId()).child("isLiked").setValue(newPreferences);

        for(Bar myBar : this.bars) {
            if(myBar.getBarName().equals(bar.getBarName())) {
                myBar.setIsLiked(newPreferences);
            }

        }
    }

}
