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

    private GestureDetectorCompat gestureObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        gestureObject = new GestureDetectorCompat(this, new BarListActivity.LearnGesture());
        final ImageView goToMap = findViewById(R.id.goToMap);

        goToMap.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){

                    Intent goToMap = new Intent(BarListActivity.this, MapsActivity.class);
                    startActivity(goToMap);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //now create the gesture Object Class

    class LearnGesture extends GestureDetector.SimpleOnGestureListener{
        //SimpleOnGestureListener is the listener for the gestures we want

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            if(event2.getX()>event1.getX() && (Math.abs(event2.getY()-event1.getY())<150)){

                Intent intent = new Intent(BarListActivity.this, MapsActivity.class);
                startActivity(intent);
                //swipe gauche à droite

            }
            else if(event2.getX()<event1.getX()){
                //swipe droite à gauche
            }
            return true;
        }
    }
}
