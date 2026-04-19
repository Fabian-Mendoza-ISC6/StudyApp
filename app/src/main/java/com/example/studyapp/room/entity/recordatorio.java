package com.example.studyapp.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = actividad.class,
                        parentColumns = "id",
                        childColumns = "idActividad",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = materia.class,
                        parentColumns = "id",
                        childColumns = "idMateria",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class recordatorio {

    @PrimaryKey(autoGenerate = true)
    public int id;


    public Integer idActividad;   // ID de la Tarea/Examen
    public Integer idMateria;     // ID de la Clase

    public String fechaAviso;     // Formato sugerido: "yyyy-MM-dd HH:mm" o un timestamp
    public String tipoAviso;      // Para diferenciar: "CLASE" o "ACTIVIDAD"

    // Campo extra útil para saber si ya se notificó
    public boolean yaNotificado = false;
}