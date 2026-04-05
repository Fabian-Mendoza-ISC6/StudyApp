package com.example.studyapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyapp.room.entity.materia;
import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    private List<materia> listaMaterias;
    private OnItemClickListener listener;

    // 🔥 INTERFAZ
    public interface OnItemClickListener {
        void onItemClick(materia m);
    }

    // 🔥 CONSTRUCTOR
    public MateriaAdapter(List<materia> listaMaterias, OnItemClickListener listener) {
        this.listaMaterias = listaMaterias;
        this.listener = listener;
    }

    public void setMaterias(List<materia> materias) {
        this.listaMaterias = materias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        materia m = listaMaterias.get(position);
        holder.txtNombre.setText(m.nombre);
        holder.txtMaestro.setText("Maestr@: " + m.profesor);
        holder.txtAula.setText("Aula: " + m.salon);
        holder.txtHorario.setText(m.horaInicio + " a " + m.horaFin);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(m);
            }
        });
        
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(m.color));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return listaMaterias != null ? listaMaterias.size() : 0;
    }

    static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtMaestro, txtAula, txtHorario;
        View viewColor;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtMateria);
            txtMaestro = itemView.findViewById(R.id.txtMaestro);
            txtAula = itemView.findViewById(R.id.txtAula);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            viewColor = itemView.findViewById(R.id.txtColor);
        }
    }
}
