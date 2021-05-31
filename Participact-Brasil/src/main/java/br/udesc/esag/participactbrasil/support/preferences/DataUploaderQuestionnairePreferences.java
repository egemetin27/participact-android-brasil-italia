package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUploaderQuestionnairePreferences {

    private static DataUploaderQuestionnairePreferences instance;
    private static SharedPreferences sharedPreferences;

    private static final String KEY_QUESTIONNAIRE_UPLOAD = "DataUploaderQuestionnairePreferences.keyQuestionnaire";
    private static final boolean QUESTIONNAIRE_UPLOAD_DEFAULT_VALUE = false;

    private static final int MODE = Context.MODE_PRIVATE;
    private static final String NAME = "DATA_UPLOADER_QUESTIONNAIRE_PREFERENCES";

    public static synchronized DataUploaderQuestionnairePreferences getInstance(Context context) {
        if (instance == null) {
            instance = new DataUploaderQuestionnairePreferences();
            sharedPreferences = context.getApplicationContext().getSharedPreferences(NAME, MODE);
        }
        return instance;
    }

    public boolean getQuestionnaireUpload() {
        return sharedPreferences.getBoolean(KEY_QUESTIONNAIRE_UPLOAD, QUESTIONNAIRE_UPLOAD_DEFAULT_VALUE);
    }

    public void setQuestionnaireUpload(boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_QUESTIONNAIRE_UPLOAD, value);
        editor.apply();
    }

}
