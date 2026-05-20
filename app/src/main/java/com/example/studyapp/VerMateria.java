package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.materia;

public class VerMateria extends BaseActivity {

    EditText etMateria, etProfesor, etSalon, etHoraInicio, etHoraFin;
    CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;
    RadioButton rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris;
    TextView btnEliminar, btnEditar, btnCancelar;
    appDatabase db;
    int idMateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_materia);
        db = AppDataBaseInstancia.getInstance(this);

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

        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);
        btnCancelar = findViewById(R.id.btnCancelarIcono);

        Intent intent = getIntent();
        idMateria = intent.getIntExtra("id", -1);
        if (intent.getStringExtra("nombre") == null) {
            cargarMateriaDesdeDB();
        }
        etMateria.setText(intent.getStringExtra("nombre"));
        etProfesor.setText(intent.getStringExtra("profesor"));
        etSalon.setText(intent.getStringExtra("salon"));
        etHoraInicio.setText(intent.getStringExtra("horaInicio"));
        etHoraFin.setText(intent.getStringExtra("horaFin"));
        String dias = intent.getStringExtra("dias");
        if (dias != null) {
            cbLunes.setChecked(dias.contains("Lunes"));
            cbMartes.setChecked(dias.contains("Martes"));
            cbMiercoles.setChecked(dias.contains("Miercoles"));
            cbJueves.setChecked(dias.contains("Jueves"));
            cbViernes.setChecked(dias.contains("Viernes"));
            cbSabado.setChecked(dias.contains("Sabado"));
            cbDomingo.setChecked(dias.contains("Domingo"));
        }

        bloquearCampos();
        btnEliminar.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar materia")
                    .setMessage("¿Estás seguro de que deseas eliminar esta materia?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        new Thread(() -> {
                            AlarmHelper.cancelarAviso(VerMateria.this, idMateria, "CLASE");
                            materia m = new materia();
                            m.id = idMateria;
                            db.appDao().eliminarMateria(m);
                            runOnUiThread(() -> {
                                Intent i = new Intent(VerMateria.this, Horario.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            });
                        }).start();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(VerMateria.this, EditarMateria.class);
            i.putExtras(getIntent());
            startActivity(i);
        });

        btnCancelar.setOnClickListener(v -> finish());
    }

    private void bloquearCampos() {
        etMateria.setEnabled(false);
        etProfesor.setEnabled(false);
        etSalon.setEnabled(false);
        etHoraInicio.setEnabled(false);
        etHoraFin.setEnabled(false);
        cbLunes.setEnabled(false); cbMartes.setEnabled(false); cbMiercoles.setEnabled(false);
        cbJueves.setEnabled(false); cbViernes.setEnabled(false); cbSabado.setEnabled(false); cbDomingo.setEnabled(false);
    }

    private void cargarMateriaDesdeDB() {
        new Thread(() -> {
            materia m = db.appDao().obtenerMateriaPorId(idMateria);
            if (m != null) {
                runOnUiThread(() -> {
                    etMateria.setText(m.nombre);
                    etProfesor.setText(m.profesor);
                    etSalon.setText(m.salon);
                    etHoraInicio.setText(m.horaInicio);
                    etHoraFin.setText(m.horaFin);
                });
            }
        }).start();
    }
}
