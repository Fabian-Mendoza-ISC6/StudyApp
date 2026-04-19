package com.example.studyapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class appController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        crearCanalNotificaciones();
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String idCanal = "RECOR_STUDYAPP";
            CharSequence nombre = "Recordatorios Académicos";
            String descripcion = "Avisos de clases y tareas";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel canal = new NotificationChannel(idCanal, nombre, importancia);
            canal.setDescription(descripcion);
            canal.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }
}