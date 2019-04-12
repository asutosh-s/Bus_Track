package com.example.android.uber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FetchbusActivity extends AppCompatActivity {

    Button mSearchB,mSearchAllB;
    EditText mBusno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetchbus);

        mSearchAllB = findViewById(R.id.SearchAllB);
        mSearchB = findViewById(R.id.SearchB);
        mBusno = findViewById(R.id.BusnoE);

        mSearchAllB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchbusActivity.this,CustomerMapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSearchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FetchbusActivity.this, "Invalid bus ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
