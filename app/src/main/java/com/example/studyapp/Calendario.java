package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Calendario extends AppCompatActivity {

    private TextView txtMesActual;
    private RecyclerView rvCalendario, rvTareasDia;
    private List<String> listaDias = new ArrayList<>();
    private List<actividad> todasActividades = new ArrayList<>();
    private List<materia> todasMaterias = new ArrayList<>();
    private appDatabase db;
    private Calendar calNavegacion = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendario);

        db = appDatabaseInstancia.getInstance(this);

        // --- BARRA SUPERIOR ---
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(this, inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> startActivity(new Intent(this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(this, Kanba.class)));
        findViewById(R.id.btnEventos).setOnClickListener(v -> actualizarCalendario()); // Refresh current
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        // --- CONTROLES DE MES ---
        txtMesActual = findViewById(R.id.txtMesActual);
        rvCalendario = findViewById(R.id.rvCalendario);
        rvTareasDia = findViewById(R.id.rvTareasDia);
        rvTareasDia.setLayoutManager(new LinearLayoutManager(this));
        
        Button btnAnterior = findViewById(R.id.btnMesAnterior);
        Button btnSiguiente = findViewById(R.id.btnMesSiguiente);

        btnAnterior.setOnClickListener(v -> {
            calNavegacion.add(Calendar.MONTH, -1);
            actualizarCalendario();
        });

        btnSiguiente.setOnClickListener(v -> {
            calNavegacion.add(Calendar.MONTH, 1);
            actualizarCalendario();
        });

        actualizarCalendario();
    }

    private void actualizarCalendario() {
        new Thread(() -> {
            todasActividades = db.appDao().obtenerActividades();
            todasMaterias = db.appDao().obtenerMaterias();
            
            runOnUiThread(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
                String nombreMes = sdf.format(calNavegacion.getTime());
                txtMesActual.setText(nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));

                listaDias.clear();
                Calendar tempCal = (Calendar) calNavegacion.clone();
                tempCal.set(Calendar.DAY_OF_MONTH, 1);
                
                int primerDiaSemana = tempCal.get(Calendar.DAY_OF_WEEK) - 1; 
                int diasEnMes = calNavegacion.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int i = 0; i < primerDiaSemana; i++) listaDias.add("");
                for (int i = 1; i <= diasEnMes; i++) listaDias.add(String.valueOf(i));

                rvCalendario.setLayoutManager(new GridLayoutManager(this, 7));
                
                rvCalendario.setAdapter(new CalendarioAdapter(listaDias, todasActividades, calNavegacion, fechaSeleccionada -> {
                    List<actividad> filtradas = new ArrayList<>();
                    for (actividad act : todasActividades) {
                        if (act.fechaEntrega != null && act.fechaEntrega.equals(fechaSeleccionada)) {
                            filtradas.add(act);
                        }
                    }
                    rvTareasDia.setAdapter(new ActividadAdapter(filtradas, todasMaterias, (act, nom) -> {}));
                }));
            });
        }).start();
    }
}