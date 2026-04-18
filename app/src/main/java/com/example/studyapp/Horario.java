package com.example.studyapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    
    Button btnL, btnM, btnMi, btnJ, btnV, btnS, btnD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario);
        db = appDatabaseInstancia.getInstance(this);

        btnL = findViewById(R.id.btnLunes);
        btnM = findViewById(R.id.btnMartes);
        btnMi = findViewById(R.id.btnMiercoles);
        btnJ = findViewById(R.id.btnJueves);
        btnV = findViewById(R.id.btnViernes);
        btnS = findViewById(R.id.btnSabado);
        btnD = findViewById(R.id.btnDomingo);

        diaActualSeleccionado = obtenerDiaDeHoy();

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
        int colorNormal = Color.WHITE;
        int colorSeleccionado = Color.parseColor("#89CAC9");

        btnL.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnM.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnMi.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnJ.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnV.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnS.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
        btnD.setBackgroundTintList(ColorStateList.valueOf(colorNormal));

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

        // 🔥 LÓGICA DE ORDENAMIENTO POR HORA
        Collections.sort(filtradas, (m1, m2) -> {
            try {
                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
                // Normalizar formatos para que Java los entienda siempre
                String h1 = m1.horaInicio.replace("a. m.", "AM").replace("p. m.", "PM").replace("a.m.", "AM").replace("p.m.", "PM");
                String h2 = m2.horaInicio.replace("a. m.", "AM").replace("p. m.", "PM").replace("a.m.", "AM").replace("p.m.", "PM");
                
                Date date1 = format.parse(h1);
                Date date2 = format.parse(h2);
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0;
            }
        });

        adapter.setMaterias(filtradas);
    }

    private void cargarMaterias() {
        new Thread(() -> {
            List<materia> materiasDB = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                todasLasMaterias = materiasDB;
                actualizarColorBotones();
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

        AutoCompleteTextView etMateria = view.findViewById(R.id.etMateria);
        EditText etProfesor = view.findViewById(R.id.etProfesor);
        EditText etSalon = view.findViewById(R.id.etSalon);
        EditText etHoraInicio = view.findViewById(R.id.etHoraInicio);
        EditText etHoraFin = view.findViewById(R.id.etHoraFin);

        int[] rbDiasIds = {R.id.rbLunes, R.id.rbMartes, R.id.rbMiercoles, R.id.rbJueves, R.id.rbViernes, R.id.rbSabado, R.id.rbDomingo};
        String[] nombresDias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

        for (int id : rbDiasIds) {
            RadioButton rb = view.findViewById(id);
            rb.setOnClickListener(v -> {
                for (int otherId : rbDiasIds) {
                    if (otherId != id) ((RadioButton) view.findViewById(otherId)).setChecked(false);
                }
            });
        }

        int[] coloresIds = {R.id.rbRojo, R.id.rbNaranja, R.id.rbAmarillo, R.id.rbVerde, R.id.rbAzul, R.id.rbMorado, R.id.rbCeleste, R.id.rbCafe, R.id.rbRosa, R.id.rbGris};
        String[] coloresHex = {"#F44336", "#FF9800", "#FFC107", "#4CAF50", "#2196F3", "#9C27B0", "#00BCD4", "#795548", "#E91E63", "#607D8B"};

        new Thread(() -> {
            List<materia> materiasUnicas = db.appDao().obtenerMaterias(); 
            List<String> nombres = new ArrayList<>();
            for (materia m : materiasUnicas) {
                if (!nombres.contains(m.nombre)) nombres.add(m.nombre);
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> adapterMat = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
                etMateria.setAdapter(adapterMat);
                etMateria.setOnItemClickListener((parent, view1, position, id) -> {
                    String seleccionado = (String) parent.getItemAtPosition(position);
                    for (materia m : materiasUnicas) {
                        if (m.nombre.equals(seleccionado)) {
                            etProfesor.setText(m.profesor);
                            etSalon.setText(m.salon);
                            for (int i = 0; i < coloresHex.length; i++) {
                                RadioButton rbColor = view.findViewById(coloresIds[i]);
                                if (coloresHex[i].equalsIgnoreCase(m.color)) {
                                    rbColor.setChecked(true);
                                    view.setTag(coloresHex[i]);
                                } else {
                                    rbColor.setChecked(false);
                                }
                            }
                            break;
                        }
                    }
                });
            });
        }).start();

        etHoraInicio.setOnClickListener(v -> mostrarReloj(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarReloj(etHoraFin));

        for (int i = 0; i < coloresIds.length; i++) {
            int index = i;
            RadioButton rb = view.findViewById(coloresIds[i]);
            rb.setOnClickListener(v -> {
                for (int cid : coloresIds) {
                    if (cid != coloresIds[index]) ((RadioButton) view.findViewById(cid)).setChecked(false);
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

            if (nombre.isEmpty()) { etMateria.setError("Obligatorio"); return; }
            
            String diaElegido = "";
            for (int i = 0; i < rbDiasIds.length; i++) {
                if (((RadioButton) view.findViewById(rbDiasIds[i])).isChecked()) {
                    diaElegido = nombresDias[i];
                    break;
                }
            }

            if (diaElegido.isEmpty()) {
                ((RadioButton) view.findViewById(R.id.rbLunes)).setError("Elige un día");
                return;
            }

            String colorSeleccionado = (view.getTag() != null) ? view.getTag().toString() : "#2196F3";

            materia nuevaMateria = new materia();
            nuevaMateria.nombre = nombre;
            nuevaMateria.profesor = profesor;
            nuevaMateria.salon = salon;
            nuevaMateria.horaInicio = hInicio;
            nuevaMateria.horaFin = hFin;
            nuevaMateria.dias = diaElegido;
            nuevaMateria.color = colorSeleccionado;

            new Thread(() -> {
                db.appDao().insertarMateria(nuevaMateria);
                runOnUiThread(() -> { cargarMaterias(); dialog.dismiss(); });
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