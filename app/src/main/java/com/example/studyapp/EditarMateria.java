package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    AutoCompleteTextView etMateria;
    EditText etProfesor, etSalon, etHoraInicio, etHoraFin;
    RadioButton rbLunes, rbMartes, rbMiercoles, rbJueves, rbViernes, rbSabado, rbDomingo;
    RadioButton rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris;
    Button btnGuardar, btnCancelar;
    int idMateria;
    String colorSeleccionado = "#2196F3";

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

        rbLunes = findViewById(R.id.rbLunes);
        rbMartes = findViewById(R.id.rbMartes);
        rbMiercoles = findViewById(R.id.rbMiercoles);
        rbJueves = findViewById(R.id.rbJueves);
        rbViernes = findViewById(R.id.rbViernes);
        rbSabado = findViewById(R.id.rbSabado);
        rbDomingo = findViewById(R.id.rbDomingo);

        // Configurar exclusividad de días
        RadioButton[] rbDias = {rbLunes, rbMartes, rbMiercoles, rbJueves, rbViernes, rbSabado, rbDomingo};
        String[] nombresDias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        
        for (RadioButton rb : rbDias) {
            rb.setOnClickListener(v -> {
                for (RadioButton other : rbDias) {
                    if (other != rb) other.setChecked(false);
                }
            });
        }

        // Mapeo de RadioButtons de colores
        rbRojo = findViewById(R.id.rbRojo); rbNaranja = findViewById(R.id.rbNaranja);
        rbAmarillo = findViewById(R.id.rbAmarillo); rbVerde = findViewById(R.id.rbVerde);
        rbAzul = findViewById(R.id.rbAzul); rbMorado = findViewById(R.id.rbMorado);
        rbCeleste = findViewById(R.id.rbCeleste); rbCafe = findViewById(R.id.rbCafe);
        rbRosa = findViewById(R.id.rbRosa); rbGris = findViewById(R.id.rbGris);

        btnGuardar = findViewById(R.id.btnDialogGuardar);
        btnCancelar = findViewById(R.id.btnDialogCancelar);

        // Recibir datos para editar
        Intent intent = getIntent();
        idMateria = intent.getIntExtra("id", -1);
        etMateria.setText(intent.getStringExtra("nombre"));
        etProfesor.setText(intent.getStringExtra("profesor"));
        etSalon.setText(intent.getStringExtra("salon"));
        etHoraInicio.setText(intent.getStringExtra("horaInicio"));
        etHoraFin.setText(intent.getStringExtra("horaFin"));
        
        String dias = intent.getStringExtra("dias");
        if (dias != null) {
            for (int i = 0; i < nombresDias.length; i++) {
                if (dias.contains(nombresDias[i])) {
                    rbDias[i].setChecked(true);
                    break; // Solo uno
                }
            }
        }

        colorSeleccionado = intent.getStringExtra("color");
        configurarRadiosColores();

        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));
        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etMateria.getText().toString();
            if (nombre.isEmpty()) { etMateria.setError("Obligatorio"); return; }

            String diaElegido = "";
            for (int i = 0; i < rbDias.length; i++) {
                if (rbDias[i].isChecked()) {
                    diaElegido = nombresDias[i];
                    break;
                }
            }

            if (diaElegido.isEmpty()) {
                rbLunes.setError("Elige un día");
                return;
            }

            materia m = new materia();
            m.id = idMateria;
            m.nombre = nombre;
            m.profesor = etProfesor.getText().toString();
            m.salon = etSalon.getText().toString();
            m.horaInicio = etHoraInicio.getText().toString();
            m.horaFin = etHoraFin.getText().toString();
            m.dias = diaElegido;
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

    private void configurarRadiosColores() {
        RadioButton[] radios = {rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris};
        String[] colores = {"#F44336","#FF9800","#FFC107","#4CAF50","#2196F3","#9C27B0","#00BCD4","#795548","#E91E63","#607D8B"};

        for (int i = 0; i < radios.length; i++) {
            int index = i;
            radios[i].setOnClickListener(v -> {
                for (RadioButton rb : radios) rb.setChecked(false);
                radios[index].setChecked(true);
                colorSeleccionado = colores[index];
            });
            if (colores[i].equalsIgnoreCase(colorSeleccionado)) radios[i].setChecked(true);
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