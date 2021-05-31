package br.udesc.esag.participactbrasil.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import br.udesc.esag.participactbrasil.support.preferences.DataUploaderPhotoPreferences;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderQuestionnairePreferences;

public class UploadAlarm {

    private static final Logger logger = LoggerFactory.getLogger(UploadAlarm.class);
    public static final String TAG = UploadAlarm.class.getSimpleName();
    public static final String UPLOAD_INTENT = "br.udesc.esag.participactbrasil.participact.UPLOAD_INTENT";
    public static final long PERIOD = 5 * 60 * 1000; //5 MIN
    private static final int REQUEST_CODE = 564;

    private static UploadAlarm instance;

    private Context context;
    private AtomicBoolean isStarted;
    private Intent intent;
    private PendingIntent pendingIntent;

    private UploadAlarm(Context context) {
        this.context = context;
        isStarted = new AtomicBoolean(false);
        intent = new Intent();
        intent.setAction(UPLOAD_INTENT);
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static synchronized UploadAlarm getInstance(Context context) {
        if (instance == null) {
            instance = new UploadAlarm(context);
            logger.info("Created new UploadAlarm instance");
        }
        return instance;
    }


    public synchronized void start() {
        DataUploaderQuestionnairePreferences.getInstance(context).setQuestionnaireUpload(true);
        DataUploaderPhotoPreferences.getInstance(context).setPhotoUpload(true);
        if (!isStarted.get()) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), PERIOD, pendingIntent);
            logger.info("Upload Alarm started");
            isStarted.set(true);
        }
    }

    public synchronized void stop() {
        if (isStarted.get()) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.cancel(pendingIntent);
            logger.info("Progress Alarm stopped");
            isStarted.set(false);
        }
    }

    public synchronized boolean isStarted() {
        return isStarted.get();
    }

}
