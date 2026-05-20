package com.example.studyapp;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;

public class SettingsManager {
    private static final String PREFS_NAME = "StudyAppPrefs";
    private static final String KEY_SETTINGS = "app_settings";

    public static void saveFontScale(Context context, float scale) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String jsonStr = prefs.getString(KEY_SETTINGS, "{}");
            JSONObject json = new JSONObject(jsonStr);
            json.put("fontScale", scale);
            prefs.edit().putString(KEY_SETTINGS, json.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getFontScale(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String jsonStr = prefs.getString(KEY_SETTINGS, "{}");
            JSONObject json = new JSONObject(jsonStr);
            return (float) json.optDouble("fontScale", 1.0);
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static void saveThemeColor(Context context, String colorHex) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String jsonStr = prefs.getString(KEY_SETTINGS, "{}");
            JSONObject json = new JSONObject(jsonStr);
            json.put("themeColor", colorHex);
            prefs.edit().putString(KEY_SETTINGS, json.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getThemeColor(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String jsonStr = prefs.getString(KEY_SETTINGS, "{}");
            JSONObject json = new JSONObject(jsonStr);
            return json.optString("themeColor", "#89CAB4");
        } catch (Exception e) {
            return "#89CAB4";
        }
    }
}
