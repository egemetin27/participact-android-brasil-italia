package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.TimeWarningActivity;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ActionType;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;
import br.udesc.esag.participactbrasil.services.NetworkService;
import br.udesc.esag.participactbrasil.support.NotificationUtility;
import br.udesc.esag.participactbrasil.support.preferences.ChangeTimePreferences;

public class ProgressBroadcastReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ProgressBroadcastReceiver.class);
    private static final String TAG = ProgressBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // aggiorno progresso dei task di sensing
        StateUtility.incrementSensingProgress(context);

        // ping a most
        Log.i(TAG, "Ping to MoST, last timestamp: "
                + MoSTPingBroadcastReceiver.moSTlastResponsePing);

        State state = StateUtility.loadState(context);
        List<TaskFlat> tasks = state.getTaskByState(TaskState.RUNNING);

        HashMap<Pipeline.Type, Integer> runningPipeline = new HashMap<Pipeline.Type, Integer>();

        for (TaskFlat taskFlat : tasks) {
            for (ActionFlat actionFlat : taskFlat.getActionsDB()) {
                TaskStatus status = StateUtility.getTaskStatus(context, taskFlat);
                if (actionFlat.getType() == ActionType.SENSING_MOST && status.getProgressSensingPercentual() < 100) {
                    Pipeline.Type pipeline = Pipeline.Type.fromInt(actionFlat.getInput_type());
                    if (runningPipeline.containsKey(pipeline)) {
                        int count = runningPipeline.get(pipeline);
                        runningPipeline.put(pipeline, count + 1);
                    } else {
                        runningPipeline.put(pipeline, 1);
                    }
                }
            }
        }


        Intent i = new Intent(context, MoSTService.class);
        i.setAction(MoSTService.PING);
        i.putExtra(MoSTService.KEY_PARTICIPACT_STATE, runningPipeline);
        context.startService(i);

        long lastCurrent = ChangeTimePreferences.getInstance(context).getLastCurrentMillisChecked();
        long lastElapsed = ChangeTimePreferences.getInstance(context).getLastElapsedChecked();

        if (lastCurrent != 0 && lastElapsed != 0
                && !ChangeTimePreferences.getInstance(context).getChangeTimeRequest()) {

            long diffCurrent = Math.abs(System.currentTimeMillis() - lastCurrent);
            long diffElapsed = Math.abs(SystemClock.elapsedRealtime() - lastElapsed);

            if (Math.abs(diffCurrent - diffElapsed) > NetworkService.CHANGE_TIME_THRESHOLD) {
                logger.warn("Freeze task. DiffCurrent {}, diffElapsed {}", diffCurrent, diffElapsed);

                ChangeTimePreferences.getInstance(context).setChangeTimeRequest(true);
                StateUtility.freezeAllTask(context);

                NotificationManager mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Intent notificationIntent = new Intent(context, TimeWarningActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationUtility.addNotification(context, R.drawable.ic_time_error, context.getString(R.string.participact_compromised), context.getString(R.string.date_time_changed), GcmBroadcastReceiver.NOTIFICATION_TIME_ERR, contentIntent);

            }

        }

        // update current millis and elapsed in pref
        ChangeTimePreferences.getInstance(context).setLastCurrentMillisChecked(
                System.currentTimeMillis());
        ChangeTimePreferences.getInstance(context).setLastElapsedChecked(
                SystemClock.elapsedRealtime());

        // if set check time with ntp server
        if (ChangeTimePreferences.getInstance(context).getChangeTimeRequest()) {
            Intent netIntent = new Intent(context, NetworkService.class);
            netIntent.setAction(NetworkService.CHECK_TIME_ACTION);
            context.startService(netIntent);
        }

    }

}
