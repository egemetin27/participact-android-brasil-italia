package br.com.bergmannsoft.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;

import org.most.MoSTApplication;

import java.util.Set;

import br.com.bergmannsoft.activity.BActivity;

/**
 * Created by fabiobergmann on 4/28/16.
 */
public class BApplication extends MoSTApplication {

    // region Variables

    private static final String TAG = BApplication.class.getSimpleName();
    protected Handler uiHandler;
    protected Handler jobHandler;
    protected LayoutInflater layoutInflater;
    protected SharedPreferences preferences;
    private BaseFragment baseFragment;

    // endregion

    // region Life Cycle

    @Override
    public void onCreate() {
        super.onCreate();

        startJobHandler();
        uiHandler = new Handler();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // endregion

    // region Handlers

    private void startJobHandler() {
        HandlerThread handlerThread = new HandlerThread("jobThread");
        handlerThread.start();
        jobHandler = new Handler(handlerThread.getLooper());
    }

    public void registerHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public Handler registerHandler(BActivity activity) {
        uiHandler = new Handler(activity);
        return uiHandler;
    }

    public Handler getJobHandler() {
        return jobHandler;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    // endregion

    // region Inflater

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    // endregion

    // region Dispatch message

    public void dispatchMessage(Message m) {
        if (uiHandler != null) {
            final Message nm = new Message();
            nm.copyFrom(m);

            if (baseFragment != null) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (baseFragment != null) {
                            baseFragment.handleMessage(nm);
                        }
                    }
                });

            }

            if (!uiHandler.sendMessage(nm)) {
                Log.e(TAG, "could not send message for handler!");
            }
        }
    }

    public void dispatchMessage(int id) {
        dispatchMessage(id, null);
    }

    public void dispatchMessage(int id, Object obj) {
        Message m = new Message();
        m.what = id;
        m.obj = obj;
        dispatchMessage(m);
    }

    // endregion

    // region Set base fragment

    public void setBaseFragment(BaseFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    // endregion

    //region Preferences

    public void savePreferenceString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void savePreferenceLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void savePreferenceInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void savePreferenceFloat(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void savePreferenceStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public String getPreferenceString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public long getPreferenceLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public int getPreferenceInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public float getPreferenceFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public Set<String> getPreferenceSet(String key, Set<String> defaultValue) {
        return preferences.getStringSet(key, defaultValue);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    //endregion

}
