package com.example.studyapp.room.entity;



import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class materia {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String profesor;
    public String dias;
    public String horaInicio;
    public String horaFin;
    public String salon;
    public String color;
}
