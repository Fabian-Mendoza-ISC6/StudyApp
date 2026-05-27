package com.example.studyapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmHelper {


    public static void programarAviso(Context context, int idReferencia, String tipo, String fecha, String hora, String titulo, String detalles) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long tiempoEvento = convertirAMilis(fecha, hora);
        if (tiempoEvento <= 0) return;

        long tiempoAviso;
        String mensaje;
        if ("CLASE".equals(tipo)) {
            tiempoAviso = tiempoEvento - (30 * 60 * 1000); // 30 min antes
            mensaje = "Clase: " + titulo + "\n" + detalles;
        } else {
            tiempoAviso = tiempoEvento - (60 * 60 * 1000); // 1 hora antes
            mensaje = "Actividad: " + titulo + "\n" + detalles;
        }

        if (tiempoAviso < System.currentTimeMillis()) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("idReferencia", idReferencia);
        intent.putExtra("tipo", tipo);
        intent.putExtra("titulo", "Recordatorio de " + (tipo.equals("CLASE") ? "Horario" : "Actividad"));
        intent.putExtra("mensaje", mensaje);

        int alarmId = (tipo.equals("CLASE") ? 10000 : 20000) + idReferencia;

        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoAviso, pi);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, tiempoAviso, pi);
        }
    }

    private static long convertirAMilis(String fecha, String hora) {
        try {
            String horaLimpia = hora.replace("a. m.", "AM").replace("p. m.", "PM")
                    .replace("a.m.", "AM").replace("p.m.", "PM")
                    .trim().toUpperCase();

            String fechaProcesada = fecha;
            if (fecha.matches(".*[a-zA-Z].*")) {
                fechaProcesada = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new java.util.Date());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy hh:mm a", Locale.US);
            try {
                return sdf.parse(fechaProcesada + " " + horaLimpia).getTime();
            } catch (Exception e) {

                SimpleDateFormat sdf2 = new SimpleDateFormat("d/M/yyyy h:mm a", Locale.US);
                return sdf2.parse(fechaProcesada + " " + horaLimpia).getTime();
            }
        } catch (Exception e) {
            android.util.Log.e("ALARM_ERROR", "Fallo al convertir: " + fecha + " " + hora);
            return 0;
        }
    }

    public static void cancelarAviso(Context context, int idReferencia, String tipo) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);

        int alarmId = (tipo.equals("CLASE") ? 10000 : 20000) + idReferencia;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
