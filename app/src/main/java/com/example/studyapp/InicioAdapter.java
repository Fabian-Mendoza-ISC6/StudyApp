package com.example.studyapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.entity.actividad;

import java.util.List;

public class InicioAdapter extends RecyclerView.Adapter<InicioAdapter.ViewHolder> {

    List<actividad> lista;

    public InicioAdapter(List<actividad> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTipo, txtFecha;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTipo = itemView.findViewById(R.id.txtTipoInicio);
            txtFecha = itemView.findViewById(R.id.txtFechaInicio);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inicio, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        actividad act = lista.get(position);

        holder.txtTipo.setText(act.tipo);
        holder.txtFecha.setText(act.fechaEntrega);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}