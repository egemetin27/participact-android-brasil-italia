package br.udesc.esag.participactbrasil.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.broadcastreceivers.AlarmBroadcastReceiver;

public class AlarmStateUtility {

    public final static String CONSOLE_SUSPEND_INTENT = "br.udesc.esag.participactbrasil.participact.CONSOLE_SUSPEND_INTENT";
    public final static String KEY_TASK = "CONSOLE_SUSPEND_INTENT.KEY_TASK";

    private final static long SUSPEND_DURATION = 1000 * 60 * 60 * 8;

    public static synchronized void addAlarm(Context context, long taskId) {
        try {
            ParticipActApplication application = (ParticipActApplication) context.getApplicationContext();

            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent();
            intent.setAction(CONSOLE_SUSPEND_INTENT + taskId);
            intent.putExtra(KEY_TASK, taskId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + SUSPEND_DURATION, pendingIntent);

            if (!application.getAlarmBR().containsKey(taskId)) {
                AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver(taskId);
                application.getAlarmBR().put(taskId, alarm);
                IntentFilter filter = new IntentFilter(CONSOLE_SUSPEND_INTENT + taskId);
                context.registerReceiver(alarm, filter);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static synchronized void removeAlarm(Context context, long taskId) {
        try {
            ParticipActApplication application = (ParticipActApplication) context.getApplicationContext();

            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent();
            intent.setAction(CONSOLE_SUSPEND_INTENT + taskId);
            intent.putExtra(KEY_TASK, taskId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mgr.cancel(pendingIntent);

            if (application.getAlarmBR().containsKey(taskId)) {
                AlarmBroadcastReceiver alarm = application.getAlarmBR().get(taskId);
                context.unregisterReceiver(alarm);
                application.getAlarmBR().remove(taskId);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}
