package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.materia;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.dao.appDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class inicio extends AppCompatActivity {

    RecyclerView recyclerTareas, recyclerClases;
    appDatabase db;
    appDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        // ================= BOTONES =================
        Button btnCalendario = findViewById(R.id.btnCalendario);
        Button btnTareas = findViewById(R.id.btnTareas);
        Button btnKanba = findViewById(R.id.btnKamba);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnCalendario.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Horario.class)));

        btnTareas.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Tarea.class)));

        btnKanba.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Kanba.class)));

        imgStudy.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, MainActivity.class)));

        // ================= RECYCLERS =================
        recyclerTareas = findViewById(R.id.recyclerTareas);
        recyclerClases = findViewById(R.id.recyclerClases);

        recyclerTareas.setLayoutManager(new LinearLayoutManager(this));
        recyclerClases.setLayoutManager(new LinearLayoutManager(this));

        db = appDatabaseInstancia.getInstance(this);
        dao = db.appDao();

        cargarDatos();
    }
    private void cargarDatos() {
        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            String[] diasNombres = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
            String hoy = diasNombres[calendar.get(Calendar.DAY_OF_WEEK) - 1];

            List<materia> todas = dao.obtenerMaterias();
            List<materia> hoyMaterias = new ArrayList<>();
            for (materia m : todas) {
                if (m.dias != null && m.dias.contains(hoy)) {
                    hoyMaterias.add(m);
                }
            }
            List<actividad> actividades = dao.obtenerActividades();
            Collections.sort(actividades, (a1, a2) -> {
                try {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date f1 = formato.parse(a1.fechaEntrega);
                    Date f2 = formato.parse(a2.fechaEntrega);
                    return f1.compareTo(f2);
                } catch (Exception e) {
                    return 0;
                }
            });


            List<actividad> topActividades = (actividades.size() > 5)
                    ? new ArrayList<>(actividades.subList(0, 5))
                    : actividades;


            runOnUiThread(() -> {
                recyclerClases.setAdapter(new InicioMateria(hoyMaterias));
                recyclerTareas.setAdapter(new InicioAdapter(topActividades));
            });
        }).start();
    }
}

