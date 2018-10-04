package fr.wildcodeschool.cafeconcert;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class BarListActivity extends AppCompatActivity {

    private GestureDetectorCompat mGestureObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);


        //Take the bars's info already created in MainActivity
        ListView listBar = findViewById(R.id.list_bar);
        ArrayList<Bar> arrayListBar = MainActivity.creatingBars();

        BarAdapter adapter = new BarAdapter(this, arrayListBar);
        listBar.setAdapter(adapter);

        //Setting button to go to MapsActivity
        final ImageView goToMap = findViewById(R.id.goToMap);
        MapsActivity.transitionBetweenActivity(goToMap, BarListActivity.this, MapsActivity.class);

    }

}
