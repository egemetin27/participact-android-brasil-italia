package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUploaderLogPreferences {

    private static DataUploaderLogPreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final String KEY_LOG_UPLOAD = "DataUploaderLogPreferences.keyLog";
    private static final boolean LOG_UPLOAD_DEFAULT_VALUE = false;

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "DATA_UPLOADER_LOG_PREFERENCES";

    public static synchronized DataUploaderLogPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DataUploaderLogPreferences();
            sharedPreferences = context.getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean getLogUpload() {
        return sharedPreferences.getBoolean(KEY_LOG_UPLOAD, LOG_UPLOAD_DEFAULT_VALUE);
    }

    public void setLogUpload(boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOG_UPLOAD, value);
        editor.apply();
    }

}
