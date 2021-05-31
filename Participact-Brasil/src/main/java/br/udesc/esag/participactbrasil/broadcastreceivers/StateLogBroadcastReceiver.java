package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;

public class StateLogBroadcastReceiver extends BroadcastReceiver {

    private final static Logger logger = LoggerFactory.getLogger(StateLogBroadcastReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        State state = StateUtility.loadState(context);
        if (state != null) {

            logger.info("STATUS");
            for (Entry<Long, TaskStatus> task : state.getTasks().entrySet()) {

                logger.info(String.format("Task id: %d", task.getKey()));
                logger.info(String.format("Task state: %s", task.getValue().getState().toString()));
                if (task.getValue().getAcceptedTime() == null) {
                    logger.info(String.format("Accepted Time: not accepted"));
                } else {
                    logger.info(String.format("Accepted Time: %s", task.getValue().getAcceptedTime().toString()));
                }
                logger.info(String.format("Last checked timestamp: %s", new DateTime(task.getValue().getLastCheckedTimestamp())));
                logger.info(String.format("Sensing progress: %d", task.getValue().getSensingProgress()));
                logger.info(String.format("Photo progress: %d", task.getValue().getPhotoProgress()));
                logger.info(String.format("Questionnaire progress: %d", task.getValue().getQuestionnaireProgress()));
                logger.info("--------------------------");

            }
            logger.info("END STATUS");

        } else {
            logger.info("NO STATUS");
        }

    }

}
