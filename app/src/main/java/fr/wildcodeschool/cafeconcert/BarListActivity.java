package fr.wildcodeschool.cafeconcert;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BarListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private GestureDetectorCompat mGestureObject;
    private DrawerLayout drawer;
    private ArrayList<Bar> bars;
    private boolean filter = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        //Take the bars's info already created in MainActivity
        ListView listBar = findViewById(R.id.list_bar);
        bars = MainActivity.creatingBars(BarListActivity.this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        filter = sharedPreferences.getBoolean("filter", false);
        if (filter) {
            BarAdapter adapter = new BarAdapter(this, arrayFilter(bars));
            listBar.setAdapter(adapter);
        } else {
            BarAdapter adapter = new BarAdapter(this, bars);
            listBar.setAdapter(adapter);
        }

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
        checkMenuCreated(drawer);
    }


    public void checkMenuCreated(DrawerLayout drawer) {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
                checkboxFilter.setChecked(filter);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    //#BurgerMenu
    //#ShareMenu : Inflate the share menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    //#ShareMenu : Send a text
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = getString(R.string.share_text);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //#BurgerMenu put links between activities
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ListView listBar = findViewById(R.id.list_bar);
        CheckBox checkboxFilter = findViewById(R.id.checkBoxFilter);
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.nav_map:
                Intent intentMap = new Intent(this, MapsActivity.class);
                startActivity(intentMap);
                break;
            case R.id.nav_bar_list:
                Intent intentList = new Intent(this, BarListActivity.class);
                startActivity(intentList);
                break;
            case R.id.filterOk:
                if (checkboxFilter.isChecked()) {
                    BarAdapter adapter = new BarAdapter(this, arrayFilter(bars));
                    listBar.setAdapter(adapter);
                } else {
                    BarAdapter adapter = new BarAdapter(this, bars);
                    listBar.setAdapter(adapter);
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("filter", checkboxFilter.isChecked());
                editor.commit();
                filter = checkboxFilter.isChecked();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Shared", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<Bar> arrayFilter(ArrayList<Bar> bars) {
        ArrayList<Bar> arrayFilter = new ArrayList<>();
        for (Bar monBar : bars) {
            if (monBar.getIsLiked() == 1) {
                arrayFilter.add(monBar);
            }
        }
        return arrayFilter;
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

}
