package com.example.studyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        
        // 1. Aplicar Idioma
        String lang = prefs.getString("language", "es");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.setLocale(locale);
        
        // 2. Aplicar Tamaño de letra (Escalado global)
        float fontScale = prefs.getFloat("fontScale", 1.0f);
        config.fontScale = fontScale;
        
        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // Aplica el color de fondo y contraste al cargar la pantalla
        applyDynamicStyles();
    }

    private void applyDynamicStyles() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String colorHex = prefs.getString("backgroundColor", "#89CAB4");
        String contrastHex = getContrastColor(colorHex);

        // 1. Aplicar color de fondo al contenedor principal
        ViewGroup root = findViewById(android.R.id.content);
        if (root != null && root.getChildCount() > 0) {
            applyColorToView(root.getChildAt(0), colorHex);
        }

        // 2. Aplicar color de contraste a toolbars específicos
        int[] toolbarIds = {R.id.toolbarIcons, R.id.toolDeTarea, R.id.toolSemanda};
        for (int id : toolbarIds) {
            View toolbar = findViewById(id);
            if (toolbar != null) {
                applyColorToView(toolbar, contrastHex);
            }
        }
    }

    // Determina el color de contraste basado en el fondo seleccionado
    private String getContrastColor(String backgroundHex) {
        switch (backgroundHex.toUpperCase()) {
            case "#7AC2BB": return "#3E8F87"; // Verde agua
            case "#A7D8F0": return "#4A9FD6"; // Azul cielo
            case "#FFD6A5": return "#FF9F50"; // Durazno
            case "#F7C8E0": return "#F46A9B"; // Rosa suave
            case "#FFF3B0": return "#FFD93D"; // Amarillo pastel
            case "#D9C8F0": return "#9A6FD0"; // Lila claro
            case "#E8E8E8": return "#BFBFBF"; // Gris perla
            default: return "#3E8F87";        // Por defecto verde agua profundo
        }
    }

    // Método para aplicar color respetando el diseño (bordes, etc)
    public void applyColorToView(View view, String colorHex) {
        if (view == null) return;
        int color = Color.parseColor(colorHex);

        if (view.getBackground() instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) view.getBackground();
            drawable.setColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    // Sobrecarga para usar el color de fondo guardado por defecto (útil para Diálogos)
    public void applyColorToView(View view) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String colorHex = prefs.getString("backgroundColor", "#89CAB4");
        applyColorToView(view, colorHex);
    }
}
