package com.example.studyapp.room.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.studyapp.room.dao.appDao;
import com.example.studyapp.room.entity.materia;
import com.example.studyapp.room.entity.actividad;
import com.example.studyapp.room.entity.recordatorio;

@Database(
        entities = {
                materia.class,
                actividad.class,
                recordatorio.class
        },
        version = 1
)
public abstract class appDatabase extends RoomDatabase {

    public abstract appDao appDao();
}