package com.example.studyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        int id = intent.getIntExtra("id", 0);
        String titulo = intent.getStringExtra("titulo");
        String mensaje = intent.getStringExtra("mensaje");
        String tipo = intent.getStringExtra("tipo"); // "CLASE" o "ACTIVIDAD"
        int idReferencia = intent.getIntExtra("idReferencia", -1);

        Intent destinoIntent;
        if ("CLASE".equals(tipo)) {
            destinoIntent = new Intent(context, VerMateria.class);

            destinoIntent.putExtra("id", idReferencia);
        } else {
            destinoIntent = new Intent(context, VerActividad.class);

            destinoIntent.putExtra("id", idReferencia);
        }

        destinoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, id, destinoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "RECOR_STUDYAPP")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setFullScreenIntent(pendingIntent, true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            notificationManager.cancel(notificationId);
        }, 15000);
    }
}