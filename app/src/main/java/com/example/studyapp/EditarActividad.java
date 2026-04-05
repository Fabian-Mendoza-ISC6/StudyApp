package com.example.studyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
public class EditarActividad extends AppCompatActivity {

    appDatabase db;
    AutoCompleteTextView tipoAct, materiaAct, estadoAct;
    EditText etFecha, etHora, etDesc;
    Button btnGuardar, btnCancelar;
    List<materia> listaMaterias = new ArrayList<>();
    int idActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_tareas); // 🔥 reutilizamos XML

        db = appDatabaseInstancia.getInstance(this);

        tipoAct = findViewById(R.id.RegistroActividad);
        materiaAct = findViewById(R.id.RegistroMateria);
        estadoAct = findViewById(R.id.EstadoActividad);
        etFecha = findViewById(R.id.DiaEntrega);
        etHora = findViewById(R.id.HoraInicio);
        etDesc = findViewById(R.id.TextDescripcion);

        btnGuardar = findViewById(R.id.btnDialogGuardar);
        btnCancelar = findViewById(R.id.btnDialogCancelar);

        //Recibimos los datos
        idActividad = getIntent().getIntExtra("id", -1);
        tipoAct.setText(getIntent().getStringExtra("tipo"));
        estadoAct.setText(getIntent().getStringExtra("estado"));
        etFecha.setText(getIntent().getStringExtra("fecha"));
        etHora.setText(getIntent().getStringExtra("hora"));
        String desc = getIntent().getStringExtra("descripcion");
        etDesc.setText(desc != null ? desc : "");
        String materiaNombre = getIntent().getStringExtra("materiaNombre");
        materiaAct.setText(materiaNombre != null ? materiaNombre : "");

        //Se cargan las materias
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

        tipoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Tarea", "Examen", "Proyecto", "Estudio"}));
        estadoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Pendiente", "En curso", "Finalizado"}));
        tipoAct.setOnClickListener(v -> tipoAct.showDropDown());
        materiaAct.setOnClickListener(v -> materiaAct.showDropDown());
        estadoAct.setOnClickListener(v -> estadoAct.showDropDown());

        //Fecha
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (v1, y, m, d) ->
                    etFecha.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        //Hora
        etHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (v1, h, min) -> {
                String amPm = (h >= 12) ? "PM" : "AM";
                int hour = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, min, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> {
            String t = tipoAct.getText().toString();
            String estado = estadoAct.getText().toString();
            String fecha = etFecha.getText().toString();
            String hora = etHora.getText().toString();
            String descTxt = etDesc.getText().toString();
            String matNombre = materiaAct.getText().toString();

            if (t.isEmpty()) {
                tipoAct.setError("Obligatorio");
                return;
            }
            if (matNombre.isEmpty()) {
                materiaAct.setError("Obligatorio");
                return;
            }
            int idMat = -1;
            for (materia m : listaMaterias) {
                if (m.nombre.equals(matNombre)) {
                    idMat = m.id;
                    break;
                }
            }
            if (idMat == -1) {
                materiaAct.setError("Materia no válida");
                return;
            }
            actividad act = new actividad();
            act.id = idActividad;
            act.tipo = t;
            act.estado = estado;
            act.fechaEntrega = fecha;
            act.horaInicio = hora;
            act.descripcion = descTxt;
            act.idMateria = idMat;

            new Thread(() -> {
                db.appDao().actualizarActividad(act);
                runOnUiThread(() -> {
                    Intent intent = new Intent(EditarActividad.this, Tarea.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }).start();
        });
    }
}