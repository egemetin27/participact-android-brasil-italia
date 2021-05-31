package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.services.NetworkService;
import br.udesc.esag.participactbrasil.support.preferences.ChangeTimePreferences;

public class DailyNotificationBroadcastReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory
            .getLogger(DailyNotificationBroadcastReceiver.class);

    public static final int DAILY_NOTIFICATION_REQUEST_CODE = 99;
    public static final String DAILY_NOTIFICATION_INTENT = "br.udesc.esag.participactbrasil.participact.DAILY_NOTIFICATION_INTENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("Received daily notification intent");
        if (!ChangeTimePreferences.getInstance(context).getChangeTimeRequest()) {
            Intent i = new Intent(context, NetworkService.class);
            i.setAction(NetworkService.CHECK_TASK_FROM_GCM_ACTION);
            context.startService(i);
        }
    }

}
