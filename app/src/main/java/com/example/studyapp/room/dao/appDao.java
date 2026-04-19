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
    long insertarMateria(materia materia);

    @Query("SELECT * FROM materia")
    List<materia> obtenerMaterias();

    @Update
    void actualizarMateria(materia materia);

    @Delete
    void eliminarMateria(materia materia);


    // ===== ACTIVIDAD =====
    @Insert
    long insertarActividad(actividad actividad);

    @Query("SELECT * FROM actividad")
    List<actividad> obtenerActividades();

    @Query("SELECT * FROM actividad WHERE idMateria = :idMateria")
    List<actividad> obtenerActividadesPorMateria(int idMateria);

    @Update
    void actualizarActividad(actividad act);

    @Delete
    void eliminarActividad(actividad act);
    @Query("DELETE FROM actividad WHERE id = :id")
    void eliminarActividadPorId(int id);
    @Query("SELECT * FROM actividad WHERE estado = :estado")
    List<actividad> obtenerPorEstado(String estado);


    // ===== MÉTODOS PARA RECORDATORIOS =====

    @Insert
    long insertarRecordatorio(recordatorio recordatorio);

    @Query("SELECT * FROM recordatorio WHERE yaNotificado = :estado")
    List<recordatorio> obtenerRecordatoriosPorEstado(boolean estado);

    @Query("UPDATE recordatorio SET yaNotificado = 1 WHERE id = :id")
    void marcarComoNotificado(int id);

    @Query("DELETE FROM recordatorio WHERE idActividad = :idActividad")
    void eliminarPorActividad(int idActividad);

    @Query("DELETE FROM recordatorio WHERE idMateria = :idMateria")
    void eliminarPorMateria(int idMateria);
}
