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
import android.widget.Toast;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;

public class EditarActividad extends BaseActivity {

    appDatabase db;
    AutoCompleteTextView tipoAct, materiaAct, estadoAct;
    EditText etFecha, etHora, etDesc;
    Button btnGuardar, btnCancelar;
    List<materia> listaMaterias = new ArrayList<>();
    int idActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_tareas);

        db = appDatabaseInstancia.getInstance(this);

        tipoAct = findViewById(R.id.RegistroActividad);
        materiaAct = findViewById(R.id.RegistroMateria);
        estadoAct = findViewById(R.id.EstadoActividad);
        etFecha = findViewById(R.id.DiaEntrega);
        etHora = findViewById(R.id.HoraInicio);
        etDesc = findViewById(R.id.TextDescripcion);

        btnGuardar = findViewById(R.id.btnDialogGuardar);
        btnCancelar = findViewById(R.id.btnDialogCancelar);

        idActividad = getIntent().getIntExtra("id", -1);
        tipoAct.setText(getIntent().getStringExtra("tipo"));
        estadoAct.setText(getIntent().getStringExtra("estado"));
        etFecha.setText(getIntent().getStringExtra("fecha"));
        etHora.setText(getIntent().getStringExtra("hora"));
        String desc = getIntent().getStringExtra("descripcion");
        etDesc.setText(desc != null ? desc : "");
        String materiaNombre = getIntent().getStringExtra("materiaNombre");
        materiaAct.setText(materiaNombre != null ? materiaNombre : "");

        new Thread(() -> {
            listaMaterias = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                List<String> nombresUnicos = new ArrayList<>();
                for (materia m : listaMaterias) {
                    if (!nombresUnicos.contains(m.nombre)) {
                        nombresUnicos.add(m.nombre);
                    }
                }
                materiaAct.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, nombresUnicos));
            });
        }).start();

        tipoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Tarea", "Examen", "Proyecto", "Estudio"}));
        estadoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Pendiente", "En curso", "Finalizado"}));
        
        tipoAct.setOnClickListener(v -> tipoAct.showDropDown());
        materiaAct.setOnClickListener(v -> materiaAct.showDropDown());
        estadoAct.setOnClickListener(v -> estadoAct.showDropDown());

        // Configurar selector de fecha con bloqueo de días pasados
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this, (v1, y, m, d) ->
                    etFecha.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            
            // Bloquear días anteriores a hoy
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

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> {
            String t = tipoAct.getText().toString().trim();
            String matNombre = materiaAct.getText().toString().trim();
            String estado = estadoAct.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();
            String descTxt = etDesc.getText().toString().trim();

            if (t.isEmpty()) { tipoAct.setError("Obligatorio"); return; }
            if (matNombre.isEmpty()) { materiaAct.setError("Obligatorio"); return; }
            if (estado.isEmpty()) { estadoAct.setError("Obligatorio"); return; }
            if (fecha.isEmpty()) { etFecha.setError("Obligatorio"); return; }
            if (hora.isEmpty()) { etHora.setError("Obligatorio"); return; }
            if (descTxt.isEmpty()) { etDesc.setError("Obligatorio"); return; }

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

            if (hayConflictoConClase(fecha, hora)) {
                Toast.makeText(this, "No puedes registrar una tarea durante el horario de una clase", Toast.LENGTH_LONG).show();
                return;
            }

            actividad act = new actividad();
            act.id = idActividad;
            act.tipo = capitalizar(t);
            act.estado = estado;
            act.fechaEntrega = fecha;
            act.horaInicio = hora;
            act.descripcion = descTxt;
            act.idMateria = idMat;

            new Thread(() -> {
                //1. ACTUALIZAR en la base de datos
                db.appDao().actualizarActividad(act);

                // 2. CANCELAR la alarma anterior
                AlarmHelper.cancelarAviso(EditarActividad.this, act.id, "ACTIVIDAD");

                // 3. REPROGRAMAR la nueva alarma
                AlarmHelper.programarAviso(
                        EditarActividad.this,
                        act.id,
                        "ACTIVIDAD",
                        act.fechaEntrega,
                        act.horaInicio,
                        act.tipo,
                        act.descripcion // Se envía la descripción como detalle
                );

                runOnUiThread(() -> {
                    Toast.makeText(EditarActividad.this, "Actividad actualizada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditarActividad.this, Tarea.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }).start();
        });
    }

    private boolean hayConflictoConClase(String fechaStr, String horaStr) {
        String diaSemana = obtenerDiaSemana(fechaStr);
        int horaTarea = tiempoEnMinutos(horaStr);
        if (diaSemana.isEmpty() || horaTarea == -1) return false;

        for (materia m : listaMaterias) {
            if (m.dias != null && m.dias.contains(diaSemana)) {
                int mInicio = tiempoEnMinutos(m.horaInicio);
                int mFin = tiempoEnMinutos(m.horaFin);
                if (mInicio != -1 && mFin != -1) {
                    if (horaTarea >= mInicio && horaTarea < mFin) return true;
                }
            }
        }
        return false;
    }

    private String obtenerDiaSemana(String fechaStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            Date date = format.parse(fechaStr);
            String dia = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
            dia = dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();
            if (dia.equals("Miércoles")) dia = "Miercoles";
            if (dia.equals("Sábado")) dia = "Sabado";
            return dia;
        } catch (Exception e) {
            return "";
        }
    }

    private int tiempoEnMinutos(String horaStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
            String h = horaStr.replace("a. m.", "AM").replace("p. m.", "PM").replace("a.m.", "AM").replace("p.m.", "PM");
            Date date = format.parse(h);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        } catch (Exception e) {
            return -1;
        }
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
