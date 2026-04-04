package com.example.studyapp.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.studyapp.room.entity.materia;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.recordatorio;

import java.util.List;

@Dao
public interface appDao {

    // ===== MATERIA =====
    @Insert
    void insertarMateria(materia materia);

    @Query("SELECT * FROM materia")
    List<materia> obtenerMaterias();

    @Update
    void actualizarMateria(materia materia);

    @Delete
    void eliminarMateria(materia materia);


    // ===== ACTIVIDAD =====
    @Insert
    void insertarActividad(actividad actividad);

    @Query("SELECT * FROM actividad")
    List<actividad> obtenerActividades();

    @Query("SELECT * FROM actividad WHERE idMateria = :idMateria")
    List<actividad> obtenerActividadesPorMateria(int idMateria);

    @Update
    void actualizarActividad(actividad actividad);

    @Delete
    void eliminarActividad(actividad actividad);


    // ===== RECORDATORIO =====
    @Insert
    void insertarRecordatorio(recordatorio recordatorio);

    @Query("SELECT * FROM recordatorio WHERE idActividad = :idActividad")
    List<recordatorio> obtenerRecordatoriosPorActividad(int idActividad);

    @Update
    void actualizarRecordatorio(recordatorio recordatorio);

    @Delete
    void eliminarRecordatorio(recordatorio recordatorio);
}
