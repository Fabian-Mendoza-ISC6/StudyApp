package com.example.studyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyapp.room.entity.actividad;
import java.util.Calendar;
import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.ViewHolder> {

    private final List<String> dias;
    private final List<actividad> actividades;
    private final Calendar calNavegacion;
    private final OnDiaClickListener listener;
    private final int hoyDia;
    private final int hoyMes;
    private final int hoyAño;

    public interface OnDiaClickListener {
        void onDiaClick(String fecha);
    }

    public CalendarioAdapter(List<String> dias, List<actividad> actividades, Calendar calNavegacion, OnDiaClickListener listener) {
        this.dias = dias;
        this.actividades = actividades;
        this.calNavegacion = calNavegacion;
        this.listener = listener;
        
        Calendar realHoy = Calendar.getInstance();
        this.hoyDia = realHoy.get(Calendar.DAY_OF_MONTH);
        this.hoyMes = realHoy.get(Calendar.MONTH) + 1;
        this.hoyAño = realHoy.get(Calendar.YEAR);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dia_calendario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String diaStr = dias.get(position);
        holder.txtDia.setText(diaStr);
        holder.txtIconoTarea.setVisibility(View.GONE);
        holder.txtEmoji.setVisibility(View.GONE);

        if (!diaStr.isEmpty()) {
            int dia = Integer.parseInt(diaStr);
            int mesViendo = calNavegacion.get(Calendar.MONTH) + 1;
            int añoViendo = calNavegacion.get(Calendar.YEAR);

            // 1. Mostrar ❌ solo si el día ya pasó REALMENTE
            if (añoViendo < hoyAño) {
                holder.txtEmoji.setVisibility(View.VISIBLE);
            } else if (añoViendo == hoyAño) {
                if (mesViendo < hoyMes) {
                    holder.txtEmoji.setVisibility(View.VISIBLE);
                } else if (mesViendo == hoyMes && dia < hoyDia) {
                    holder.txtEmoji.setVisibility(View.VISIBLE);
                }
            }

            // 2. Mostrar 📚 si hay tareas para esta fecha exacta (día/mes/año)
            String fechaDia = dia + "/" + mesViendo + "/" + añoViendo;
            for (actividad act : actividades) {
                if (act.fechaEntrega != null && act.fechaEntrega.equals(fechaDia)) {
                    if (act.estado.equalsIgnoreCase("Pendiente") || act.estado.equalsIgnoreCase("En curso")) {
                        holder.txtIconoTarea.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDiaClick(fechaDia);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDia, txtEmoji, txtIconoTarea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDia = itemView.findViewById(R.id.txtNumeroDia);
            txtEmoji = itemView.findViewById(R.id.txtEmojiTachado);
            txtIconoTarea = itemView.findViewById(R.id.txtIconoTarea);
        }
    }
}