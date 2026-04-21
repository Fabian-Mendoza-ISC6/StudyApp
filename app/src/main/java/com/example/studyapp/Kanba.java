package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.dao.appDao;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.util.ArrayList;
import java.util.List;

public class Kanba extends AppCompatActivity {

    RecyclerView rPendiente, rProceso, rTerminado;
    ItemKanba adPendiente, adProceso, adTerminado;
    appDatabase db;
    appDao dao;

    List<actividad> pendientes = new ArrayList<>(), proceso = new ArrayList<>(), terminado = new ArrayList<>();
    List<materia> todasMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kanba);

        db = appDatabaseInstancia.getInstance(this);
        dao = db.appDao();

        // ================= TOOLBAR BUTTONS =================
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(this, inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> startActivity(new Intent(this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v -> cargarKanban()); // Refresh current
        findViewById(R.id.btnEventos).setOnClickListener(v -> startActivity(new Intent(this, Calendario.class)));
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        rPendiente = findViewById(R.id.recyclerPendiente);
        rProceso = findViewById(R.id.recyclerProceso);
        rTerminado = findViewById(R.id.recyclerTerminado);

        rPendiente.setLayoutManager(new LinearLayoutManager(this));
        rProceso.setLayoutManager(new LinearLayoutManager(this));
        rTerminado.setLayoutManager(new LinearLayoutManager(this));

        configurarDrop(findViewById(R.id.contededordeTarea), "Pendiente");
        configurarDrop(findViewById(R.id.contededordeClases), "En curso");
        configurarDrop(findViewById(R.id.contededorHecho), "Finalizado");

        cargarKanban();
    }

    private void cargarKanban() {
        new Thread(() -> {
            pendientes = dao.obtenerPorEstado("Pendiente");
            proceso = dao.obtenerPorEstado("En curso");
            terminado = dao.obtenerPorEstado("Finalizado");
            todasMaterias = dao.obtenerMaterias();

            runOnUiThread(() -> {
                if (adPendiente == null) {
                    adPendiente = new ItemKanba(pendientes, todasMaterias);
                    adProceso = new ItemKanba(proceso, todasMaterias);
                    adTerminado = new ItemKanba(terminado, todasMaterias);
                    rPendiente.setAdapter(adPendiente);
                    rProceso.setAdapter(adProceso);
                    rTerminado.setAdapter(adTerminado);
                } else {
                    adPendiente.setDatos(pendientes, todasMaterias);
                    adProceso.setDatos(proceso, todasMaterias);
                    adTerminado.setDatos(terminado, todasMaterias);
                }
            });
        }).start();
    }

    private void configurarDrop(FrameLayout contenedor, String nuevoEstado) {
        contenedor.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    break;
                case DragEvent.ACTION_DROP:
                    actividad act = (actividad) event.getLocalState();
                    act.estado = nuevoEstado;
                    new Thread(() -> {
                        dao.actualizarActividad(act);
                        runOnUiThread(this::cargarKanban);
                    }).start();
                    break;
            }
            return true;
        });
    }
}