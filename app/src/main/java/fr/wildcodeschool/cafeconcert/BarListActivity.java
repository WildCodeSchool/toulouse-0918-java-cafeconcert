package fr.wildcodeschool.cafeconcert;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class BarListActivity extends AppCompatActivity {

    private GestureDetectorCompat mGestureObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        mGestureObject = new GestureDetectorCompat(this, new BarListActivity.LearnGesture());

        //Setting button to go to MapsActivity
        final ImageView goToMap = findViewById(R.id.goToMap);
        MapsActivity.transitionBetweenActivity(goToMap, BarListActivity.this, MapsActivity.class);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mGestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //now create the gesture Object Class

    //swipe pour aller sur l'activité map
    class LearnGesture extends GestureDetector.SimpleOnGestureListener{
        //SimpleOnGestureListener is the listener for the gestures we want

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            if(event2.getX() > event1.getX() && (Math.abs(event2.getY()-event1.getY()) < 150)){

                Intent intent = new Intent(BarListActivity.this, MapsActivity.class);
                startActivity(intent);
                //swipe gauche à droite

            }
            else if(event2.getX() < event1.getX()){
                //swipe droite à gauche
            }
            return true;
        }
    }
}
