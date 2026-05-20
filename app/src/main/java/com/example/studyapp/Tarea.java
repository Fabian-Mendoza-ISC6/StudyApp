package com.example.studyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.database.appDatabase;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Tarea extends BaseActivity { // ✅ Hereda de BaseActivity para aplicar Fuente y Color
    appDatabase db;
    RecyclerView recyclerActividades;
    ActividadAdapter adapter;
    List<actividad> listaActividades = new ArrayList<>();
    List<materia> listaMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tareas);
        
        db = AppDataBaseInstancia.getInstance(this);

        findViewById(R.id.btnInicio).setOnClickListener(v -> startActivity(new Intent(this, Inicio.class)));
        findViewById(R.id.btnCalendario).setOnClickListener(v -> startActivity(new Intent(this, Horario.class)));
        findViewById(R.id.btnTareas).setOnClickListener(v -> cargarActividades()); 
        findViewById(R.id.btnKamba).setOnClickListener(v -> startActivity(new Intent(this, Kanba.class)));
        findViewById(R.id.btnEventos).setOnClickListener(v -> startActivity(new Intent(this, Calendario.class)));
        findViewById(R.id.btnConfiguracion).setOnClickListener(v -> startActivity(new Intent(this, Configuraciones.class)));
        findViewById(R.id.img_study).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        recyclerActividades = findViewById(R.id.recyclerActividades);
        recyclerActividades.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActividadAdapter(listaActividades, listaMaterias,(act, nombreMateria) -> {
            Intent intent = new Intent(Tarea.this, VerActividad.class);
            intent.putExtra("id", act.id);
            intent.putExtra("tipo", act.tipo);
            intent.putExtra("estado", act.estado);
            intent.putExtra("fecha", act.fechaEntrega);
            intent.putExtra("hora", act.horaInicio);
            intent.putExtra("descripcion", act.descripcion);
            intent.putExtra("idMateria", act.idMateria);
            intent.putExtra("materiaNombre", nombreMateria);
            startActivity(intent);
        });
        recyclerActividades.setAdapter(adapter);

        cargarActividades();
        cargarMaterias();

        findViewById(R.id.btnAgrgar).setOnClickListener(v -> mostrarDialogoAgregarTarea());
    }

    private void cargarActividades() {
        new Thread(() -> {
            List<actividad> lista = db.appDao().obtenerActividades();
            runOnUiThread(() -> {
                listaActividades = lista;
                adapter.setActividades(listaActividades);
            });
        }).start();
    }

    private void cargarMaterias() {
        new Thread(() -> {
            List<materia> lista = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                listaMaterias = lista;
                adapter.setMaterias(listaMaterias);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
        cargarMaterias();
    }

    private void mostrarDialogoAgregarTarea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.agregar_tareas, null);
        
        applyColorToView(view);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        AutoCompleteTextView tipoAct = view.findViewById(R.id.RegistroActividad);
        AutoCompleteTextView materiaAct = view.findViewById(R.id.RegistroMateria);
        AutoCompleteTextView estadoAct = view.findViewById(R.id.EstadoActividad);
        EditText etFecha = view.findViewById(R.id.DiaEntrega);
        EditText etHora = view.findViewById(R.id.HoraInicio);
        EditText etDesc = view.findViewById(R.id.TextDescripcion);

        new Thread(() -> {
            listaMaterias = db.appDao().obtenerMaterias();
            runOnUiThread(() -> {
                List<String> nombresUnicos = new ArrayList<>();
                for (materia m : listaMaterias) {
                    if (!nombresUnicos.contains(m.nombre)) nombresUnicos.add(m.nombre);
                }
                materiaAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresUnicos));
            });
        }).start();

        String[] tipos = {getString(R.string.cat_tarea), getString(R.string.cat_examen), getString(R.string.cat_proyecto), getString(R.string.cat_estudio)};
        String[] estados = {getString(R.string.est_pendiente), getString(R.string.est_en_curso), getString(R.string.est_finalizado)};

        tipoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipos));
        estadoAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados));

        tipoAct.setOnClickListener(v -> tipoAct.showDropDown());
        materiaAct.setOnClickListener(v -> materiaAct.showDropDown());
        estadoAct.setOnClickListener(v -> estadoAct.showDropDown());

        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (v1, y, m, d) -> {
                etFecha.setText(d + "/" + (m + 1) + "/" + y);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (v1, h, min) -> {
                String amPm = (h >= 12) ? "PM" : "AM";
                int hour = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, min, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        view.findViewById(R.id.btnDialogCancelar).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDialogGuardar).setOnClickListener(v -> {
            String t = tipoAct.getText().toString().trim();
            String mNombre = materiaAct.getText().toString().trim();
            if (t.isEmpty() || mNombre.isEmpty()) { 
                Toast.makeText(this, getString(R.string.msg_obligatorio), Toast.LENGTH_SHORT).show(); 
                return; 
            }

            int idMat = -1;
            for (materia mat : listaMaterias) {
                if (mat.nombre.equals(mNombre)) { idMat = mat.id; break; }
            }

            actividad n = new actividad();
            n.tipo = t;
            n.estado = estadoAct.getText().toString();
            n.fechaEntrega = etFecha.getText().toString();
            n.horaInicio = etHora.getText().toString();
            n.descripcion = etDesc.getText().toString();
            n.idMateria = idMat;

            new Thread(() -> {
                long idGen = db.appDao().insertarActividad(n);
                AlarmHelper.programarAviso(Tarea.this, (int) idGen, "ACTIVIDAD", n.fechaEntrega, n.horaInicio, n.tipo, n.descripcion);
                runOnUiThread(() -> { cargarActividades(); dialog.dismiss(); });
            }).start();
        });
        dialog.show();
    }
}
