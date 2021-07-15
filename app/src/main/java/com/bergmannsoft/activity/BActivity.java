package com.bergmannsoft.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.bergmannsoft.application.BApplication;
import com.bergmannsoft.application.BMessageType;
import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.util.FileCache;

import java.util.List;

/**
 * Created by fabiobergmann on 4/28/16.
 * Bergmannsoft Activity with common functionality.
 */
public class BActivity extends AppCompatActivity implements Handler.Callback {

    // region Variables

    protected static String TAG = BActivity.class.getSimpleName();

    protected FileCache fileCache;
    protected Handler uiHandler;
    protected Handler jobHandler;
    protected BApplication application;
    private boolean performShow;
    private int playingSound;
    private MediaPlayer mediaPlayer;

    // endregion

    // region Life cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        application = (BApplication) getApplication();
        fileCache = application.getFileCache();
        uiHandler = application.registerHandler(this);
        jobHandler = application.getJobHandler();
        performShow = true;
    }

    @Override
    protected void onResume() {

        if (uiHandler == null)
            uiHandler = application.registerHandler(this);
        else
            application.registerHandler(uiHandler);

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (performShow) {
            performShow = false;
//            application.performShow(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
//        application.performDestroy(this);
    }

    protected void present(Class<? extends Activity> activity) {
        Intent i = new Intent(this, activity);
        startActivity(i);
    }

    // endregion

    // region Handler

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case BMessageType.PROGRESS_DIALOG_SHOW:
                showProgress((String) message.obj);
                return true;
        }
        return false;
    }

    // endregion

    // region Start activity for result

    protected void startActivityForResult(Class<? extends Activity> clazz,
                                          int requestCode) {
        Intent i = new Intent(this, clazz);
        startActivityForResult(i, requestCode);
    }

    // endregion

    //region Simple dialog_categories message

    protected void showAlert(final String message) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = createDialog("Alert", message, "OK", null, null, null);
                dialog.show();
            }
        });
    }

    protected void showMessage(final String message, final String title) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = createDialog(title, message, "OK", null, null, null);
                dialog.show();
            }
        });
    }

    protected AlertDialog createDialog(String title, String message,
                                       String positive, String negative,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(positive,
                positiveListener);
        if (negative != null && negative.length() > 0) {
            builder.setNegativeButton(negative, negativeListener);
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    protected void showError(final String message) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
//                if (progressDialog != null) {
//                    dismissProgress();
//                }
                try {
                    BProgressDialog.hideProgressBar();
                    AlertDialog alert = createDialog("Error", message, "OK", null,
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

    protected Dialog createOptionsDialog(String title, List<String> options,
                                         DialogInterface.OnClickListener listener) {
        String[] items = new String[options.size()];
        options.toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setItems(items, listener);

        return builder.create();
    }

    //endregion

    // region Progress Dialog

    protected void showProgress() {
        showProgress(null);
    }

    protected void showProgress(final String message) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                BProgressDialog.showProgressBar(BActivity.this, false, message);
            }
        });
    }

    protected void updateProgressMessage(final String message) {
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                BProgressDialog.updateMessage(message);
            }

        });

    }

    protected void dismissProgress() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    BProgressDialog.hideProgressBar();
                } catch (Exception e) {
//                    Crashlytics.log(Log.ERROR, TAG, e.getMessage());
                    Log.e(TAG, null, e);
                }
            }
        });
    }

    //endregion

    //region Set and Get values to TextView and EditText

    protected void setTextViewValue(int id, String value) {
        TextView t = (TextView) findViewById(id);
        if (t != null)
            t.setText(value);
    }

    protected String getTextViewValue(int id) {
        TextView t = (TextView) findViewById(id);
        if (t != null)
            return t.getText().toString();
        return "";
    }

    protected String getEditTextValue(int id) {
        EditText e = (EditText) findViewById(id);
        if (e != null)
            return e.getText().toString();
        return "";
    }

    protected void setEditTextValue(int id, String value) {
        if (value == null) value = "";
        EditText e = (EditText) findViewById(id);
        if (e != null)
            e.setText(value);
    }

    //endregion

    //region Sound

    protected void stopSound() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
//            Crashlytics.log(Log.ERROR, TAG, e.getMessage());
            Log.e(TAG, null, e);
        }
    }

    protected void playSound(int id) {

        stopSound();

        if (playingSound == id) {
            playingSound = -1;
            return;
        }

        this.playingSound = id;

        try {
            mediaPlayer = MediaPlayer.create(this, id);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    application.dispatchMessage(BMessageType.ACTION_MEDIA_PLAYER_FINISHED);
                    mediaPlayer.release();
                }

            });
            mediaPlayer.start();
        } catch (Exception e) {
//            Crashlytics.log(Log.ERROR, TAG, e.getMessage());
            Log.e(TAG, null, e);
        }
    }

    //endregion
}
