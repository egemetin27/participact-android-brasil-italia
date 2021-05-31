package br.udesc.esag.participactbrasil.support;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import org.most.MoSTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ActionType;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.services.TaskService;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.local.TaskStatus;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.network.request.CompleteTaskListener;
import br.udesc.esag.participactbrasil.network.request.CompleteWithFailureTaskRequest;
import br.udesc.esag.participactbrasil.network.request.CompleteWithSuccessTaskRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;

public class SupportStateUtility {

    private final static Logger logger = LoggerFactory.getLogger(SupportStateUtility.class);

    private static final String FILENAME = "state.raw";
    private static final String FILENAME_TEMP = "temp.raw";

    private static synchronized boolean persistState(Context context, State state) {
        try {
            boolean result = false;
            FileOutputStream fileOutputStream = context.openFileOutput(FILENAME_TEMP, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(state);
            objectOutputStream.close();

            File file = new File(context.getFilesDir(), FILENAME_TEMP);
            if (file.exists()) {
                result = file.renameTo(new File(context.getFilesDir(), FILENAME));
                if (!result) {
                    logger.error("State file not renamed.");
                }
            }

            return result;
        } catch (IOException e) {
            logger.error("Exception persisting state.", e);
            return false;
        }
    }

    public static synchronized boolean deleteState(Context context) {
        try {
            logger.warn("Deleting state file.");
            File file = new File(context.getFilesDir(), FILENAME);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            logger.error("Exception deleting state file.", e);
            return false;
        }
    }

    public static synchronized State loadState(Context context) {
        State result = null;
        try {
            SystemUpgrade.upgrade(context);
            File file = new File(context.getFilesDir(), FILENAME);
            if (file.exists()) {
                FileInputStream fileInputStream = context.openFileInput(FILENAME);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Object obj = objectInputStream.readObject();
                objectInputStream.close();

                if (obj instanceof State) {
                    result = (State) obj;
                }
            }
        } catch (Exception e) {
            logger.error("Exception loading state.", e);
            State state = new State();
            persistState(context, state);
            result = state;
        }
        return result;
    }

    public static synchronized void addTask(Context context, TaskFlat task, TaskState taskState) {
        State state = loadState(context);
        if (state == null) {
            state = new State();
            logger.info("Successfully created state object.");
        }
        state.addTask(task);
        state.changeState(task, taskState);
        persistState(context, state);
        logger.info("Successfully added task with id {} and state {} to local state.", task.getId(), taskState);
    }

    public static synchronized void changeTaskState(Context context, TaskFlat task, TaskState newState) {
        State state = loadState(context);
        if (state == null) {
            state = new State();
            logger.info("Successfully created state object.");
        }
        TaskStatus taskStatus = state.getTaskById(task.getId());
        TaskState oldState = null;
        if (taskStatus != null) {
            oldState = taskStatus.getState();
        }
        state.changeState(task, newState);
        persistState(context, state);
        logger.info("Successfully changed state of task with id {} from {} to {}.", task.getId(), oldState, newState);
    }

    public static synchronized void removeTask(Context context, TaskFlat task) {
        State state = loadState(context);
        if (state == null) {
            logger.warn("Trying to remove task with id {} but state file not exist.", task.getId());
            return;
        }
        state.removeTask(task);
        persistState(context, state);
        logger.info("Successfully removed task with id {} from local state.", task.getId());
    }


    public static synchronized void activateAllTask(Context context, TaskState taskState) {
        State state = loadState(context);
        if (state == null) {
            logger.warn("Trying to activate all task in state {} but state file not exist.", taskState);
            return;
        }
        for (TaskFlat task : state.getTaskByState(taskState)) {
            TaskService.activateTask(context, task);
            state.changeState(task, TaskState.RUNNING);
        }
        persistState(context, state);
        logger.info("Activating all tasks in state {}.", taskState);
    }

    public static synchronized void suspendAllTask(Context context, TaskState taskState) {
        State state = loadState(context);
        if (state == null) {
            logger.warn("Trying to suspend all task in state {} but state file not exist.", taskState);
            return;
        }
        for (TaskFlat task : state.getTaskByState(taskState)) {
            TaskService.suspendTask(context, task);
            state.changeState(task, TaskState.SUSPENDED);
        }
        persistState(context, state);
        logger.info("Deactivating all tasks in state {}.", taskState);
    }

    public static synchronized void freezeAllTask(Context context) {
        State state = loadState(context);
        if (state == null) {
            logger.warn("Trying to freeze all task but state file not exist.");
            return;
        }
        for (TaskFlat task : state.getTaskByState(TaskState.SUSPENDED)) {
            logger.info("Freezing suspended task with id {}.", task.getId());
            state.changeState(task, TaskState.ERROR);
        }
        for (TaskFlat task : state.getTaskByState(TaskState.RUNNING)) {
            logger.info("Freezing running task with id {}.", task.getId());
            TaskService.suspendTask(context, task);
            state.changeState(task, TaskState.ERROR);
        }
        persistState(context, state);
        logger.info("Freezed all tasks.");
    }

    public static synchronized void defreezeAllTask(Context context) {
        State state = loadState(context);
        if (state == null) {
            logger.warn("Trying to defrezee all task but state file not exist.");
            return;
        }

        for (TaskFlat task : state.getTaskByState(TaskState.ERROR)) {
            logger.info("Defreezing running task with id {}.", task.getId());
            TaskService.activateTask(context, task);
            state.changeState(task, TaskState.RUNNING);
        }
        persistState(context, state);
        logger.info("Defreezed all tasks.");
    }

    public static synchronized void incrementSensingProgress(Context context) {
        State state = SupportStateUtility.loadState(context);
        if (state == null) {
            return;
        }
        Long timestamp = System.currentTimeMillis();

        for (TaskStatus task : state.getTaskStatusByState(TaskState.COMPLETED_NOT_SYNC_WITH_SERVER)) {
            completeTask(context, task);
        }

        for (TaskStatus task : state.getTaskStatusByState(TaskState.SUSPENDED)) {
            if (task.isExpired()) {
                AlarmStateUtility.removeAlarm(context.getApplicationContext(), task.getTask().getId());
                completeTask(context, task);
            }
        }

        for (TaskStatus task : state.getTaskStatusByState(TaskState.RUNNING)) {
            task.incrementSensingProgress(timestamp);
            if (task.getProgressSensingPercentual() == 100) {
                stopSensing(context, task);
            }
            if (task.isExpired()) {
                completeTask(context, task);
            }
        }
        persistState(context, state);
    }

    public static synchronized void stopSensing(Context context, TaskStatus task) {
        Log.d("SupportStateUtility", "stopSensing");
        for(ActionFlat action:task.getTask().getSensingActions()){
            Intent i = new Intent(context, MoSTService.class);
            i.setAction(MoSTService.START);
            i.putExtra(MoSTService.KEY_PIPELINE_TYPE, action.getInput_type());
            context.startService(i);
        }
    }

    public static synchronized void incrementPhotoProgress(Context context, TaskFlat task, Long actionId) {
        State state = SupportStateUtility.loadState(context);
        if (state == null) {
            return;
        }
        TaskStatus status = state.getTaskById(task.getId());
        if (status.getState() == TaskState.RUNNING) {
            status.incrementPhotoProgress(actionId);
            logger.info("Incremented photo progress of task with id {} and action id {}", task.getId(), actionId);
            if (status.isExpired()) {
                completeTask(context, status);
            }
        }
        persistState(context, state);
    }

    public static synchronized void incrementPhotoProgress(Context context, Long taskId, Long actionId) {
        State state = SupportStateUtility.loadState(context);
        if (state == null) {
            return;
        }
        TaskStatus status = state.getTaskById(taskId);
        if (status.getState() == TaskState.RUNNING) {
            status.incrementPhotoProgress(actionId);
            logger.info("Incremented photo progress of task with id {} and action id {}", taskId, actionId);
            if (status.isExpired()) {
                completeTask(context, status);
            }
        }
        persistState(context, state);
    }

    public static synchronized void incrementQuestionnaireProgress(Context context, TaskFlat task, Long actionId) {
        State state = SupportStateUtility.loadState(context);
        if (state == null) {
            return;
        }
        TaskStatus status = state.getTaskById(task.getId());
        if (status.getState() == TaskState.RUNNING) {
            status.incrementQuestionnaireProgress(actionId);
            logger.info("Incremented questionnaire progress of task with id {} and action id {}", task.getId(), actionId);
            if (status.isExpired()) {
                completeTask(context, status);
            }
        }
        persistState(context, state);
    }

    public static synchronized void incrementQuestionnaireProgress(Context context, Long taskId, Long actionId) {
        State state = SupportStateUtility.loadState(context);
        if (state == null) {
            return;
        }
        TaskStatus status = state.getTaskById(taskId);
        if (status.getState() == TaskState.RUNNING) {
            status.incrementQuestionnaireProgress(actionId);
            logger.info("Incremented questionnaire progress of task with id {} and action id {}", taskId, actionId);
            if (status.isExpired()) {
                completeTask(context, status);
            }
        }
        persistState(context, state);
    }

    private static void completeTask(Context context, TaskStatus status) {
        logger.info("Trying to complete task with id {}.", status.getTask().getId());
        logger.info("Task accept time + task duration = {} + {}.", status.getAcceptedTime(), status.getTask().getDuration());
        logger.info("Task progress: sensing progress={}, task photo progress={}, task questionnaire progress={}", status.getSensingProgress(), status.getPhotoProgress(), status.getQuestionnaireProgress());

        if (status.getState() != TaskState.COMPLETED_NOT_SYNC_WITH_SERVER) {
            //stop sensing
            if (status.getState() == TaskState.RUNNING) {
                TaskService.suspendTask(context, status.getTask());
                logger.info("Suspended task with id {}.", status.getTask().getId());
            }
            status.setState(TaskState.COMPLETED_NOT_SYNC_WITH_SERVER);
        }

        //send new state at server
        SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
        if (!contentManager.isStarted()) {
            contentManager.start(context.getApplicationContext());
        }

        if (status.isCompleted()) {
            logger.info("Sending final task state of task with id {}. Result success.", status.getTask().getId());
            CompleteWithSuccessTaskRequest request = new CompleteWithSuccessTaskRequest(context, status.getTask().getId());
            contentManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new CompleteTaskListener(context, status.getTask()));
        } else {
            logger.info("Sending final task state of task with id {}. Result unsuccess.", status.getTask().getId());
            CompleteWithFailureTaskRequest request = new CompleteWithFailureTaskRequest(context, status);
            contentManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new CompleteTaskListener(context, status.getTask()));
        }
    }

}
