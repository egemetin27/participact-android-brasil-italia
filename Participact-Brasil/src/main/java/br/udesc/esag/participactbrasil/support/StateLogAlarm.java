package br.udesc.esag.participactbrasil.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

//Each hour sends an intent to StateLogBroadcastReceiver to log the state
public class StateLogAlarm {

    private static final Logger logger = LoggerFactory.getLogger(StateLogAlarm.class);
    public static final String TAG = StateLogAlarm.class.getSimpleName();
    public static final String STATE_LOG_ACTION = "br.udesc.esag.participactbrasil.participact.STATE_LOG";
    public static final long PERIOD = 60 * 60 * 1000; //60 MIN
    private static final int REQUEST_CODE = 0;

    private static StateLogAlarm instance;

    private Context context;
    private AtomicBoolean isStarted;
    private Intent intent;
    private PendingIntent pendingIntent;

    private StateLogAlarm(Context context) {
        this.context = context;
        isStarted = new AtomicBoolean(false);
        intent = new Intent();
        intent.setAction(STATE_LOG_ACTION);
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static synchronized StateLogAlarm getInstance(Context context) {
        if (instance == null) {
            instance = new StateLogAlarm(context);
            logger.info("Created new CheckClientAppVersionAlarm instance");
        }
        return instance;
    }


    public synchronized void start() {
        if (!isStarted.get()) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), PERIOD, pendingIntent);
            logger.info("Progress Alarm started");
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
