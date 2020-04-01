package com.chakam.praktikum6_splashscreen_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.chakam.praktikum6_splashscreen_login.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setelah Spalsh Screen Selesai langsung ke tampilan Login
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
            }, 3000L); //Waktu Splash Screen
    }
}

