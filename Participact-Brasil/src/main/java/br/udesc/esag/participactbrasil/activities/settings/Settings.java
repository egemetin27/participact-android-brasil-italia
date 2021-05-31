package br.udesc.esag.participactbrasil.activities.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.bergmannsoft.util.Connectivity;

/**
 * Created by fabiobergmann on 13/10/16.
 */

public class Settings {

    private static Settings instance;
    private Context context;

    private Settings(Context context) {
        this.context = context;
    }

    public static Settings getInstance(Context context) {
        if (instance == null) {
            instance = new Settings(context);
        }
        return instance;
    }

    public void setUseWiFiOnly(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("use_wifi_only", b);
        editor.commit();
    }

    public boolean isUseWiFiOnly() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getBoolean("use_wifi_only", false);
    }

    public boolean canSendData() {
        boolean wifiOnly = isUseWiFiOnly();
        return !wifiOnly || Connectivity.isWiFiConnected(context);
    }
}
