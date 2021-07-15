package com.bergmannsoft.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.LayoutInflater;

import com.bergmannsoft.activity.ActivityMode;
import com.bergmannsoft.activity.BActivity;
import com.bergmannsoft.util.FileCache;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Created by fabiobergmann on 4/28/16.
 */
public class BApplication extends MultiDexApplication {

    // region Variables

    public static final String TAG = BApplication.class.getSimpleName();
    protected Handler uiHandler;
    protected Handler jobHandler;
    protected LayoutInflater layoutInflater;
    protected SharedPreferences preferences;
    private FileCache fileCache;
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
        fileCache = new FileCache(this);
    }

    // endregion

    // region Getters and setters

    public FileCache getFileCache() {
        return fileCache;
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
                        } else {
                            Log.e(TAG, "baseFragment is null");
                        }
                    }
                });

            } else {
                Log.e(TAG, "baseFragment is null");
            }

            if (!uiHandler.sendMessage(nm)) {
                Log.e(TAG, "could not send message for handler!");
            }
        } else {
            Log.e(TAG, "uiHandler is null");
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

    public void removePreference(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

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

    //region Activities transition

    private List<ActivityMode> stack = new LinkedList<>();

    public void showActivity(Context context, Class<? extends Activity> clazz) {
        stack.add(ActivityMode.SHOW);
        if (!gotoActivity(context, clazz)) {
            stack.remove(stack.size() - 1);
        }
    }

    public void showModalActivity(Context context, Class<? extends Activity> clazz) {
        stack.add(ActivityMode.MODAL);
        if (!gotoActivity(context, clazz)) {
            stack.remove(stack.size() - 1);
        }
    }

    public void showModalActivity(Context context, Intent intent) {
        stack.add(ActivityMode.MODAL);
        if (!gotoActivity(context, intent)) {
            stack.remove(stack.size() - 1);
        }
    }

    public void clearActivities() {
        stack.clear();
    }

    /*public void performShow(Activity a) {
        if (stack.size() == 0) return;
        ActivityMode mode = stack.get(stack.size() - 1);
        switch (mode) {
            case MODAL:
                a.overridePendingTransition(R.anim.slide_to_top, 0);
                break;
            case SHOW:
                a.overridePendingTransition(R.anim.slide_to_left, 0);
                break;
        }
    }

    public void performDestroy(Activity a) {
        if (stack.size() == 0) return;
        ActivityMode mode = stack.get(stack.size() - 1);
        switch (mode) {
            case MODAL:
                a.overridePendingTransition(0, R.anim.slide_to_bottom);
                break;
            case SHOW:
                a.overridePendingTransition(0, R.anim.slide_to_right);
                break;
        }
        stack.remove(stack.size() - 1);
    }*/

    protected boolean gotoActivity(Context context, Class<? extends Activity> clazz) {
        if (this.getClass().equals(clazz)) {
            return false;
        }
        Intent intent = new Intent(context, clazz);
        return gotoActivity(context, intent);
    }

    protected boolean gotoActivity(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        return true;
    }

    //endregion

}
