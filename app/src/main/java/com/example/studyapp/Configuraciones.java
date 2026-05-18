package com.example.studyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Configuraciones extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuraciones);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        float currentScale = prefs.getFloat("fontScale", 1.0f);
        String currentLang = prefs.getString("language", "es");

        // ================= BARRA DE NAVEGACIÓN =================
        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnCalendario = findViewById(R.id.btnCalendario);
        Button btnTareas = findViewById(R.id.btnTareas);
        Button btnKanba = findViewById(R.id.btnKamba);
        Button btnEventos = findViewById(R.id.btnEventos);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnInicio.setOnClickListener(v -> startActivity(new Intent(this, inicio.class)));
        btnCalendario.setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        btnTareas.setOnClickListener(v -> startActivity(new Intent(this, Tarea.class)));
        btnKanba.setOnClickListener(v -> startActivity(new Intent(this, Kanba.class)));
        btnEventos.setOnClickListener(v -> startActivity(new Intent(this, Calendario.class)));
        imgStudy.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        // ================= CONFIGURACIONES LOGIC =================
        
        // Font Size
        RadioGroup rgFontSize = findViewById(R.id.rgFontSize);
        if (currentScale == 0.85f) rgFontSize.check(R.id.rbSmall);
        else if (currentScale == 1.2f) rgFontSize.check(R.id.rbLarge);
        else rgFontSize.check(R.id.rbMedium);

        // Language Spinner - Solo Español e Inglés
        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        String[] languages = {"Español", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Seleccionar el idioma actual en el spinner
        if (currentLang.equals("en")) {
            spinnerLanguage.setSelection(1);
        } else {
            spinnerLanguage.setSelection(0);
        }

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            // Guardar Tamaño de letra
            float newScale = 1.0f;
            int checkedId = rgFontSize.getCheckedRadioButtonId();
            if (checkedId == R.id.rbSmall) newScale = 0.85f;
            else if (checkedId == R.id.rbLarge) newScale = 1.2f;
            editor.putFloat("fontScale", newScale);

            // Guardar Idioma
            String selectedLang = spinnerLanguage.getSelectedItemPosition() == 1 ? "en" : "es";
            editor.putString("language", selectedLang);
            
            editor.apply();

            Toast.makeText(this, "Cambios guardados. Reiniciando...", Toast.LENGTH_SHORT).show();
            
            // Reiniciar la app para aplicar idioma y fuente globalmente
            Intent intent = new Intent(this, inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
