package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.udesc.esag.participactbrasil.services.PendingActiveTaskIntentService;

public class CheckPendingActionsBroadcastReceiver extends BroadcastReceiver {

    public static final String CHECK_PENDING_ACTIONS = "br.udesc.esag.participactbrasil.participact.CHECK_PENDING_ACTIONS";
    private static final String TASK_ID = "TASK_ID";
    public static final int CHECK_PENDING_ACTIONS_REQUEST_CODE = 98;

    public CheckPendingActionsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        long taskId = intent.getLongExtra(TASK_ID, -1);

        if (taskId > 0)
            PendingActiveTaskIntentService.startAddPendingNotifications(context, taskId);

    }
}
