package br.udesc.esag.participactbrasil.support;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.TaskStackBuilder;

import com.octo.android.robospice.SpiceManager;

import br.udesc.esag.participactbrasil.services.LocationService;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.broadcastreceivers.GcmBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlatList;
import br.udesc.esag.participactbrasil.network.request.AcceptTaskRequest;
import br.udesc.esag.participactbrasil.network.request.NotificationAcceptMandatoryTaskListener;

/**
 * Created by LabGES on 14/06/2016.
 */
public class TaskUtility {

    private static Context context;
    private static TaskUtility taskUtility;

    public static TaskUtility getInstance(Context ctx){
        context = ctx;
        if(taskUtility == null){
            taskUtility = new TaskUtility();
        }
        return taskUtility;
    }

    public void handleNewTasks(TaskFlatList result, SpiceManager _contentManager){
        boolean added = false;
        StateUtility.addTaskList(context, result);
        if (result.getList().size() > 0) {
            for (TaskFlat task : result.getList()) {
                if (!task.getCanBeRefused() && !GeolocalizationTaskUtils.isNotifiedByArea(task)) {
                    AcceptTaskRequest request = new AcceptTaskRequest(context, task.getId());
                    if (!_contentManager.isStarted()) {
                        _contentManager.start(context);
                    }
                    _contentManager.execute(request,
                            new NotificationAcceptMandatoryTaskListener(context, task));
                } else if (GeolocalizationTaskUtils.isNotifiedByArea(task)) {

                    TaskFlat taskDB = StateUtility.getTaskById(context, task.getId());
                    if (taskDB == null) {
                        taskDB = StateUtility.addTask(context, task);
                        if (taskDB != null) {
                            //state in hidden
                            SupportStateUtility.changeTaskState(context, taskDB, TaskState.HIDDEN);
                        }
                    }
                } else {
                    added = true;
                }
            }
        }

        if (StateUtility.getTaskByState(context, TaskState.HIDDEN).size() > 0) {
            Location last = LocationService.getLastLocation();
            if (last != null) {
                for (TaskFlat task : StateUtility.getTaskByState(context, TaskState.HIDDEN)) {
                    if (GeolocalizationTaskUtils.isInside(context, last.getLongitude(), last.getLatitude(), task.getNotificationArea())) {

                        if (task.getCanBeRefused()) {
                            StateUtility.changeTaskState(context, task, TaskState.GEO_NOTIFIED_AVAILABLE);
                        } else {
                            AcceptTaskRequest request = new AcceptTaskRequest(context, task.getId());

                            if (!_contentManager.isStarted()) {
                                _contentManager.start(context);
                            }
                            _contentManager.execute(request, new NotificationAcceptMandatoryTaskListener(context, task));
                        }
                    }
                }
            }
        }
    }

    public void handleNewTasksFromService(TaskFlatList result, SpiceManager contentManager){
        for (TaskFlat task : result.getList()) {
            if (!task.getCanBeRefused() && !GeolocalizationTaskUtils.isNotifiedByArea(task)) {

                AcceptTaskRequest request = new AcceptTaskRequest(context, task.getId());

                if (!contentManager.isStarted()) {
                    contentManager.start(context);
                }

                contentManager.execute(request, new NotificationAcceptMandatoryTaskListener(context, task));

            } else if (GeolocalizationTaskUtils.isNotifiedByArea(task)) {
                br.udesc.esag.participactbrasil.domain.persistence.TaskFlat taskDB = StateUtility.getTaskById(context, task.getId());
                if (taskDB == null) {
                    taskDB = StateUtility.addTask(context, task);
                    if (taskDB != null) {
                        // state in hidden
                        StateUtility.changeTaskState(context, taskDB, TaskState.HIDDEN);
                    }
                }

                if (StateUtility.getTaskByState(context, TaskState.HIDDEN).size() > 0) {
                    Location last = LocationService.getLastLocation();
                    if (last != null) {
                        for (br.udesc.esag.participactbrasil.domain.persistence.TaskFlat hiddenTask : StateUtility.getTaskByState(context, TaskState.HIDDEN)) {
                            if (!hiddenTask.getCanBeRefused() && GeolocalizationTaskUtils.isInside(context, last.getLongitude(), last.getLatitude(), hiddenTask.getNotificationArea())) {
                                AcceptTaskRequest request = new AcceptTaskRequest(context, task.getId());

                                if (!contentManager.isStarted()) {
                                    contentManager.start(context);
                                }

                                contentManager.execute(request, new br.udesc.esag.participactbrasil.network.NotificationAcceptMandatoryTaskListener(context, hiddenTask));

                            }
                        }
                    }
                }

            } else {

                Intent resultIntent = new Intent(context, DashboardActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(DashboardActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );


                NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_tasks_notification), GcmBroadcastReceiver.NOTIFICATION_NEW_TASK, resultPendingIntent);

            }
        }
    }
}
