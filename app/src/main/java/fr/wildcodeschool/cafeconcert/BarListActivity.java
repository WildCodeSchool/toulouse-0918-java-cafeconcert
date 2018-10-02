package fr.wildcodeschool.cafeconcert;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;

public class BarListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);

        //Take the bars's info already created in MainActivity
        ListView listBar = findViewById(R.id.list_bar);
        ArrayList<Bar> arrayListBar = MainActivity.creatingBars();

        BarAdapter adapter = new BarAdapter(this, arrayListBar);
        listBar.setAdapter(adapter);
    }
}
