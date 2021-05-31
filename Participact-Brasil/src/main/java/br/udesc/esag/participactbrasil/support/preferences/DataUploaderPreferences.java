package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUploaderPreferences {

    private static DataUploaderPreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final long DEFAULT_RES_VALUE = 0L;
    private static final long TIME_THRESHOLD = 1000 * 60 * 15; //15 minutes

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "DATA_UPLOADER_PREFERENCES";

    public static synchronized DataUploaderPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DataUploaderPreferences();
            sharedPreferences = context.getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean checkLastUpload(String dataUploadType) {
        long lastUpload = sharedPreferences.getLong(dataUploadType, DEFAULT_RES_VALUE);
        if (lastUpload == DEFAULT_RES_VALUE || (lastUpload + TIME_THRESHOLD <= System.currentTimeMillis())) {
            return true;
        } else {
            return false;
        }
    }

    public long getLastUpload(String dataUploadType) {
        return sharedPreferences.getLong(dataUploadType, DEFAULT_RES_VALUE);
    }

    public void setLastUpload(String dataUploadType, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(dataUploadType, value);
        editor.apply();
    }

}
