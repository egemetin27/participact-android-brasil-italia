package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;
import br.udesc.esag.participactbrasil.support.AlarmStateUtility;

public class AlarmBroadcastReceiver extends BroadcastReceiver implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(AlarmBroadcastReceiver.class);

    private static final long serialVersionUID = -2289366968646749313L;

    private long taskId;

    public AlarmBroadcastReceiver(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        long taskId = intent.getExtras().getLong(AlarmStateUtility.KEY_TASK);
        logger.info("Received resume alarm for task {}.", taskId);
        State state = StateUtility.loadState(context);

        if (state != null) {
            TaskFlat task = state.getTaskById(taskId).getTask();
            StateUtility.changeTaskState(context, task, TaskState.RUNNING);
            AlarmStateUtility.removeAlarm(context.getApplicationContext(), taskId);
        }

    }

    @Override
    public int hashCode() {
        return (int) taskId;
    }

}
