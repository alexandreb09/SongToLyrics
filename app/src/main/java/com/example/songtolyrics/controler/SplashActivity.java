package com.example.songtolyrics.controler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.songtolyrics.R;

public class SplashActivity extends AppCompatActivity {
    int SPLASH_DISPLAY_LENGTH = 2500;                                                               /* Durée de la page d'accueil */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::passerActiviteSuivante, SPLASH_DISPLAY_LENGTH);
    }
    void passerActiviteSuivante(){
        Intent nextActivity = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(nextActivity);
        finish();
    }
}





