package com.example.studyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Tarea extends AppCompatActivity {
    appDatabase db;
    RecyclerView recyclerActividades;
    ActividadAdapter adapter;
    List<actividad> listaActividades = new ArrayList<>();
    List<materia> listaMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tareas);
        
        db = appDatabaseInstancia.getInstance(this);

        // ================= TOOLBAR BUTTONS =================
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(this, inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> cargarActividades()); // Refresh current
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(this, Kanba.class)));
        findViewById(R.id.btnEventos).setOnClickListener(v -> startActivity(new Intent(this, Calendario.class)));
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        // Inicializar RecyclerView
        recyclerActividades = findViewById(R.id.recyclerActividades);
        recyclerActividades.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActividadAdapter(listaActividades, listaMaterias,(act, nombreMateria) -> {
            Intent intent = new Intent(Tarea.this, VerActividad.class);
            intent.putExtra("id", act.id);
            intent.putExtra("tipo", act.tipo);
            intent.putExtra("estado", act.estado);
            intent.putExtra("fecha", act.fechaEntrega);
            intent.putExtra("hora", act.horaInicio);
            intent.putExtra("descripcion", act.descripcion);
            intent.putExtra("idMateria", act.idMateria);
            intent.putExtra("materiaNombre", nombreMateria);
            startActivity(intent);
        });
        recyclerActividades.setAdapter(adapter);

        cargarActividades();
        cargarMaterias();

        findViewById(R.id.btnAgrgar).setOnClickListener(v -> mostrarDialogoAgregarTarea());
    }

    private void cargarActividades() {
        new Thread(() -> {
            List<actividad> lista = db.appDao().obtenerActividades();
            runOnUiThread(() -> {
                listaActividades = lista;
                adapter.setActividades(listaActividades);
            });
        }).start();
    }
    private void cargarMaterias() {
        new Thread(() -> {
            List<materia> lista = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                listaMaterias = lista;
                adapter.setMaterias(listaMaterias);
            });
        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
    }

    private void mostrarDialogoAgregarTarea() {
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.agregar_tareas, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        AutoCompleteTextView tipoAct = view.findViewById(R.id.RegistroActividad);
        AutoCompleteTextView materiaAct = view.findViewById(R.id.RegistroMateria);
        AutoCompleteTextView estadoAct = view.findViewById(R.id.EstadoActividad);
        EditText etFecha = view.findViewById(R.id.DiaEntrega);
        EditText etHora = view.findViewById(R.id.HoraInicio);
        EditText etDesc = view.findViewById(R.id.TextDescripcion);

        new Thread(() -> {
            listaMaterias = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                List<String> nombres = new ArrayList<>();
                for (materia m : listaMaterias) {
                    nombres.add(m.nombre);
                }
                materiaAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres));
            });
        }).start();

        tipoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"Tarea", "Examen", "Proyecto", "Estudio"}));
        estadoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"Pendiente", "En curso", "Finalizado"}));

        tipoAct.setOnClickListener(v -> tipoAct.showDropDown());
        materiaAct.setOnClickListener(v -> materiaAct.showDropDown());
        estadoAct.setOnClickListener(v -> estadoAct.showDropDown());

        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this, (v1, y, m, d) -> etFecha.setText(d + "/" + (m + 1) + "/" + y), 
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        });

        etHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (v1, h, min) -> {
                String amPm = (h >= 12) ? "PM" : "AM";
                int hour = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, min, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        view.findViewById(R.id.btnDialogCancelar).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDialogGuardar).setOnClickListener(v -> {
            String t = tipoAct.getText().toString();
            String mNombre = materiaAct.getText().toString();
            if (t.isEmpty()) { tipoAct.setError("Obligatorio"); return; }
            if (mNombre.isEmpty()) { materiaAct.setError("Obligatorio"); return; }

            int idMat = -1;
            for (materia mat : listaMaterias) {
                if (mat.nombre.equals(mNombre)) { idMat = mat.id; break; }
            }
            if (idMat == -1) { materiaAct.setError("Materia no válida"); return; }

            actividad n = new actividad();
            n.tipo = t;
            n.estado = estadoAct.getText().toString();
            n.fechaEntrega = etFecha.getText().toString();
            n.horaInicio = etHora.getText().toString();
            n.descripcion = etDesc.getText().toString();
            n.idMateria = idMat;

            new Thread(() -> {
                long idGenerado = db.appDao().insertarActividad(n);
                AlarmHelper.programarAviso(Tarea.this, (int)idGenerado, "ACTIVIDAD", n.fechaEntrega, n.horaInicio, n.tipo);
                runOnUiThread(() -> { cargarActividades(); dialog.dismiss(); });
            }).start();
        });
        dialog.show();
    }
}