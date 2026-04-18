package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.appDatabaseInstancia;
import com.example.studyapp.room.entity.materia;

public class Horario extends AppCompatActivity {
    appDatabase db;
    RecyclerView recyclerView;
    MateriaAdapter adapter;
    List<materia> todasLasMaterias = new ArrayList<>();
    String diaActualSeleccionado = "";
    
    // Referencias a los botones de los días
    Button btnL, btnM, btnMi, btnJ, btnV, btnS, btnD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario);
        db = appDatabaseInstancia.getInstance(this);

        // Inicializar botones
        btnL = findViewById(R.id.btnLunes);
        btnM = findViewById(R.id.btnMartes);
        btnMi = findViewById(R.id.btnMiercoles);
        btnJ = findViewById(R.id.btnJueves);
        btnV = findViewById(R.id.btnViernes);
        btnS = findViewById(R.id.btnSabado);
        btnD = findViewById(R.id.btnDomingo);

        // Detectar el día de hoy
        diaActualSeleccionado = obtenerDiaDeHoy();

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MateriaAdapter(new ArrayList<>(), m -> {
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

        cargarMaterias();
        configurarFiltrosDias();

        // Botones de navegación
        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(Horario.this, inicio.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> startActivity(new Intent(Horario.this, Tarea.class)));
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(Horario.this, Kanba.class)));
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(Horario.this, MainActivity.class)));
        
        Button btnAgregar = findViewById(R.id.btnAgrgar);
        btnAgregar.setOnClickListener(v -> mostrarDialogo());
    }

    private String obtenerDiaDeHoy() {
        String dia = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(new Date());
        dia = dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();
        if (dia.equals("Miércoles")) dia = "Miercoles";
        if (dia.equals("Sábado")) dia = "Sabado";
        return dia;
    }

    private void configurarFiltrosDias() {
        btnL.setOnClickListener(v -> cambiarDia("Lunes"));
        btnM.setOnClickListener(v -> cambiarDia("Martes"));
        btnMi.setOnClickListener(v -> cambiarDia("Miercoles"));
        btnJ.setOnClickListener(v -> cambiarDia("Jueves"));
        btnV.setOnClickListener(v -> cambiarDia("Viernes"));
        btnS.setOnClickListener(v -> cambiarDia("Sabado"));
        btnD.setOnClickListener(v -> cambiarDia("Domingo"));
    }

    private void cambiarDia(String dia) {
        diaActualSeleccionado = dia;
        actualizarColorBotones();
        filtrarMaterias();
    }

    private void actualizarColorBotones() {
        // Color normal (blanco) y color seleccionado (morado claro)
        int colorNormal = Color.WHITE;
        int colorSeleccionado = Color.parseColor("#89CAC9");

        // Resetear todos los botones
        btnL.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnM.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnMi.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnJ.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnV.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnS.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnD.setBackgroundTintList(ColorStateList.valueOf(colorNormal));

        // Pintar solo el seleccionado
        switch (diaActualSeleccionado) {
            case "Lunes": btnL.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Martes": btnM.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Miercoles": btnMi.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Jueves": btnJ.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Viernes": btnV.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Sabado": btnS.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
            case "Domingo": btnD.setBackgroundTintList(ColorStateList.valueOf(colorSeleccionado)); break;
        }
    }

    private void filtrarMaterias() {
        List<materia> filtradas = new ArrayList<>();
        for (materia m : todasLasMaterias) {
            if (m.dias != null && m.dias.contains(diaActualSeleccionado)) {
                filtradas.add(m);
            }
        }
        adapter.setMaterias(filtradas);
    }

    private void cargarMaterias() {
        new Thread(() -> {
            List<materia> materiasDB = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                todasLasMaterias = materiasDB;
                actualizarColorBotones(); // Resaltar botón de hoy al cargar
                filtrarMaterias();
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

        EditText etMateria = view.findViewById(R.id.etMateria);
        EditText etProfesor = view.findViewById(R.id.etProfesor);
        EditText etSalon = view.findViewById(R.id.etSalon);
        EditText etHoraInicio = view.findViewById(R.id.etHoraInicio);
        EditText etHoraFin = view.findViewById(R.id.etHoraFin);

        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));

        int[] cbIds = {R.id.cbLunes, R.id.cbMartes, R.id.cbMiercoles, R.id.cbJueves, R.id.cbViernes, R.id.cbSabado, R.id.cbDomingo};
        String[] nombresDias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

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

            StringBuilder diasSeleccionados = new StringBuilder();
            for (int i = 0; i < cbIds.length; i++) {
                CheckBox cb = view.findViewById(cbIds[i]);
                if (cb != null && cb.isChecked()) {
                    if (diasSeleccionados.length() > 0) diasSeleccionados.append(", ");
                    diasSeleccionados.append(nombresDias[i]);
                }
            }

            String colorSeleccionado = (view.getTag() != null) ? view.getTag().toString() : "#2196F3";

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