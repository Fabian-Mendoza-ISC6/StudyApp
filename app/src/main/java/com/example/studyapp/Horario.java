package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Horario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario);

        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnTareas = findViewById(R.id.btnTareas);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Horario.this, inicio.class);
                startActivity(intent);
            }
        });

        btnTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Horario.this, Tarea.class);
                startActivity(intent);
            }
        });

        imgStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Horario.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}