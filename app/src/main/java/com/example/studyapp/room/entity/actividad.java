package com.example.studyapp.room.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = materia.class,
                parentColumns = "id",
                childColumns = "idMateria",
                onDelete = ForeignKey.CASCADE
        )
)
public class actividad {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String tipo;          // tarea, examen, proyecto
    public String estado;        // pendiente, en_proceso, terminado
    public String fechaEntrega;
    public String horaInicio;
    public String descripcion;

    public int idMateria;        // FK real
}
