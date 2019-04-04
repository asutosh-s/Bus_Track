package com.example.android.uber;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.w3c.dom.DocumentFragment;

public class BaseActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 50;
    private GestureDetector gestureDetector;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        view = (ImageView) findViewById(R.id.swipe);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", -100f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.start();

        if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        gestureDetector = new GestureDetector(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float vx, float vy) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        //check which one is greater in X or in Y
        if(Math.abs(diffX) > Math.abs(diffY)){
            //right or left swipe
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(vx) > SWIPE_VELOCITY_THRESHOLD){
                if(diffX>0){
                    onSwipwRight();
                }
                else{
                    onSwipeLeft();
                }
                result = true;
            }
        }
        else{
            //up or down swipe
            if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(vy) > SWIPE_VELOCITY_THRESHOLD){
                if(diffY>0){
                    onSwipeButtom();
                }
                else{
                    onSwipeUp();
                }
                result = true;
            }
        }
        return result;
    }

    private void onSwipeButtom() {
//        Toast.makeText(this, "Swipe buttom", Toast.LENGTH_SHORT).show();

    }

    private void onSwipeUp() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
//        Toast.makeText(this, "Swipe Up", Toast.LENGTH_SHORT).show();
    }

    private void onSwipeLeft() {
//        Toast.makeText(this, "Swipe left", Toast.LENGTH_SHORT).show();
    }

    private void onSwipwRight() {
//        Toast.makeText(this, "Swipe right", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
