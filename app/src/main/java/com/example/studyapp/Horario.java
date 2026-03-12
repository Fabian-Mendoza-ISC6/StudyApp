package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class Horario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario);

        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnTareas = findViewById(R.id.btnTareas);
        ImageView imgStudy = findViewById(R.id.img_study);
        Button btnAgregar = findViewById(R.id.btnAgrgar);

        btnInicio.setOnClickListener(v -> startActivity(new Intent(Horario.this, inicio.class)));
        btnTareas.setOnClickListener(v -> startActivity(new Intent(Horario.this, Tarea.class)));
        imgStudy.setOnClickListener(v -> startActivity(new Intent(Horario.this, MainActivity.class)));
        btnAgregar.setOnClickListener(v -> mostrarDialogo());
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

        // Configurar selección única de DÍAS
        int[] diasIds = {R.id.rbLunes, R.id.rbMartes, R.id.rbMiercoles, R.id.rbJueves, R.id.rbViernes, R.id.rbSabado, R.id.rbDomingo};
        for (int id : diasIds) {
            RadioButton rb = view.findViewById(id);
            rb.setOnClickListener(v -> {
                for (int otherId : diasIds) {
                    if (otherId != id) ((RadioButton) view.findViewById(otherId)).setChecked(false);
                }
            });
        }

        // Configurar selección única de COLORES
        int[] coloresIds = {R.id.rbRojo, R.id.rbNaranja, R.id.rbAmarillo, R.id.rbVerde, R.id.rbAzul, R.id.rbMorado, R.id.rbCeleste, R.id.rbCafe, R.id.rbRosa, R.id.rbGris};
        for (int id : coloresIds) {
            RadioButton rb = view.findViewById(id);
            rb.setOnClickListener(v -> {
                for (int otherId : coloresIds) {
                    if (otherId != id) ((RadioButton) view.findViewById(otherId)).setChecked(false);
                }
            });
        }

        EditText etHoraInicio = view.findViewById(R.id.etHoraInicio);
        EditText etHoraFin = view.findViewById(R.id.etHoraFin);
        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));

        view.findViewById(R.id.btnDialogCancelar).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDialogGuardar).setOnClickListener(v -> dialog.dismiss());

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