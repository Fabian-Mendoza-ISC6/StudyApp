package com.example.studyapp;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.materia;

import java.util.List;

public class ItemKanba extends RecyclerView.Adapter<ItemKanba.ViewHolder> {

    List<actividad> lista;
    List<materia> materias;

    public ItemKanba(List<actividad> lista, List<materia> materias) {
        this.lista = lista;
        this.materias = materias;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;

        public ViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtKanban);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kanba, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        actividad a = lista.get(position);

        String nombreMateria = "Sin materia";

        for (materia m : materias) {
            if (m.id == a.idMateria) {
                nombreMateria = m.nombre;
                break;
            }
        }

        holder.txt.setText("📘 " + nombreMateria + "\n📌 " + a.tipo);


        holder.itemView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadow, a, 0);

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setDatos(List<actividad> nuevaLista, List<materia>nuevasMaterias){
        this.lista = nuevaLista;
        this.materias = nuevasMaterias;
        notifyDataSetChanged();
    }

}