package com.example.studyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class Configuraciones extends BaseActivity {

    private String colorSeleccionadoHex = "#89CAB4"; // Color por defecto
    private Map<Integer, String> mapaColores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuraciones);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        float currentScale = prefs.getFloat("fontScale", 1.0f);
        String currentLang = prefs.getString("language", "es");
        String currentColor = prefs.getString("backgroundColor", "#89CAB4");

        findViewById(R.id.btnInicio).setOnClickListener(v ->
                startActivity(new Intent(this, Inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v ->
                startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v ->
                startActivity(new Intent(this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v ->
                startActivity(new Intent(this, Kanba.class)));
        findViewById(R.id.btnEventos).setOnClickListener(v ->
                startActivity(new Intent(this, Calendario.class)));
        findViewById(R.id.img_study).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        // Tamaño de letra
        RadioGroup rgFontSize = findViewById(R.id.rgFontSize);
        if (currentScale == 0.85f) rgFontSize.check(R.id.rbSmall);
        else if (currentScale == 1.2f) rgFontSize.check(R.id.rbLarge);
        else rgFontSize.check(R.id.rbMedium);

        // Idioma - USANDO spinner_item PARA QUE SE VEA NEGRO
        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        String[] displayLangs = {"Español (Spanish)", "English (Inglés)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, displayLangs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        if (currentLang.equals("en")) spinnerLanguage.setSelection(1);
        else spinnerLanguage.setSelection(0);

        // Colores de fondo
        inicializarMapaColores();

        for (Map.Entry<Integer, String> entry : mapaColores.entrySet()) {
            RadioButton rb = findViewById(entry.getKey());
            if (entry.getValue().equalsIgnoreCase(currentColor)) {
                rb.setChecked(true);
                colorSeleccionadoHex = currentColor;
            }
            
            rb.setOnClickListener(v -> {
                desmarcarOtrosColores(entry.getKey());
                colorSeleccionadoHex = entry.getValue();
            });
        }

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            // Guardar tamaño de letra
            float newScale = 1.0f;
            int checkedFontId = rgFontSize.getCheckedRadioButtonId();
            if (checkedFontId == R.id.rbSmall) newScale = 0.85f;
            else if (checkedFontId == R.id.rbLarge) newScale = 1.2f;
            editor.putFloat("fontScale", newScale);

            String selectedLang = (spinnerLanguage.getSelectedItemPosition() == 1) ? "en" : "es";
            editor.putString("language", selectedLang);

            editor.putString("backgroundColor", colorSeleccionadoHex);
            
            editor.apply();

            Toast.makeText(this, R.string.msg_saved_restarting, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void inicializarMapaColores() {
        mapaColores.put(R.id.rbBgVerdeAgua, "#7AC2BB");
        mapaColores.put(R.id.rbBgAzulCielo, "#A7D8F0");
        mapaColores.put(R.id.rbBgDurazno, "#FFD6A5");
        mapaColores.put(R.id.rbBgRosa, "#F7C8E0");
        mapaColores.put(R.id.rbBgAmarillo, "#FFF3B0");
        mapaColores.put(R.id.rbBgLila, "#D9C8F0");
        mapaColores.put(R.id.rbBgGris, "#E8E8E8");
    }

    private void desmarcarOtrosColores(int idSeleccionado) {
        for (Integer id : mapaColores.keySet()) {
            if (id != idSeleccionado) {
                ((RadioButton) findViewById(id)).setChecked(false);
            }
        }
    }
}
