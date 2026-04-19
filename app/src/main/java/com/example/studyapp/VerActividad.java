package com.example.studyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import androidx.appcompat.app.AlertDialog;

public class VerActividad extends AppCompatActivity {

    AutoCompleteTextView tipoAct, materiaAct, estadoAct;
    EditText fecha, hora, descripcion;
    TextView btnEliminar, btnEditar, btnCancelar;
    appDatabase db;
    int idActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_tareas);

        db = appDatabaseInstancia.getInstance(this);
        tipoAct = findViewById(R.id.RegistroActividad);
        materiaAct = findViewById(R.id.RegistroMateria);
        estadoAct = findViewById(R.id.EstadoActividad);
        fecha = findViewById(R.id.DiaEntrega);
        hora = findViewById(R.id.HoraInicio);
        descripcion = findViewById(R.id.TextDescripcion);

        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);
        btnCancelar = findViewById(R.id.btnCancelarIcono);

        Intent intent = getIntent();
        idActividad = intent.getIntExtra("id", -1);
        tipoAct.setText(intent.getStringExtra("tipo"));
        estadoAct.setText(intent.getStringExtra("estado"));
        fecha.setText(intent.getStringExtra("fecha"));
        hora.setText(intent.getStringExtra("hora"));
        String desc = intent.getStringExtra("descripcion");
        descripcion.setText(desc != null ? desc : "");
        materiaAct.setText(intent.getStringExtra("materiaNombre"));
        
        tipoAct.setFocusable(false);
        materiaAct.setFocusable(false);
        estadoAct.setFocusable(false);
        fecha.setFocusable(false);
        hora.setFocusable(false);
        descripcion.setFocusable(false);

        tipoAct.setClickable(false);
        materiaAct.setClickable(false);
        estadoAct.setClickable(false);

        btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Estás segur@ de eliminar esta actividad?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> {
                        AlarmHelper.cancelarAviso(VerActividad.this, idActividad, "ACTIVIDAD");
                        db.appDao().eliminarActividadPorId(idActividad);
                        runOnUiThread(() -> {
                            Intent inte = new Intent(VerActividad.this, Tarea.class);
                            inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(inte);
                            finish();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });


        btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(VerActividad.this, EditarActividad.class);
            i.putExtra("id", idActividad);
            i.putExtra("tipo", tipoAct.getText().toString());
            i.putExtra("estado", estadoAct.getText().toString());
            i.putExtra("fecha", fecha.getText().toString());
            i.putExtra("hora", hora.getText().toString());
            i.putExtra("descripcion", descripcion.getText().toString());
            i.putExtra("materiaNombre", materiaAct.getText().toString());
            startActivity(i);
        });

        btnCancelar.setOnClickListener(v -> finish());
    }
}