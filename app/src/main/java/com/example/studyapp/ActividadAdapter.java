package com.example.studyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyapp.room.entity.actividad;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<actividad> listaActividades;

    public ActividadAdapter(List<actividad> listaActividades) {
        this.listaActividades = listaActividades;
    }

    public void setActividades(List<actividad> actividades) {
        this.listaActividades = actividades;
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
        
        // Ajustamos los datos según los campos reales de tu entidad 'actividad'
        holder.txtTipo.setText("Tipo: " + a.tipo);
        holder.txtEstado.setText("Tipo: " + a.estado);
        holder.txtFecha.setText("Fecha: " + a.fechaEntrega);
        holder.txtHorario.setText("Hora: " + a.horaInicio);
        
        // El nombre de la materia se suele obtener por el idMateria, 
        // por ahora pondremos el ID o un texto fijo si no tienes la relación cargada
        holder.txtMateria.setText("ID Materia: " + a.idMateria);
        
        // Si tienes lógica de colores para el estado, puedes aplicarla aquí
        if ("Finalizado".equals(a.estado)) {
            holder.viewColor.setBackgroundColor(android.graphics.Color.GREEN);
        } else if ("En curso".equals(a.estado)) {
            holder.viewColor.setBackgroundColor(android.graphics.Color.YELLOW);
        } else {
            holder.viewColor.setBackgroundColor(android.graphics.Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return listaActividades != null ? listaActividades.size() : 0;
    }

    static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView txtTipo, txtMateria,txtEstado, txtFecha, txtHorario;
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
