package com.example.studyapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.entity.materia;

import java.util.List;

public class InicioMateria extends RecyclerView.Adapter< InicioMateria.ViewHolder> {

    List<materia> lista;

    public InicioMateria(List<materia> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtHorario;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreMateria);
            txtHorario = itemView.findViewById(R.id.txtHorarioMateria);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inicio_materia, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        materia m = lista.get(position);

        holder.txtNombre.setText(m.nombre);
        holder.txtHorario.setText(m.horaInicio + " - " + m.horaFin);
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}