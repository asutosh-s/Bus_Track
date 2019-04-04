package com.example.android.uber;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends AppCompatActivity {

    //variables
    private Button mUser, mDriver;
    private GoogleMap mMap;
    LocationManager locationManager;
    Color color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//        }

        mDriver = (Button) findViewById(R.id.driverB);
        mUser = (Button) findViewById(R.id.userB);
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDriver.setBackgroundColor(Color.rgb(128,128,128));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 500ms
                        mDriver.setBackgroundColor(Color.rgb(192,192,192));
                    }
                }, 500);
                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
            }
        });
        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.setBackgroundColor(Color.rgb(128,128,128));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 500ms
                        mUser.setBackgroundColor(Color.rgb(192,192,192));
                    }
                }, 500);
                Intent intent = new Intent(MainActivity.this, CustomerMapActivity.class);
                startActivity(intent);
            }
        });

    }  //On create ends here

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_COARSE_LOCATION: {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
//                }
//            break;
//            }
//        }
//    }
}
