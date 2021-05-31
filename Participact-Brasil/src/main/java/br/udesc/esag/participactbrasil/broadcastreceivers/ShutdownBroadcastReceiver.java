package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.services.LocationService;
import br.udesc.esag.participactbrasil.support.preferences.ChangeTimePreferences;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {

    private final static Logger logger = LoggerFactory.getLogger(ShutdownBroadcastReceiver.class);


    @Override
    public void onReceive(Context context, Intent intent) {

        logger.info("Shutdown.");

        ChangeTimePreferences.getInstance(context).setLastCurrentMillisChecked(0);
        ChangeTimePreferences.getInstance(context).setLastElapsedChecked(0);

        Intent i = new Intent(context, LocationService.class);
        i.setAction(LocationService.STOP);
        context.startService(i);
    }

}
