package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Tarea extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tareas);

        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnCalendario = findViewById(R.id.btnCalendario);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, inicio.class);
                startActivity(intent);
            }
        });

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, Horario.class);
                startActivity(intent);
            }
        });

        imgStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}