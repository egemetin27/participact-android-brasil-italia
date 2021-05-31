package br.udesc.esag.participactbrasil.dialog;

import android.app.Activity;
import android.util.Log;

import br.com.bergmannsoft.dialog.BProgressDialog;

/**
 * Created by fabiobergmann on 9/30/16.
 */

public class ProgressDialog {

    private static final String TAG = ProgressDialog.class.getSimpleName();

    public static void show(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BProgressDialog.showProgressBar(activity, false, message);
            }
        });
    }

    public static void updateMessage(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                BProgressDialog.updateMessage(message);
            }

        });
    }

    public static void dismiss(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    BProgressDialog.hideProgressBar();
                } catch (Exception e) {
                    Log.e(TAG, null, e);
                }
            }
        });
    }

}
