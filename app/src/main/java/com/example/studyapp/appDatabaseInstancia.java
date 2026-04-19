package com.example.studyapp;

import android.content.Context;
import androidx.room.Room;
import com.example.studyapp.room.database.appDatabase;

public class appDatabaseInstancia {

    private static appDatabase INSTANCE;

    public static appDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    appDatabase.class,
                    "study_db"
            )
            .fallbackToDestructiveMigration() // Permite que la base de datos se recree si cambia la estructura
            .build();
        }
        return INSTANCE;
    }
}
