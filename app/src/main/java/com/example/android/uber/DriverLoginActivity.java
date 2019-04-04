package com.example.android.uber;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.sax2.Driver;

public class DriverLoginActivity extends AppCompatActivity {

    //variable
    private EditText mEmail, mPassword;
    private Button mLogin, mReg;
    Tag TAG;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth = FirebaseAuth.getInstance();
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null )
                {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mReg = (Button) findViewById(R.id.registration);

        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(DriverLoginActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(mAuth.getCurrentUser()!=null){
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("DriverNotActive").child(userId);
                                currentUser.setValue(true);
                                Intent intent = new Intent(DriverLoginActivity.this,RegDoneActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(email.matches(""))
                {
                    Toast.makeText(DriverLoginActivity.this, "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.matches(""))
                {
                    Toast.makeText(DriverLoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(DriverLoginActivity.this, "Sign in error", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(mAuth.getCurrentUser()!=null){
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("DriverActive").child(userId);
                                currentUser.setValue(true);
                                DatabaseReference User = FirebaseDatabase.getInstance().getReference().child("Users").child("DriverNotActive").child(userId);
                                User.removeValue();
                                Intent intent = new Intent(DriverLoginActivity.this,DriverMapActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {
        Intent callingIntent = new Intent(DriverLoginActivity.this,MainActivity.class);
        callingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(callingIntent);
        super.onBackPressed();
    }
}
