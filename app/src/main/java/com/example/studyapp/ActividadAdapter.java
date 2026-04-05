package com.example.studyapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<actividad> listaActividades;
    private List<materia> listaMaterias;
    private OnItemClickListener listener;

    // ✅ CONSTRUCTOR CORRECTO (UNO SOLO)
    public ActividadAdapter(List<actividad> listaActividades, List<materia> listaMaterias, OnItemClickListener listener) {
        this.listaActividades = listaActividades;
        this.listaMaterias = listaMaterias;
        this.listener = listener;
    }

    public void setActividades(List<actividad> actividades) {
        this.listaActividades = actividades;
        notifyDataSetChanged();
    }

    public void setMaterias(List<materia> materias) {
        this.listaMaterias = materias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        actividad a = listaActividades.get(position);

        holder.txtTipo.setText(a.tipo);
        holder.txtFecha.setText("Fecha: " + a.fechaEntrega);
        holder.txtHorario.setText("Hora: " + a.horaInicio);
        holder.txtEstado.setText("Estado: " + a.estado); // 🔥 CORRECCIÓN

        // 🔥 BUSCAR MATERIA
        materia matEncontrada = null;
        if (listaMaterias != null) {
            for (materia m : listaMaterias) {
                if (m.id == a.idMateria) {
                    matEncontrada = m;
                    break;
                }
            }
        }

        if (matEncontrada != null) {
            holder.txtMateria.setText(matEncontrada.nombre);

            try {
                holder.viewColor.setBackgroundColor(Color.parseColor(matEncontrada.color));
            } catch (Exception e) {
                holder.viewColor.setBackgroundColor(Color.GRAY);
            }

        } else {
            holder.txtMateria.setText("Sin materia");
            holder.viewColor.setBackgroundColor(Color.GRAY);
        }

        // 🔥 CLICK
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {

                String Materia = "Sin materia";

                if (listaMaterias != null) {
                    for (materia m : listaMaterias) {
                        if (m.id == a.idMateria) {
                            Materia = m.nombre;
                            break;
                        }
                    }
                }

                a.descripcion = a.descripcion == null ? "" : a.descripcion;

                listener.onItemClick(a, Materia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaActividades != null ? listaActividades.size() : 0;
    }

    // 🔥 INTERFAZ
    public interface OnItemClickListener {
        void onItemClick(actividad act, String Materia);
    }

    // 🔥 VIEW HOLDER (SIN lógica de click aquí)
    static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView txtTipo, txtMateria, txtEstado, txtFecha, txtHorario;
        View viewColor;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            viewColor = itemView.findViewById(R.id.txtColor);
        }
    }
}