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

import java.util.Calendar;
import java.util.Locale;


public class Tarea extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tareas);

        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnCalendario = findViewById(R.id.btnCalendario);
        Button btnKanba = findViewById(R.id.btnKamba);
        ImageView imgStudy = findViewById(R.id.img_study);
        Button btnAgregar = findViewById(R.id.btnAgrgar);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, inicio.class);
                startActivity(intent);
            }
        });

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, Horario.class);
                startActivity(intent);
            }
        });

        imgStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnKanba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tarea.this, Kanba.class);
                startActivity(intent);
            }
        });
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarTarea();
            }
        });
    }

    private void mostrarDialogoAgregarTarea() {
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.agregar_tareas, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //
        AutoCompleteTextView actividad = view.findViewById(R.id.RegistroActividad);
        AutoCompleteTextView materia = view.findViewById(R.id.RegistroMateria);
        AutoCompleteTextView estado = view.findViewById(R.id.EstadoActividad);

        String[] actividades = {"Tarea", "Examen", "Proyecto", "Estudio"};
        String[] materias = {"Matemáticas", "Programación", "Física", "Base de Datos"};
        String[] estados = {"Pendiente", "En curso", "Finalizado"};

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, actividades);
        ArrayAdapter<String> adapterMateria = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, materias);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);

        actividad.setAdapter(adapterActividad);
        materia.setAdapter(adapterMateria);
        estado.setAdapter(adapterEstado);

        actividad.setOnClickListener(v -> actividad.showDropDown());
        materia.setOnClickListener(v -> materia.showDropDown());
        estado.setOnClickListener(v -> estado.showDropDown());

        //
        EditText etFecha = view.findViewById(R.id.DiaEntrega);
        EditText etHora = view.findViewById(R.id.HoraInicio);

        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                etFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                int hour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        view.findViewById(R.id.btnDialogCancelar).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDialogGuardar).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
