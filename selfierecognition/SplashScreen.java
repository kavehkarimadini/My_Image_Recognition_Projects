package com.example.selfierecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MediaPlayer music = MediaPlayer.create(this,R.raw.music);
        music.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                music.stop();
                finish();
            }
        }, 3000);
    }
}