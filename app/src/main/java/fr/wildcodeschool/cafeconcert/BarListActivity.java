package fr.wildcodeschool.cafeconcert;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BarListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private GestureDetectorCompat mGestureObject;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        //Take the bars's info already created in MainActivity
        ListView listBar = findViewById(R.id.list_bar);
        ArrayList<Bar> arrayListBar = MainActivity.creatingBars();

        BarAdapter adapter = new BarAdapter(this, arrayListBar);
        listBar.setAdapter(adapter);
        mGestureObject = new GestureDetectorCompat(this, new BarListActivity.LearnGesture());
        //Setting button to go to MapsActivity
        final ImageView goToMap = findViewById(R.id.goToMap);
        MapsActivity.transitionBetweenActivity(goToMap, BarListActivity.this, MapsActivity.class);

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
    }

    //#BurgerMenu put links between activities
    //TODO: Ajouter les liens vers le profile et les favoris
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_map:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.nav_bar_list:
                startActivity(new Intent(this, BarListActivity.class));
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Shared", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //now create the gesture Object Class

    //swipe pour aller sur l'activité map
    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        //SimpleOnGestureListener is the listener for the gestures we want

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (event2.getX() > event1.getX() && (Math.abs(event2.getY() - event1.getY()) < 150)) {

                Intent intent = new Intent(BarListActivity.this, MapsActivity.class);
                startActivity(intent);
                //swipe gauche à droite

            } else if (event2.getX() < event1.getX()) {
                //swipe droite à gauche
            }
            return true;
        }
    }
}
