package br.udesc.esag.participactbrasil.support.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class ShowTipsPreferences {

    private static ShowTipsPreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final boolean DEFAULT_SHOW_VALUE = true;

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "SHOW_TIPS_PREFERENCES";

    public static synchronized ShowTipsPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new ShowTipsPreferences();
            sharedPreferences = context.getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean shouldShowTips(String scene) {
        return sharedPreferences.getBoolean(scene, DEFAULT_SHOW_VALUE);
    }

    public void setShouldShowTips(String scene, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(scene, value);
        editor.apply();
    }
}
