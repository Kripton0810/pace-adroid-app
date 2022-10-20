package com.kripton.paceclasses;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    Button login,logout;
    FirebaseUser user;
    FrameLayout frame;
    EditText idtoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInClient = new Config().config(this);

        frame = findViewById(R.id.frame);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        user = auth.getCurrentUser();
        if (user!=null)
        {
            Intent intent = new Intent(MainActivity.this,MainDashedboard.class);
            startActivity(intent);
            finish();
        }
        setFragement(new Login());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Config().Signout(googleSignInClient,MainActivity.this);
    }

    private void setFragement(Fragment fragement)
    {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frame.getId(), fragement).commit();
    }
}