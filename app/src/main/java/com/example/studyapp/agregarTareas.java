package com.example.studyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;



public class agregarTareas extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_tareas);

        AutoCompleteTextView actividad = findViewById(R.id.RegistroActividad);
        AutoCompleteTextView materia = findViewById(R.id.RegistroMateria);

        String[] actividades = {"Tarea", "Examen", "Proyecto", "Estudio"};
        String[] materias = {"Matemáticas", "Programación", "Física", "Base de Datos"};

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                actividades
        );

        ArrayAdapter<String> adapterMateria = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                materias
        );

        actividad.setAdapter(adapterActividad);
        materia.setAdapter(adapterMateria);
        actividad.setOnClickListener(v -> actividad.showDropDown());
        materia.setOnClickListener(v -> materia.showDropDown());

        EditText etFecha = findViewById(R.id.DiaEntrega);

        etFecha.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();

            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etFecha.setText(fecha);
                    },
                    año, mes, dia
            );

            datePickerDialog.show();
        });
    }
    private void mostrarReloj(EditText editText) {
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String amPm = (hourOfDay >= 12) ? "PM" : "AM";
            int hour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);

            String horaFormateada = String.format(
                    Locale.getDefault(),
                    "%02d:%02d %s",
                    hour, minute, amPm
            );

            editText.setText(horaFormateada);

        }, c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false).show();
    }

}
