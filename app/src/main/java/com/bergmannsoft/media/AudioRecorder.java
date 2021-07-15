package com.bergmannsoft.media;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorder {

    private static final String TAG = AudioRecorder.class.getSimpleName();

    private Context context;
    private File mFile;
    private MediaRecorder mRecorder;

    private boolean recording = false;

    private Timer timer;
    private long seconds;

    Handler handler = new Handler(Looper.getMainLooper());

    public interface OnRecordListener {
        void onRecording(String time);
        void onCompletion(File audio);
        void onException(Exception e);
    }

    private OnRecordListener listener;

    public AudioRecorder(Context context, OnRecordListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public boolean isRecording() {
        return recording;
    }

    public void start() {
        if (recording)
            return;
        recording = true;
        mFile = MediaUtils.newAudioFile(context);
        if (mFile == null) {
            listener.onException(new FileException("Error creating file."));
            return;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFile.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, null, e);
        }

        try {
            mRecorder.start();
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }

        seconds = 0;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seconds++;
                long sec = seconds;
                long min = 0;
                while (sec > 59) {
                    sec -= 60;
                    min++;
                }
                final String ssec = sec > 9 ? String.valueOf(sec) : String.format("0%d", sec);
                final String smin = min > 9 ? String.valueOf(min) : String.format("0%d", min);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onRecording(String.format("%s:%s", smin, ssec));
                    }
                });
            }
        }, 1000, 1000);

    }

    public void stop() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
        recording = false;
        if (timer != null) {
            timer.cancel();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (seconds > 1 && mFile != null && mFile.exists()) {
                    listener.onCompletion(mFile);
                } else {
                    if (seconds <= 1) {
                        listener.onException(new TooSmallAudioException());
                    } else {
                        listener.onException(new FileException("Unknown error creating file."));
                    }
                }
            }
        });
    }

}
