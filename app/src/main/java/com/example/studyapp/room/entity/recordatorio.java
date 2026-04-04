package com.example.studyapp.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = actividad.class,
                parentColumns = "id",
                childColumns = "idActividad",
                onDelete = ForeignKey.CASCADE
        )
)
public class recordatorio {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idActividad;   // FK hacia Tarea

    public String fechaAviso;
    public String tipoAviso;
}
