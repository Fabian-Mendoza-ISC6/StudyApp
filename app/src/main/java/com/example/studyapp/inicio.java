package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class inicio extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        Button btnCalendario = findViewById(R.id.btnCalendario);
        Button btnTareas = findViewById(R.id.btnTareas);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inicio.this, Horario.class);
                startActivity(intent);
            }
        });

        btnTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inicio.this, Tarea.class);
                startActivity(intent);
            }
        });

        imgStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inicio.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}