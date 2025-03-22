package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.database.DataBase;
import com.example.myapplication.registration.Registration;

public class SplashActivity extends AppCompatActivity {
    DataBase db;
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "myProfile";
    public static final String DATA_SAVE = "DATA_SAVE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        boolean data_save = sPref.getBoolean(DATA_SAVE, false);

        db = new DataBase(this);
        db.create_db();

        //CHECK ACCESS FOR MAP
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION} , 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        //CHECK DATA
        if(data_save) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            Intent intent2 = new Intent(this, Registration.class);
            startActivity(intent2);
            finish();
        }
    }
}