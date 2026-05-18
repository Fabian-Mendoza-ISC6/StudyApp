package com.example.studyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        // 1.0f es el tamaño normal. 0.85f pequeño, 1.2f grande.
        float fontScale = prefs.getFloat("fontScale", 1.0f);
        
        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = fontScale;
        
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
