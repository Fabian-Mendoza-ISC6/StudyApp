package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.materia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditarMateria extends BaseActivity {

    appDatabase db;
    AutoCompleteTextView etMateria;
    EditText etProfesor, etSalon, etHoraInicio, etHoraFin;
    RadioButton rbLunes, rbMartes, rbMiercoles, rbJueves, rbViernes, rbSabado, rbDomingo;
    RadioButton rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris;
    Button btnGuardar, btnCancelar;
    int idMateria;
    String colorSeleccionado = "#2196F3";
    List<materia> todasLasMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agregar_horario);

        db = AppDataBaseInstancia.getInstance(this);
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

        RadioButton[] rbDias = {rbLunes, rbMartes, rbMiercoles, rbJueves, rbViernes, rbSabado, rbDomingo};
        String[] nombresDias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        
        for (RadioButton rb : rbDias) {
            rb.setOnClickListener(v -> {
                for (RadioButton other : rbDias) {
                    if (other != rb) other.setChecked(false);
                }
            });
        }

        rbRojo = findViewById(R.id.rbRojo); rbNaranja = findViewById(R.id.rbNaranja);
        rbAmarillo = findViewById(R.id.rbAmarillo); rbVerde = findViewById(R.id.rbVerde);
        rbAzul = findViewById(R.id.rbAzul); rbMorado = findViewById(R.id.rbMorado);
        rbCeleste = findViewById(R.id.rbCeleste); rbCafe = findViewById(R.id.rbCafe);
        rbRosa = findViewById(R.id.rbRosa); rbGris = findViewById(R.id.rbGris);

        btnGuardar = findViewById(R.id.btnDialogGuardar);
        btnCancelar = findViewById(R.id.btnDialogCancelar);

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
                    break;
                }
            }
        }

        colorSeleccionado = intent.getStringExtra("color");
        configurarRadiosColores();
        cargarTodasLasMaterias();

        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));
        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etMateria.getText().toString().trim();
            String profesor = etProfesor.getText().toString().trim();
            String salon = etSalon.getText().toString().trim();
            String hInicio = etHoraInicio.getText().toString().trim();
            String hFin = etHoraFin.getText().toString().trim();

            nombre = capitalizar(nombre);
            profesor = capitalizar(profesor);

            String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ. ]+$";

            if (nombre.isEmpty()) { etMateria.setError("La materia es obligatoria"); return; }
            if (!nombre.matches(regex)) { etMateria.setError("Solo se permiten letras y puntos"); return; }
            
            if (profesor.isEmpty()) { etProfesor.setError("El profesor es obligatorio"); return; }
            if (!profesor.matches(regex)) { etProfesor.setError("Solo se permiten letras y puntos"); return; }
            
            if (hInicio.isEmpty()) { Toast.makeText(this, "Debe seleccionar la hora de inicio", Toast.LENGTH_SHORT).show(); return; }
            if (hFin.isEmpty()) { Toast.makeText(this, "Debe seleccionar la hora de fin", Toast.LENGTH_SHORT).show(); return; }

            int minInicio = tiempoEnMinutos(hInicio);
            int minFin = tiempoEnMinutos(hFin);
            if (minFin <= minInicio) {
                Toast.makeText(this, "La hora de salida no puede ser antes o igual a la de entrada", Toast.LENGTH_SHORT).show();
                return;
            }

            String diaElegido = "";
            for (int i = 0; i < rbDias.length; i++) {
                if (rbDias[i].isChecked()) {
                    diaElegido = nombresDias[i];
                    break;
                }
            }

            if (diaElegido.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar un día", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hayConflicto(diaElegido, hInicio, hFin, idMateria)) {
                Toast.makeText(this, "Ya tienes una clase registrada en este horario y día", Toast.LENGTH_LONG).show();
                return;
            }

            materia m = new materia();
            m.id = idMateria;
            m.nombre = nombre;
            m.profesor = profesor;
            m.salon = salon;
            m.horaInicio = hInicio;
            m.horaFin = hFin;
            m.dias = diaElegido;
            m.color = colorSeleccionado;

            new Thread(() -> {
                AlarmHelper.cancelarAviso(EditarMateria.this, idMateria, "CLASE");
                db.appDao().actualizarMateria(m);

                String detallesMateria = "Prof: " + m.profesor + " | Aula: " + m.salon;
                AlarmHelper.programarAviso(
                        EditarMateria.this,
                        m.id,
                        "CLASE",
                        m.dias,
                        m.horaInicio,
                        m.nombre,
                        detallesMateria
                );

                runOnUiThread(() -> {
                    Toast.makeText(EditarMateria.this, "Materia actualizada", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditarMateria.this, Horario.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                });
            }).start();
        });
    }

    private void cargarTodasLasMaterias() {
        new Thread(() -> {
            todasLasMaterias = db.appDao().obtenerMaterias();
        }).start();
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

    private boolean hayConflicto(String dia, String hInicio, String hFin, int idExcluir) {
        int nuevoInicio = tiempoEnMinutos(hInicio);
        int nuevoFin = tiempoEnMinutos(hFin);
        if (nuevoInicio == -1 || nuevoFin == -1) return false;

        for (materia m : todasLasMaterias) {
            if (m.id == idExcluir) continue;
            if (m.dias != null && m.dias.contains(dia)) {
                int mInicio = tiempoEnMinutos(m.horaInicio);
                int mFin = tiempoEnMinutos(m.horaFin);
                if (mInicio == -1 || mFin == -1) continue;
                if (nuevoInicio < mFin && nuevoFin > mInicio) return true;
            }
        }
        return false;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
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
