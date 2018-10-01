package fr.wildcodeschool.cafeconcert;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ListActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        gestureObject = new GestureDetectorCompat(this, new ListActivity.LearnGesture());
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

                Intent intent = new Intent(ListActivity.this, MapsActivity.class);
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
