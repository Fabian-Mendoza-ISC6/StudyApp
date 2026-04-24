package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Calendario extends AppCompatActivity {

    private RecyclerView rvCalendario, rvTareasDia;
    private List<actividad> todasActividades = new ArrayList<>();
    private List<materia> todasMaterias = new ArrayList<>();
    private appDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendario);

        db = appDatabaseInstancia.getInstance(this);

        // --- NAVEGACIÓN ---
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(this, inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> startActivity(new Intent(this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(this, Kanba.class)));
        findViewById(R.id.btnEventos).setOnClickListener(v -> actualizarCalendario()); 
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        // --- RECYCLERS ---
        rvCalendario = findViewById(R.id.rvCalendario);
        rvTareasDia = findViewById(R.id.rvTareasDia);
        rvTareasDia.setLayoutManager(new LinearLayoutManager(this));
        
        // Ocultamos los botones de navegación de mes individual (ya que ahora es una lista)
        View layoutMes = findViewById(R.id.layoutMes);
        if (layoutMes != null) layoutMes.setVisibility(View.GONE);

        actualizarCalendario();
    }

    private void actualizarCalendario() {
        new Thread(() -> {
            todasActividades = db.appDao().obtenerActividades();
            todasMaterias = db.appDao().obtenerMaterias();
            
            // 1. Identificar todos los meses que tienen tareas
            Set<String> mesesKeys = new HashSet<>();
            
            // Siempre incluimos el mes actual
            Calendar hoy = Calendar.getInstance();
            mesesKeys.add(hoy.get(Calendar.MONTH) + "-" + hoy.get(Calendar.YEAR));

            // Buscamos en todas las tareas
            for (actividad act : todasActividades) {
                if (act.fechaEntrega != null && act.fechaEntrega.contains("/")) {
                    try {
                        String[] partes = act.fechaEntrega.split("/");
                        if (partes.length == 3) {
                            int m = Integer.parseInt(partes[1]) - 1; // Mes (0-11)
                            int a = Integer.parseInt(partes[2]);     // Año
                            mesesKeys.add(m + "-" + a);
                        }
                    } catch (Exception ignored) {}
                }
            }

            // 2. Convertir las claves a objetos Calendar para ordenarlos
            List<Calendar> listaMeses = new ArrayList<>();
            for (String key : mesesKeys) {
                String[] p = key.split("-");
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, Integer.parseInt(p[1]));
                c.set(Calendar.MONTH, Integer.parseInt(p[0]));
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                listaMeses.add(c);
            }

            // 3. Si quieres ver al menos unos meses próximos aunque no tengan tareas
            // (Opcional, pero ayuda a que no se vea vacío si solo hay tareas hoy)
            if (listaMeses.size() < 3) {
                Calendar extra = (Calendar) hoy.clone();
                for (int i = 1; i <= 3; i++) {
                    extra.add(Calendar.MONTH, 1);
                    String key = extra.get(Calendar.MONTH) + "-" + extra.get(Calendar.YEAR);
                    if (!mesesKeys.contains(key)) {
                        Calendar c = (Calendar) extra.clone();
                        c.set(Calendar.DAY_OF_MONTH, 1);
                        listaMeses.add(c);
                    }
                }
            }

            Collections.sort(listaMeses);

            runOnUiThread(() -> {
                rvCalendario.setLayoutManager(new LinearLayoutManager(this));
                
                MesAdapter adapter = new MesAdapter(listaMeses, todasActividades, fechaSeleccionada -> {
                    // Mostrar las tareas del día que el usuario toque en cualquier calendario
                    List<actividad> filtradas = new ArrayList<>();
                    for (actividad act : todasActividades) {
                        if (act.fechaEntrega != null && act.fechaEntrega.equals(fechaSeleccionada)) {
                            filtradas.add(act);
                        }
                    }
                    rvTareasDia.setAdapter(new ActividadAdapter(filtradas, todasMaterias, (act, nom) -> {}));
                });
                
                rvCalendario.setAdapter(adapter);
            });
        }).start();
    }
}
