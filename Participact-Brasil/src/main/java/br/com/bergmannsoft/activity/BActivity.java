package br.com.bergmannsoft.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import br.com.bergmannsoft.application.BApplication;
import br.com.bergmannsoft.application.BMessageType;

/**
 * Created by fabiobergmann on 4/28/16.
 * BergmannSoft Activity with common functionality.
 */
public class BActivity extends AppCompatActivity implements Handler.Callback {

    // region Variables

    protected static String TAG = BActivity.class.getSimpleName();

    protected Handler uiHandler;
    protected Handler jobHandler;
    protected BApplication application;
    private boolean performShow;
    private int playingSound;
    private MediaPlayer mediaPlayer;

    private Timer updateTimer;

    // endregion

    // region Life cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        application = (BApplication) getApplication();
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

        if (updateTimer != null) {
            updateTimer.cancel();
        }
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                jobHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onUpdate();
                    }
                });
            }
        }, 0, 20000);
    }

    @Override
    protected void onPause() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        super.onPause();
    }

    protected void onUpdate() {

    }

    // endregion

    // region Handler

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            default:
                return false;
        }
    }

    // endregion

    // region Start activity for result

    protected void startActivityForResult(Class<? extends Activity> clazz,
                                          int requestCode) {
        Intent i = new Intent(this, clazz);
        startActivityForResult(i, requestCode);
    }

    // endregion

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
            Log.e(TAG, null, e);
        }
    }

    //endregion
}
