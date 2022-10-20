package com.kripton.paceclasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SystemClock.sleep(1500);
        getSupportActionBar().hide();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null)
        {
            Intent intent = new Intent(SplashScreen.this,MainDashedboard.class);
            startActivity(intent);
            Animatoo.animateFade(SplashScreen.this);
        }
        else
        {
            SharedPreferences preferences = getSharedPreferences("Profile",MODE_PRIVATE);
            if (preferences.getBoolean("skip",false))
            {
                Intent intent = new Intent(SplashScreen.this,MainDashedboard.class);
                startActivity(intent);
                Animatoo.animateFade(SplashScreen.this);
            }
            else
            {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                Animatoo.animateFade(SplashScreen.this);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}