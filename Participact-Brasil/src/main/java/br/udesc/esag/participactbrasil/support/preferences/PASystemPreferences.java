package br.udesc.esag.participactbrasil.support.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fabiobergmann on 31/10/16.
 */

public class PASystemPreferences {

    private static PASystemPreferences instance;
    private static SharedPreferences sharedPreferences;
    private static int MODE = Context.MODE_PRIVATE;

    private static final String FILENAME = "SYSTEM_PREFERENCES";
    private static final String HELP_URL = "help_url";
    private static final String HELP_CONTENT = "help_content";
    private static final String ABOUT_URL = "about_url";
    private static final String ABOUT_CONTENT = "about_content";

    public static synchronized PASystemPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new PASystemPreferences();
            sharedPreferences = context.getSharedPreferences(FILENAME, MODE);
        }
        return instance;
    }

    private PASystemPreferences() {

    }

    public void saveHelpUrl(String url) {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(HELP_URL, url);
        e.apply();
    }

    public String getHelpUrl() {
        return sharedPreferences.getString(HELP_URL, null);
    }

    public String getHelpContent() {
        return sharedPreferences.getString(HELP_CONTENT, null);
    }

    public void saveHelpContent(String content) {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(HELP_CONTENT, content);
        e.apply();
    }

    public void saveAboutUrl(String url) {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(ABOUT_URL, url);
        e.apply();
    }

    public String getAboutUrl() {
        return sharedPreferences.getString(ABOUT_URL, null);
    }

    public String getAboutContent() {
        return sharedPreferences.getString(ABOUT_CONTENT, null);
    }

    public void saveAboutContent(String content) {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(ABOUT_CONTENT, content);
        e.apply();
    }

}
