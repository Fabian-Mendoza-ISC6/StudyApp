package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.materia;

public class VerMateria extends AppCompatActivity {

    EditText etMateria, etProfesor, etSalon, etHoraInicio, etHoraFin;
    CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;
    RadioButton rbRojo, rbNaranja, rbAmarillo, rbVerde, rbAzul, rbMorado, rbCeleste, rbCafe, rbRosa, rbGris;
    ImageView btnEliminar, btnEditar, btnCancelar;
    appDatabase db;
    int idMateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_materia);
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

        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);
        btnCancelar = findViewById(R.id.btnCancelarIcono);

        //Recibimos los datos
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
        if (color != null) {
            switch (color) {
                case "#F44336": rbRojo.setChecked(true); break;
                case "#FF9800": rbNaranja.setChecked(true); break;
                case "#FFC107": rbAmarillo.setChecked(true); break;
                case "#4CAF50": rbVerde.setChecked(true); break;
                case "#2196F3": rbAzul.setChecked(true); break;
                case "#9C27B0": rbMorado.setChecked(true); break;
                case "#00BCD4": rbCeleste.setChecked(true); break;
                case "#795548": rbCafe.setChecked(true); break;
                case "#E91E63": rbRosa.setChecked(true); break;
                case "#607D8B": rbGris.setChecked(true); break;
            }
        }


        bloquearCampos();
        btnEliminar.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar materia")
                    .setMessage("¿Estás seguro de que deseas eliminar esta materia?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        new Thread(() -> {
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

            i.putExtras(intent);

            startActivity(i);
        });

        btnCancelar.setOnClickListener(v -> {
            Intent i = new Intent(VerMateria.this, Horario.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void bloquearCampos() {
        etMateria.setEnabled(false);
        etProfesor.setEnabled(false);
        etSalon.setEnabled(false);
        etHoraInicio.setEnabled(false);
        etHoraFin.setEnabled(false);

        cbLunes.setEnabled(false);
        cbMartes.setEnabled(false);
        cbMiercoles.setEnabled(false);
        cbJueves.setEnabled(false);
        cbViernes.setEnabled(false);
        cbSabado.setEnabled(false);
        cbDomingo.setEnabled(false);

        rbRojo.setEnabled(false);
        rbNaranja.setEnabled(false);
        rbAmarillo.setEnabled(false);
        rbVerde.setEnabled(false);
        rbAzul.setEnabled(false);
        rbMorado.setEnabled(false);
        rbCeleste.setEnabled(false);
        rbCafe.setEnabled(false);
        rbRosa.setEnabled(false);
        rbGris.setEnabled(false);
    }
}