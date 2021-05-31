package br.com.bergmannsoft.application;

import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

public class BUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final String TAG = BUncaughtExceptionHandler.class.getSimpleName();
    private UncaughtExceptionHandler defaultUEH;

    public BUncaughtExceptionHandler() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        Log.e(TAG, null, e);

        defaultUEH.uncaughtException(thread, e);
    }

}
