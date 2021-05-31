package br.udesc.esag.participactbrasil.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.TaskStackBuilder;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.services.LocationService;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.broadcastreceivers.GcmBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.GeolocalizationTaskUtils;
import br.udesc.esag.participactbrasil.support.LoginUtility;
import br.udesc.esag.participactbrasil.support.NotificationUtility;

public class NotificationAcceptMandatoryTaskListener implements RequestListener<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationAcceptMandatoryTaskListener.class);

    Context context;
    NotificationManager mNotificationManager;
    TaskFlat taskDB;

    public NotificationAcceptMandatoryTaskListener(Context context, TaskFlat task) {
        this.context = context;
        this.taskDB = task;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        LoginUtility.checkIfLoginException(context, spiceException);
    }

    @Override
    public void onRequestSuccess(ResponseMessage result) {
        if (result != null && result.getResultCode() == 200) {
            if (taskDB != null) {
                //activate task
                Location last = LocationService.getLastLocation();
                if (GeolocalizationTaskUtils.isActivatedByArea(taskDB) && !GeolocalizationTaskUtils.isInside(context, last.getLongitude(), last.getLatitude(), taskDB.getActivationArea())) {
                    StateUtility.changeTaskState(context, taskDB, TaskState.RUNNING_BUT_NOT_EXEC);
                    logger.info("Activated mandatory task with id {} in RUNNING_BUT_NOT_EXEC because not in activation area.", taskDB.getId());
                } else {
                    StateUtility.changeTaskState(context, taskDB, TaskState.RUNNING);
                    logger.info("Activated mandatory task with id {} in RUNNING.", taskDB.getId());
                }

                Intent resultIntent = new Intent(context, DashboardActivity.class);

                resultIntent.setAction(DashboardActivity.GO_TO_TASK_ACTIVE_FRAGMENT);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(DashboardActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );


                NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_task_accepted), GcmBroadcastReceiver.NOTIFICATION_NEW_TASK_ACCEPTED, resultPendingIntent);
            }
        }
    }

}