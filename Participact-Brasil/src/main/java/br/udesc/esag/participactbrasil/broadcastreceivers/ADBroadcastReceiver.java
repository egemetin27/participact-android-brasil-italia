package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ADBroadcastReceiver extends BroadcastReceiver {

    private static Logger logger = LoggerFactory.getLogger(ADBroadcastReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MoSTService.class);
        i.setAction(MoSTService.STOP);
        i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.ACTIVITY_RECOGNITION_COMPARE.toInt());//TODO change
        context.startService(i);

        //TODO review implementation
        // reset btn state
      /*  Editor editor = context.getSharedPreferences(TaskActiveCard.ACTIVITY_DETECTION_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(TaskActiveCard.KEY_AD_SELECTED, -1);
        editor.apply();*/
        logger.info("Activity Detection stopped by alarm.");

    }

}
