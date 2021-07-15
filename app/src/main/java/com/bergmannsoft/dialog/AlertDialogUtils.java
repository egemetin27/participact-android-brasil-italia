package com.bergmannsoft.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.List;

import br.com.participact.participactbrasil.R;

/**
 * Created by fabiobergmann on 9/29/16.
 */

public class AlertDialogUtils {

    public static void showAlert(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = createDialog(activity, activity.getString(R.string.alert), message, "OK", null, null, null);
                dialog.show();
            }
        });
    }

    public static void showMessage(final Activity activity, final String message, final String title) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = createDialog(activity, title, message, "OK", null, null, null);
                dialog.show();
            }
        });
    }

    public static AlertDialog createDialog(Activity activity, String title, String message,
                                       String positive, String negative,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(positive,
                positiveListener);
        if (negative != null && negative.length() > 0) {
            builder.setNegativeButton(negative, negativeListener);
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static void showError(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    BProgressDialog.hideProgressBar();
                    AlertDialog alert = createDialog(activity, activity.getString(R.string.error), message, "OK", null,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showOptionsDialog(final Activity activity, final String title, final List<String> options, final DialogInterface.OnClickListener listener) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] items = new String[options.size()];
                options.toArray(items);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title).setItems(items, listener);
                builder.create().show();
            }
        });

    }

    public static void showOptionsDialog(final Activity activity, final String title, final int resIdOptions, final DialogInterface.OnClickListener listener) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title).setItems(resIdOptions, listener);
                builder.create().show();
            }
        });

    }

}
