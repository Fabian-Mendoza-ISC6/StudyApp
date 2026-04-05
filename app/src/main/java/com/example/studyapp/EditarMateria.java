package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.materia;

import java.util.Calendar;
import java.util.Locale;

public class EditarMateria extends AppCompatActivity {

    appDatabase db;
    EditText etMateria, etProfesor, etSalon, etHoraInicio, etHoraFin;
    CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;
    RadioButton rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris;
    Button btnGuardar, btnCancelar;
    int idMateria;
    String colorSeleccionado = "#2196F3"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agregar_horario);

        db = appDatabaseInstancia.getInstance(this);
        etMateria = findViewById(R.id.etMateria);
        etProfesor = findViewById(R.id.etProfesor);
        etSalon = findViewById(R.id.etSalon);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);

        cbLunes = findViewById(R.id.cbLunes);
        cbMartes = findViewById(R.id.cbMartes);
        cbMiercoles = findViewById(R.id.cbMiercoles);
        cbJueves = findViewById(R.id.cbJueves);
        cbViernes = findViewById(R.id.cbViernes);
        cbSabado = findViewById(R.id.cbSabado);
        cbDomingo = findViewById(R.id.cbDomingo);

        rbRojo = findViewById(R.id.rbRojo);
        rbNaranja = findViewById(R.id.rbNaranja);
        rbAmarillo = findViewById(R.id.rbAmarillo);
        rbVerde = findViewById(R.id.rbVerde);
        rbAzul = findViewById(R.id.rbAzul);
        rbMorado = findViewById(R.id.rbMorado);
        rbCeleste = findViewById(R.id.rbCeleste);
        rbCafe = findViewById(R.id.rbCafe);
        rbRosa = findViewById(R.id.rbRosa);
        rbGris = findViewById(R.id.rbGris);

        btnGuardar = findViewById(R.id.btnDialogGuardar);
        btnCancelar = findViewById(R.id.btnDialogCancelar);

        // Recibimos los datos
        Intent intent = getIntent();
        idMateria = intent.getIntExtra("id", -1);
        etMateria.setText(intent.getStringExtra("nombre"));
        etProfesor.setText(intent.getStringExtra("profesor"));
        etSalon.setText(intent.getStringExtra("salon"));
        etHoraInicio.setText(intent.getStringExtra("horaInicio"));
        etHoraFin.setText(intent.getStringExtra("horaFin"));
        String dias = intent.getStringExtra("dias");
        if (dias != null) {
            cbLunes.setChecked(dias.contains("Lunes"));
            cbMartes.setChecked(dias.contains("Martes"));
            cbMiercoles.setChecked(dias.contains("Miérc"));
            cbJueves.setChecked(dias.contains("Jueves"));
            cbViernes.setChecked(dias.contains("Viernes"));
            cbSabado.setChecked(dias.contains("Sábado"));
            cbDomingo.setChecked(dias.contains("Dom"));
        }


        String color = intent.getStringExtra("color");
        if (color != null) colorSeleccionado = color;
        configurarRadios();

        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));


        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {

            String nombre = etMateria.getText().toString();
            if (nombre.isEmpty()) {
                etMateria.setError("Obligatorio");
                return;
            }
            String profesor = etProfesor.getText().toString();
            String salon = etSalon.getText().toString();
            String hInicio = etHoraInicio.getText().toString();
            String hFin = etHoraFin.getText().toString();


            StringBuilder diasSeleccionados = new StringBuilder();
            if (cbLunes.isChecked()) diasSeleccionados.append("Lunes, ");
            if (cbMartes.isChecked()) diasSeleccionados.append("Martes, ");
            if (cbMiercoles.isChecked()) diasSeleccionados.append("Miérc., ");
            if (cbJueves.isChecked()) diasSeleccionados.append("Jueves, ");
            if (cbViernes.isChecked()) diasSeleccionados.append("Viernes, ");
            if (cbSabado.isChecked()) diasSeleccionados.append("Sábado, ");
            if (cbDomingo.isChecked()) diasSeleccionados.append("Dom., ");

            if (diasSeleccionados.length() > 0)
                diasSeleccionados.delete(diasSeleccionados.length() - 2, diasSeleccionados.length());

            materia m = new materia();
            m.id = idMateria;
            m.nombre = nombre;
            m.profesor = profesor;
            m.salon = salon;
            m.horaInicio = hInicio;
            m.horaFin = hFin;
            m.dias = diasSeleccionados.toString();
            m.color = colorSeleccionado;

            new Thread(() -> {
                db.appDao().actualizarMateria(m);
                runOnUiThread(() -> {
                    Intent i = new Intent(EditarMateria.this, Horario.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                });
            }).start();
        });
    }

    private void configurarRadios() {
        RadioButton[] radios = {rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris};
        String[] colores = {"#F44336","#FF9800","#FFC107","#4CAF50","#2196F3","#9C27B0","#00BCD4","#795548","#E91E63","#607D8B"};

        for (int i = 0; i < radios.length; i++) {
            int index = i;
            radios[i].setOnClickListener(v -> {
                for (RadioButton rb : radios) rb.setChecked(false);
                radios[index].setChecked(true);
                colorSeleccionado = colores[index];
            });

            if (colores[i].equals(colorSeleccionado)) {
                radios[i].setChecked(true);
            }
        }
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