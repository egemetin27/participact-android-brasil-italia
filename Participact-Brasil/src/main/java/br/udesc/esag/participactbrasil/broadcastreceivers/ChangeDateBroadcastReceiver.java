package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.support.CheckClientAppVersionAlarm;
import br.udesc.esag.participactbrasil.support.ProgressAlarm;
import br.udesc.esag.participactbrasil.support.UploadAlarm;

public class ChangeDateBroadcastReceiver extends BroadcastReceiver {

    private final static Logger logger = LoggerFactory.getLogger(ChangeDateBroadcastReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            logger.warn("Changed date to {}", new DateTime());
            UploadAlarm.getInstance(context).stop();
            ProgressAlarm.getInstance(context).stop();
            CheckClientAppVersionAlarm.getInstance(context).stop();

        } finally {

            UploadAlarm.getInstance(context).start();
            ProgressAlarm.getInstance(context).start();
            CheckClientAppVersionAlarm.getInstance(context).start();

        }
    }

}
