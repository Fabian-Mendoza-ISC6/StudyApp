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
import java.util.Date;
import java.util.List;
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
        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnCalendario = findViewById(R.id.btnCalendario);
        Button btnTareas = findViewById(R.id.btnTareas);
        Button btnKanba = findViewById(R.id.btnKamba);
        Button btnEventos = findViewById(R.id.btnEventos);
        ImageView imgStudy = findViewById(R.id.img_study);

        btnInicio.setOnClickListener(v -> cargarDatos()); // Ya estamos en inicio, solo refresca
        
        btnCalendario.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Horario.class)));

        btnTareas.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Tarea.class)));

        btnKanba.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Kanba.class)));

        btnEventos.setOnClickListener(v ->
                startActivity(new Intent(inicio.this, Calendario.class)));

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

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            String[] diasNombres = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
            String hoy = diasNombres[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            
            String fechaHoy = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(calendar.getTime());

            // 1. Filtrar Materias (Próximos 30 minutos)
            List<materia> todasMaterias = dao.obtenerMaterias();
            List<materia> proximasMaterias = new ArrayList<>();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            long ahoraMillis = calendar.getTimeInMillis();

            for (materia m : todasMaterias) {
                if (m.dias != null && m.dias.contains(hoy)) {
                    try {
                        Date horaClaseDate = timeFormat.parse(m.horaInicio.replace("p. m.", "PM").replace("a. m.", "AM"));
                        if (horaClaseDate != null) {
                            Calendar calClase = (Calendar) calendar.clone();
                            Calendar temp = Calendar.getInstance();
                            temp.setTime(horaClaseDate);
                            
                            calClase.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
                            calClase.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
                            calClase.set(Calendar.SECOND, 0);

                            long diferenciaMillis = calClase.getTimeInMillis() - ahoraMillis;
                            long ventanaTiempo = 35 * 60 * 1000; 

                            if (diferenciaMillis > 0 && diferenciaMillis <= ventanaTiempo) {
                                proximasMaterias.add(m);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            List<actividad> todasActividades = dao.obtenerActividades();
            List<actividad> tareasHoy = new ArrayList<>();
            for (actividad a : todasActividades) {
                if (a.fechaEntrega != null && a.fechaEntrega.equals(fechaHoy)) {
                    tareasHoy.add(a);
                }
            }

            runOnUiThread(() -> {
                recyclerClases.setAdapter(new InicioMateria(proximasMaterias));
                recyclerTareas.setAdapter(new InicioAdapter(tareasHoy));
            });
        }).start();
    }
}
