package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUploaderPhotoPreferences {

    private static DataUploaderPhotoPreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final String KEY_PHOTO_UPLOAD = "DataUploaderPhotoPreferences.keyPhoto";
    private static final boolean PHOTO_UPLOAD_DEFAULT_VALUE = true;

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "DATA_UPLOADER_PHOTO_PREFERENCES";

    public static synchronized DataUploaderPhotoPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DataUploaderPhotoPreferences();
            sharedPreferences = context.getApplicationContext().getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean getPhotoUpload() {
        return sharedPreferences.getBoolean(KEY_PHOTO_UPLOAD, PHOTO_UPLOAD_DEFAULT_VALUE);
    }

    public void setPhotoUpload(boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_PHOTO_UPLOAD, value);
        editor.apply();
    }

}
