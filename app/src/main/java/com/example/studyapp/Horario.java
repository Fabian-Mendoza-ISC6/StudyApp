package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.materia;

public class Horario extends AppCompatActivity {
    appDatabase db;
    RecyclerView recyclerView;
    MateriaAdapter adapter;
    List<materia> listaMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario);
        db = appDatabaseInstancia.getInstance(this);

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MateriaAdapter(listaMaterias, m -> {
            Intent intent = new Intent(Horario.this, VerMateria.class);

            intent.putExtra("id", m.id);
            intent.putExtra("nombre", m.nombre);
            intent.putExtra("profesor", m.profesor);
            intent.putExtra("salon", m.salon);
            intent.putExtra("horaInicio", m.horaInicio);
            intent.putExtra("horaFin", m.horaFin);
            intent.putExtra("dias", m.dias);
            intent.putExtra("color", m.color);

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Cargar materias guardadas
        cargarMaterias();

        // Botones de navegación
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(Horario.this, inicio.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> startActivity(new Intent(Horario.this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(Horario.this, Kanba.class)));
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(Horario.this, MainActivity.class)));
        
        Button btnAgregar = findViewById(R.id.btnAgrgar);
        btnAgregar.setOnClickListener(v -> mostrarDialogo());
    }

    private void cargarMaterias() {
        new Thread(() -> {
            List<materia> materiasDB = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                listaMaterias = materiasDB;
                adapter.setMaterias(listaMaterias);
            });
        }).start();
    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_agregar_horario, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Referencias a los campos del diálogo
        EditText etMateria = view.findViewById(R.id.etMateria);
        EditText etProfesor = view.findViewById(R.id.etProfesor);
        EditText etSalon = view.findViewById(R.id.etSalon);
        EditText etHoraInicio = view.findViewById(R.id.etHoraInicio);
        EditText etHoraFin = view.findViewById(R.id.etHoraFin);

        // Configurar selección de reloj
        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));

        // IDs de los CheckBoxes para los días
        int[] checkBoxesIds = {R.id.cbLunes, R.id.cbMartes, R.id.cbMiercoles, R.id.cbJueves, R.id.cbViernes, R.id.cbSabado, R.id.cbDomingo};

        // Configuración de RadioButtons para Colores
        int[] coloresIds = {R.id.rbRojo, R.id.rbNaranja, R.id.rbAmarillo, R.id.rbVerde, R.id.rbAzul, R.id.rbMorado, R.id.rbCeleste, R.id.rbCafe, R.id.rbRosa, R.id.rbGris};
        String[] coloresHex = {"#F44336", "#FF9800", "#FFC107", "#4CAF50", "#2196F3", "#9C27B0", "#00BCD4", "#795548", "#E91E63", "#607D8B"};
        
        for (int i = 0; i < coloresIds.length; i++) {
            int index = i;
            RadioButton rb = view.findViewById(coloresIds[i]);
            rb.setOnClickListener(v -> {
                for (int otherId : coloresIds) {
                    if (otherId != coloresIds[index]) ((RadioButton) view.findViewById(otherId)).setChecked(false);
                }
                view.setTag(coloresHex[index]);
            });
        }

        // BOTÓN GUARDAR
        view.findViewById(R.id.btnDialogGuardar).setOnClickListener(v -> {
            String nombre = etMateria.getText().toString();
            String profesor = etProfesor.getText().toString();
            String salon = etSalon.getText().toString();
            String hInicio = etHoraInicio.getText().toString();
            String hFin = etHoraFin.getText().toString();

            if (nombre.isEmpty()) {
                etMateria.setError("Obligatorio");
                return;
            }

            // Obtener días seleccionados (Múltiple)
            StringBuilder diasSeleccionados = new StringBuilder();
            for (int id : checkBoxesIds) {
                CheckBox cb = view.findViewById(id);
                if (cb != null && cb.isChecked()) {
                    if (diasSeleccionados.length() > 0) diasSeleccionados.append(", ");
                    diasSeleccionados.append(cb.getText().toString());
                }
            }

            // Obtener color (del tag o azul por defecto)
            String colorSeleccionado = (view.getTag() != null) ? view.getTag().toString() : "#2196F3";

            // Crear y guardar materia
            materia nuevaMateria = new materia();
            nuevaMateria.nombre = nombre;
            nuevaMateria.profesor = profesor;
            nuevaMateria.salon = salon;
            nuevaMateria.horaInicio = hInicio;
            nuevaMateria.horaFin = hFin;
            nuevaMateria.dias = diasSeleccionados.toString();
            nuevaMateria.color = colorSeleccionado;

            new Thread(() -> {
                db.appDao().insertarMateria(nuevaMateria);
                runOnUiThread(() -> {
                    cargarMaterias();
                    dialog.dismiss();
                });
            }).start();
        });

        view.findViewById(R.id.btnDialogCancelar).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarReloj(EditText editText) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String amPm = (hourOfDay >= 12) ? "PM" : "AM";
            int hour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
            editText.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }
}
