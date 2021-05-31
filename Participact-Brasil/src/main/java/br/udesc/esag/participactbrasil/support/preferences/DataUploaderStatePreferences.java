package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUploaderStatePreferences {

    private static DataUploaderStatePreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final String KEY_STATE_UPLOAD = "DataUploaderStatePreferences.keyLog";
    private static final boolean STATE_UPLOAD_DEFAULT_VALUE = false;

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "DATA_UPLOADER_STATE_PREFERENCES";

    public static synchronized DataUploaderStatePreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DataUploaderStatePreferences();
            sharedPreferences = context.getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean getStateUpload() {
        return sharedPreferences.getBoolean(KEY_STATE_UPLOAD, STATE_UPLOAD_DEFAULT_VALUE);
    }

    public void setStateUpload(boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_STATE_UPLOAD, value);
        editor.apply();
    }

}
