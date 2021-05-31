package br.udesc.esag.participactbrasil.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import java.util.Collection;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.broadcastreceivers.CheckPendingActionsBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ActionType;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;
import br.udesc.esag.participactbrasil.support.NotificationUtility;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PendingActiveTaskIntentService extends IntentService {

    private static final String ScheduleReceiver = "br.udesc.esag.participactbrasil.support.action.ScheduleReceiver";
    private static final String AddPendingNotifications = "br.udesc.esag.participactbrasil.support.action.AddPendingNotifications";
    public static final int NOTIFICATION_ID = 97;

    private static final String TASK_ID = "TASK_ID";

    /**
     * Starts this service to perform action ScheduleReceiver with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionScheduleReceiver(Context context, long taskId) {
        Intent intent = new Intent(context, PendingActiveTaskIntentService.class);
        intent.putExtra(TASK_ID, taskId);
        intent.setAction(ScheduleReceiver);
        context.startService(intent);
    }

    public static void startAddPendingNotifications(Context context, long taskId) {
        Intent intent = new Intent(context, PendingActiveTaskIntentService.class);
        intent.putExtra(TASK_ID, taskId);
        intent.setAction(AddPendingNotifications);
        context.startService(intent);
    }


    public PendingActiveTaskIntentService() {
        super("PendingActiveTaskIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ScheduleReceiver.equals(action)) {
                long taskId = intent.getLongExtra(TASK_ID, -1);
                if (taskId >= 0)
                    handleActionScheduleReceiver(taskId);
            } else if (AddPendingNotifications.equals(intent.getAction())) {
                long taskId = intent.getLongExtra(TASK_ID, -1);
                if (taskId >= 0)
                    handleActionAddPendingNotification(taskId);
            }
        }
    }

    /**
     * Handle action ActionAddPendingActiveTasNotification in the provided background thread with the provided
     * parameters.
     */
    private void handleActionScheduleReceiver(long taskId) {

        State state = StateUtility.loadState(this);

        if (state == null)
            return;

        TaskStatus status = state.getTaskById(taskId);

        if (status == null)
            return;

        TaskFlat task = status.getTask();

        if (task == null)
            return;

        //86 400 000 = 1 day taskDuration is in minutes
        long now = System.currentTimeMillis();
        long notificationTime = now + (task.getDuration() * 60000) - 86400000;

        if (notificationTime > now + 300000) {//300000 = 5 minutes, don't sent notification if the range between accept and deadline is less than 5 minutes
            Intent intent = new Intent();
            intent.setAction(CheckPendingActionsBroadcastReceiver.CHECK_PENDING_ACTIONS);
            intent.putExtra(TASK_ID, taskId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, CheckPendingActionsBroadcastReceiver.CHECK_PENDING_ACTIONS_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarm.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        }

    }

    private void handleActionAddPendingNotification(long taskId) {

        if (taskId < 0)
            return;

        State state = StateUtility.loadState(this);

        if (state == null)
            return;

        TaskStatus status = state.getTaskById(taskId);

        if (status == null)
            return;

        Collection<ActionFlat> actions = status.getTask().getActionsDB();

        if (actions == null)
            return;

        boolean toSendPhoto = false;
        boolean toSendQuest = false;

        for (ActionFlat currentFlat : actions) {

            if (currentFlat.getType() == ActionType.PHOTO) {

                if (status.getRemainingPhotoPerAction(currentFlat.getId()) > 0) {
                    toSendPhoto = true;
                }

            } else if (currentFlat.getType() == ActionType.QUESTIONNAIRE) {

                if (!status.isQuestionnaireCompleted(currentFlat.getId())) {
                    toSendQuest = true;
                }
            }
        }

        String notificationText = "";

        if (toSendPhoto && toSendQuest)
            notificationText = getString(R.string.pending_task_photo_and_questionnaire);

        else if (toSendPhoto)
            notificationText = getString(R.string.pending_task_photo);

        else if (toSendQuest)
            notificationText = getString(R.string.pending_task_questionnaire);


        if (toSendPhoto || toSendQuest) {

            Intent resultIntent = new Intent(this, DashboardActivity.class);

            resultIntent.setAction(DashboardActivity.GO_TO_TASK_ACTIVE_FRAGMENT);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(DashboardActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationUtility.addNotification(this, R.drawable.ic_new_task, status.getTask().getName(), notificationText, NOTIFICATION_ID, resultPendingIntent);

        }
    }
}
