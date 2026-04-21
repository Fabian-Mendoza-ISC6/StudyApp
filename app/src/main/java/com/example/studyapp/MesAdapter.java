package com.example.studyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyapp.room.entity.actividad;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MesAdapter extends RecyclerView.Adapter<MesAdapter.ViewHolder> {

    private final List<Calendar> listaMeses;
    private final List<actividad> actividades;
    private final CalendarioAdapter.OnDiaClickListener listener;

    public MesAdapter(List<Calendar> listaMeses, List<actividad> actividades, CalendarioAdapter.OnDiaClickListener listener) {
        this.listaMeses = listaMeses;
        this.actividades = actividades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar calMes = listaMeses.get(position);
        
        // Título del mes
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String nombreMes = sdf.format(calMes.getTime());
        holder.txtNombreMes.setText(nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));

        // Calcular días
        List<String> listaDias = new ArrayList<>();
        Calendar tempCal = (Calendar) calMes.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int primerDiaSemana = tempCal.get(Calendar.DAY_OF_WEEK) - 1;
        int diasEnMes = calMes.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < primerDiaSemana; i++) listaDias.add("");
        for (int i = 1; i <= diasEnMes; i++) listaDias.add(String.valueOf(i));

        // Configurar los días del mes
        holder.rvDias.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 7));
        holder.rvDias.setAdapter(new CalendarioAdapter(listaDias, actividades, calMes, listener));
    }

    @Override
    public int getItemCount() {
        return listaMeses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreMes;
        RecyclerView rvDias;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreMes = itemView.findViewById(R.id.txtNombreMes);
            rvDias = itemView.findViewById(R.id.rvDiasDelMes);
        }
    }
}