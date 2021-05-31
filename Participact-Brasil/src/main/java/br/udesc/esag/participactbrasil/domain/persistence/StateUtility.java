package br.udesc.esag.participactbrasil.domain.persistence;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import org.joda.time.DateTime;
import org.most.MoSTApplication;
import org.most.MoSTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.broadcastreceivers.GcmBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.data.DataQuestionaireClosedAnswer;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.local.ImageDescriptor;
import br.udesc.esag.participactbrasil.domain.persistence.support.DomainDBHelper;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;
import br.udesc.esag.participactbrasil.network.CompleteTaskListener;
import br.udesc.esag.participactbrasil.network.CompleteWithFailureTaskRequest;
import br.udesc.esag.participactbrasil.network.request.CompleteTaskWithFailureListener;
import br.udesc.esag.participactbrasil.network.request.CompleteWithSuccessTaskRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.AlarmStateUtility;
import br.udesc.esag.participactbrasil.support.DataUploader;
import br.udesc.esag.participactbrasil.support.ImageDescriptorUtility;
import br.udesc.esag.participactbrasil.support.NotificationUtility;
import br.udesc.esag.participactbrasil.support.SystemUpgrade;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderPhotoPreferences;

public class StateUtility {

    private final static Logger logger = LoggerFactory.getLogger(StateUtility.class);
    private static final String TAG = StateUtility.class.getSimpleName();

    public static synchronized State loadState(Context context) {
        State result = null;
        try {

            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[3];//maybe this number needs to be corrected String methodName = e.getMethodName();

            logger.debug("Called by {} {}", e.getClassName(), e.getMethodName());

            SystemUpgrade.upgrade(context);

            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> dao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);
            List<TaskStatus> list = dao.queryForAll();
            result = new State(list);

        } catch (Exception e) {
            logger.error("Exception loading state.", e);
        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized void convertTaskStatus(Context context, br.udesc.esag.participactbrasil.domain.local.TaskStatus task) {
        try {

            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskFlat, Long> taskFlatDao = databaseHelper.getRuntimeExceptionDao(TaskFlat.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);
            RuntimeExceptionDao<ActionFlat, Long> actionDao = databaseHelper.getRuntimeExceptionDao(ActionFlat.class);
            RuntimeExceptionDao<RemainingPhotoPerAction, Long> remainPhotoDao = databaseHelper.getRuntimeExceptionDao(RemainingPhotoPerAction.class);
            RuntimeExceptionDao<QuestionnaireProgressPerAction, Long> questProgressDao = databaseHelper.getRuntimeExceptionDao(QuestionnaireProgressPerAction.class);
            RuntimeExceptionDao<Question, Long> questionDao = databaseHelper.getRuntimeExceptionDao(Question.class);
            RuntimeExceptionDao<ClosedAnswer, Long> closedAnswerDao = databaseHelper.getRuntimeExceptionDao(ClosedAnswer.class);


            TaskFlat taskFlat = new TaskFlat();
            taskFlat.setId(task.getTask().getId());
            taskFlat.setCanBeRefused(task.getTask().getCanBeRefused());
            taskFlat.setDeadline(task.getTask().getDeadline());
            taskFlat.setDescription(task.getTask().getDescription());
            taskFlat.setDuration(task.getTask().getDuration());
            taskFlat.setLatitude(task.getTask().getLatitude());
            taskFlat.setLongitude(task.getTask().getLongitude());
            taskFlat.setName(task.getTask().getName());
            taskFlat.setPoints(task.getTask().getPoints());
            taskFlat.setRadius(task.getTask().getRadius());
            taskFlat.setSensingDuration(task.getTask().getSensingDuration());
            taskFlat.setStart(task.getTask().getStart());
            taskFlat.setType(task.getTask().getType());

            taskFlatDao.createIfNotExists(taskFlat);

            for (br.udesc.esag.participactbrasil.domain.persistence.ActionFlat actionOld : task.getTask().getActionsDB()) {

                ActionFlat action = new ActionFlat();
                action.setDescription(actionOld.getDescription());
                action.setDuration_threshold(actionOld.getDuration_threshold());
                action.setId(actionOld.getId());
                action.setInput_type(actionOld.getInput_type());
                action.setName(actionOld.getName());
                action.setNumeric_threshold(actionOld.getNumeric_threshold());
                action.setTitle(actionOld.getTitle());
                action.setType(ActionType.convertFrom(actionOld.getType()));

                action.setTask(taskFlat);
                actionDao.createIfNotExists(action);

                if (actionOld.getQuestionsDB() != null) {
                    for (br.udesc.esag.participactbrasil.domain.persistence.Question questionOld : actionOld.getQuestionsDB()) {
                        Question question = new Question();
                        question.setId(questionOld.getId());
                        question.setIsClosedAnswers(questionOld.getIsClosedAnswers());
                        question.setIsMultipleAnswers(questionOld.getIsMultipleAnswers());
                        question.setQuestion(questionOld.getQuestion());
                        question.setQuestionOrder(questionOld.getQuestionOrder());

                        question.setActionFlat(action);
                        questionDao.createIfNotExists(question);

                        for (br.udesc.esag.participactbrasil.domain.persistence.ClosedAnswer answerOld : questionOld.getClosed_answersDB()) {
                            ClosedAnswer answer = new ClosedAnswer();
                            answer.setAnswerDescription(answerOld.getAnswerDescription());
                            answer.setAnswerOrder(answerOld.getAnswerOrder());
                            answer.setId(answerOld.getId());

                            answer.setQuestion(question);
                            closedAnswerDao.createIfNotExists(answer);
                        }
                    }
                }
            }

            taskFlatDao.refresh(taskFlat);

            TaskStatus taskStatus = new TaskStatus();
            taskStatus.setId(taskFlat.getId());
            taskStatus.setAcceptedTime(task.getAcceptedTime());
            taskStatus.setLastCheckedTimestamp(task.getLastCheckedTimestamp());
            taskStatus.setPhotoProgress(task.getPhotoProgress());
            taskStatus.setQuestionnaireProgress(task.getQuestionnaireProgress());
            taskStatus.setSensingProgress(task.getSensingProgress());
            taskStatus.setState(task.getState());
            taskStatus.setPhotoThreshold(task.getPhotoThreshold());
            taskStatus.setQuestionnaireThreshold(task.getQuestionnaireThreshold());

            taskStatus.setTask(taskFlat);
            taskStatusdao.createIfNotExists(taskStatus);

            for (Entry<Long, Boolean> entry : task.getQuestionnaireProgressPerAction().entrySet()) {
                QuestionnaireProgressPerAction questProgress = new QuestionnaireProgressPerAction();
                questProgress.setDone(entry.getValue());
                ActionFlat action = actionDao.queryForId(entry.getKey());

                questProgress.setAction(action);
                questProgress.setTaskStatus(taskStatus);

                questProgressDao.createIfNotExists(questProgress);
            }

            for (Entry<Long, Integer> entry : task.getRemainingPhotoPerAction().entrySet()) {

                RemainingPhotoPerAction remPhoto = new RemainingPhotoPerAction();
                remPhoto.setRemaingPhoto(entry.getValue());

                ActionFlat action = actionDao.queryForId(entry.getKey());
                remPhoto.setAction(action);
                remPhoto.setTaskStatus(taskStatus);

                remainPhotoDao.createIfNotExists(remPhoto);
            }

            taskStatusdao.refresh(taskStatus);
            taskStatusdao.update(taskStatus);

            logger.info("Successfully converted task with id {} and state {} to new db format state.", task.getTask().getId());
        } finally {
            OpenHelperManager.releaseHelper();
        }

    }

    public static synchronized void addTaskList(Context context, TaskFlatList taskList) {
        for (TaskFlat task : taskList.getList()) {
            try {
                addTask(context, task);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static synchronized TaskFlat addTask(Context context, TaskFlat taskFlat) {
        try {

            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskFlat, Long> taskFlatDao = databaseHelper.getRuntimeExceptionDao(TaskFlat.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);
            RuntimeExceptionDao<ActionFlat, Long> actionDao = databaseHelper.getRuntimeExceptionDao(ActionFlat.class);
            RuntimeExceptionDao<RemainingPhotoPerAction, Long> remainPhotoDao = databaseHelper.getRuntimeExceptionDao(RemainingPhotoPerAction.class);
            RuntimeExceptionDao<QuestionnaireProgressPerAction, Long> questProgressDao = databaseHelper.getRuntimeExceptionDao(QuestionnaireProgressPerAction.class);
            RuntimeExceptionDao<Question, Long> questionDao = databaseHelper.getRuntimeExceptionDao(Question.class);
            RuntimeExceptionDao<ClosedAnswer, Long> closedAnswerDao = databaseHelper.getRuntimeExceptionDao(ClosedAnswer.class);

            if (taskFlatDao.idExists(taskFlat.getId())) {
                return taskFlatDao.queryForId(taskFlat.getId());
            }


            taskFlatDao.createIfNotExists(taskFlat);

            for (ActionFlat actionflat : taskFlat.getActions()) {
                actionflat.setTask(taskFlat);
                actionDao.createIfNotExists(actionflat);

                if (actionflat.getQuestions() != null) {
                    for (Question questionOld : actionflat.getQuestions()) {
                        Question question = new Question();
                        question.setId(questionOld.getId());
                        question.setIsClosedAnswers(questionOld.getIsClosedAnswers());
                        question.setIsMultipleAnswers(questionOld.getIsMultipleAnswers());
                        question.setQuestion(questionOld.getQuestion());
                        question.setQuestionOrder(questionOld.getQuestionOrder());

                        question.setActionFlat(actionflat);
                        questionDao.createIfNotExists(question);

                        for (ClosedAnswer answerOld : questionOld.getClosed_answers()) {
                            ClosedAnswer answer = new ClosedAnswer();
                            answer.setAnswerDescription(answerOld.getAnswerDescription());
                            answer.setAnswerOrder(answerOld.getAnswerOrder());
                            answer.setId(answerOld.getId());

                            answer.setQuestion(question);
                            closedAnswerDao.createIfNotExists(answer);
                        }
                    }
                }
            }

            taskFlatDao.refresh(taskFlat);

            TaskStatus taskStatus = new TaskStatus();
            taskStatus.setId(taskFlat.getId());
            taskStatus.setState(TaskState.AVAILABLE);

            taskStatus.setTask(taskFlat);
            taskStatusdao.createIfNotExists(taskStatus);
            taskStatusdao.refresh(taskStatus);

            for (ActionFlat action : taskFlat.getActions()) {
                if (action.getType() == ActionType.PHOTO) {
//					photoThreshold += action.getNumeric_threshold();
//					remainingPhotoPerAction.put(action.getId(), action.getNumeric_threshold());

                    taskStatus.photoThreshold = action.getNumeric_threshold();
                    RemainingPhotoPerAction remPhoto = new RemainingPhotoPerAction();
                    remPhoto.setRemaingPhoto(action.getNumeric_threshold());
                    ActionFlat actionFlat = actionDao.queryForId(action.getId());
                    remPhoto.setAction(actionFlat);
                    remPhoto.setTaskStatus(taskStatus);

                    remainPhotoDao.createIfNotExists(remPhoto);
                    taskStatusdao.update(taskStatus);
                }

                if (action.getType() == ActionType.QUESTIONNAIRE) {
//					questionnaireThreshold ++;
//					questionnaireProgressPerAction.put(action.getId(), false);
                    taskStatus.questionnaireThreshold++;
                    QuestionnaireProgressPerAction questProgress = new QuestionnaireProgressPerAction();
                    questProgress.setDone(false);
                    ActionFlat actionFlat = actionDao.queryForId(action.getId());
                    questProgress.setAction(actionFlat);
                    questProgress.setTaskStatus(taskStatus);

                    questProgressDao.createIfNotExists(questProgress);
                    taskStatusdao.update(taskStatus);
                }

                if (action.getType() == ActionType.ACTIVITY_DETECTION) {
                    taskStatus.setActivityDetectionDuration(action.getDuration_threshold());
                    taskStatusdao.update(taskStatus);
                }
            }

            taskStatusdao.update(taskStatus);
            taskFlatDao.update(taskFlat);

            logger.info("Successfully added task with id {} in db.", taskFlat.getId());

            return taskFlat;
        } catch (Exception e) {
            logger.warn("Exception adding task with id {} in db.", taskFlat.getId());
            return null;
        } finally {
            OpenHelperManager.releaseHelper();
        }

    }

    public static synchronized TaskFlat getTaskById(Context context, Long id) {
        try {

            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskFlat, Long> taskFlatDao = databaseHelper.getRuntimeExceptionDao(TaskFlat.class);
            return taskFlatDao.queryForId(id);

        } catch (Exception e) {
            logger.warn("Exception getting task with id {} in db.", id);
            return null;
        } finally {
            OpenHelperManager.releaseHelper();
        }
    }


    public static synchronized void changeTaskState(Context context, TaskFlat task, TaskState newState) {
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (TaskStatus t : taskStatusdao.queryForAll()) {
                if (t.getTask().getId().equals(task.getId())) {
                    TaskState oldState = t.getState();
                    if (t.getAcceptedTime() == null && (newState == TaskState.RUNNING || newState == TaskState.RUNNING_BUT_NOT_EXEC)) {
                        t.setAcceptedTime(new DateTime());
                    }
                    transactionToState(context, task, oldState, newState);
                    t.setState(newState);
                    taskStatusdao.update(t);
                    task.setTaskStatus(t);
                    logger.info("Successfully changed state of task with id {} from {} to {}.", task.getId(), oldState, newState);
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    public static void transactionToState(Context context, TaskFlat task, TaskState oldState,
                                          TaskState newState) {

        if (oldState == newState) {
            return;
        }

        switch (newState) {
            case RUNNING:
                TaskService.activateTask(context, task);
                break;
            case RUNNING_BUT_NOT_EXEC:
                if (oldState == TaskState.RUNNING) {
                    TaskService.suspendTask(context, task);
                }
                break;
            case SUSPENDED:
                TaskService.suspendTask(context, task);
                break;
            case ERROR:
                if (oldState == TaskState.RUNNING) {
                    TaskService.suspendTask(context, task);
                }
                break;
            case COMPLETED_NOT_SYNC_WITH_SERVER:
                if (oldState == TaskState.RUNNING) {
                    TaskService.suspendTask(context, task);
                }
                break;
            case GEO_NOTIFIED_AVAILABLE:
                if (oldState == TaskState.HIDDEN) {
                    NotificationUtility.addNotification(context, R.drawable.ic_new_task, context.getString(R.string.participact_notification), context.getString(R.string.new_tasks_notification), GcmBroadcastReceiver.NOTIFICATION_NEW_TASK);
                }
                break;

            default:
                break;
        }

        ParticipActApplication.getInstance().dispatchMessage(MessageType.TASK_UPDATED);
    }

    public static synchronized void removeTask(Context context, TaskFlat task) {
        try {

            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskFlat, Long> taskFlatDao = databaseHelper.getRuntimeExceptionDao(TaskFlat.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);
            RuntimeExceptionDao<ActionFlat, Long> actionDao = databaseHelper.getRuntimeExceptionDao(ActionFlat.class);
            RuntimeExceptionDao<RemainingPhotoPerAction, Long> remainPhotoDao = databaseHelper.getRuntimeExceptionDao(RemainingPhotoPerAction.class);
            RuntimeExceptionDao<QuestionnaireProgressPerAction, Long> questProgressDao = databaseHelper.getRuntimeExceptionDao(QuestionnaireProgressPerAction.class);

            for (TaskStatus t : taskStatusdao.queryForAll()) {
                if (t.getTask().getId().equals(task.getId())) {

                    remainPhotoDao.delete(t.getRemainingPhotoPerAction());
                    questProgressDao.delete(t.getQuestionnaireProgressPerAction());
                    actionDao.delete(task.getActionsDB());
                    taskStatusdao.delete(t);
                    taskFlatDao.delete(task);

                    logger.info("Successfully removed task with id {} from local state.", task.getId());
                    return;
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    public static synchronized List<ActionFlat> getAllOpenSensorsActions(Context context) {

        List<ActionFlat> result = new ArrayList<>();
        try {

            List<TaskFlat> taskFlatList = getTaskListByState(context,TaskState.RUNNING);
            for(TaskFlat taskFlat:taskFlatList){
                for(ActionFlat actionFlat:taskFlat.getSensingActions()){
                    if(actionFlat.getType().equals(ActionType.SENSING_MOST)
                            && actionFlat.getTask().getTaskStatus().getProgressSensingPercentual() < 100) {
                        result.add(actionFlat);
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<ActionFlat> getDoneSensorsActions(Context context) {

        List<ActionFlat> result = new ArrayList<>();
        try {
            List<TaskFlat> taskFlatList = getAllTasks(context);

            for(TaskFlat taskFlat:taskFlatList){

                Log.d(TAG, taskFlat.getTaskStatus().getState().toString());

                if (taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_NOT_SYNC_WITH_SERVER) ||
                        taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_WITH_SUCCESS) ||
                        taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_WITH_UNSUCCESS)) {
                    result.addAll(taskFlat.getSensingActions());
                } else {
                    for(ActionFlat actionFlat:taskFlat.getSensingActions()){
                        if(actionFlat.getType().equals(ActionType.SENSING_MOST) && actionFlat.getTask().getTaskStatus().getProgressSensingPercentual() == 100) {
                            result.add(actionFlat);
                        }
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<ActionFlat> getAllOpenDirectActions(Context context) {

        List<ActionFlat> result = new ArrayList<>();
        try {

            List<TaskFlat> taskFlatList = getTaskListByState(context,TaskState.RUNNING);
            for(TaskFlat taskFlat:taskFlatList){
                for(ActionFlat actionFlat:taskFlat.getDirectActions()){
                    if(actionFlat.getType().equals(ActionType.PHOTO)
                            && actionFlat.getTask().getTaskStatus().getRemainingPhotoPerAction(actionFlat.getId()) > 0) {
                        result.add(actionFlat);
                    }else if(actionFlat.getType().equals(ActionType.QUESTIONNAIRE)
                            && !actionFlat.getTask().getTaskStatus().isQuestionnaireCompleted(actionFlat.getId())){
                        result.add(actionFlat);
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<ActionFlat> getAllDirectActions(Context context) {

        List<ActionFlat> result = new ArrayList<>();
        try {
            List<TaskFlat> taskFlatList = getAllTasks(context);

            for(TaskFlat taskFlat:taskFlatList){
                result.addAll(taskFlat.getDirectActions());
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<ActionFlat> getDoneDirectActions(Context context) {

        List<ActionFlat> result = new ArrayList<>();
        try {
            List<TaskFlat> taskFlatList = getAllTasks(context);

            for(TaskFlat taskFlat:taskFlatList){

                Log.d(TAG, taskFlat.getTaskStatus().getState().toString());

                if (taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_NOT_SYNC_WITH_SERVER) ||
                        taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_WITH_SUCCESS) ||
                        taskFlat.getTaskStatus().getState().equals(TaskState.COMPLETED_WITH_UNSUCCESS)) {
                    result.addAll(taskFlat.getDirectActions());
                } else {
                    for(ActionFlat actionFlat:taskFlat.getDirectActions()){
                        if(actionFlat.getType().equals(ActionType.PHOTO)
                                && actionFlat.getTask().getTaskStatus().getRemainingPhotoPerAction(actionFlat.getId()) == 0) {
                            result.add(actionFlat);
                        }else if(actionFlat.getType().equals(ActionType.QUESTIONNAIRE)
                                && actionFlat.getTask().getTaskStatus().isQuestionnaireCompleted(actionFlat.getId())){
                            result.add(actionFlat);
                        }
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<TaskFlat> getTaskListByState(Context context, TaskState taskState) {

        List<TaskFlat> result = new ArrayList<>();
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (TaskStatus t : taskStatusdao.queryForEq("state", taskState)) {
                TaskFlat taskFlat = t.getTask();
                taskFlat.setTaskStatus(t);
                Log.d(TAG, "" + taskFlat.getStart());
                for (ActionFlat actionFlat : taskFlat.getActionsDB()) {
                    List<Question> questions = new ArrayList<>();
                    for (Question question : actionFlat.getQuestionsDB()) {
                        questions.add(question);
                    }
                    actionFlat.setQuestions(questions);
                }
                if (taskState != TaskState.AVAILABLE || (taskState == TaskState.AVAILABLE && taskFlat.getCanBeRefused()))
                    result.add(taskFlat);
                if (t.isExpired()) {
                    completeTask(context, t);
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<TaskFlat> getAllTasks(Context context) {

        List<TaskFlat> result = new ArrayList<>();
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (TaskStatus t : taskStatusdao.queryForAll()) {
                TaskFlat taskFlat = t.getTask();
                taskFlat.setTaskStatus(t);
                for (ActionFlat actionFlat : taskFlat.getActionsDB()) {
                    List<Question> questions = new ArrayList<>();
                    for (Question question : actionFlat.getQuestionsDB()) {
                        questions.add(question);
                    }
                    actionFlat.setQuestions(questions);
                }
                result.add(taskFlat);
                if (t.isExpired()) {
                    completeTask(context, t);
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<TaskFlat> getTaskByState(Context context, TaskState taskState) {

        List<TaskFlat> result = new ArrayList<>();
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (TaskStatus t : taskStatusdao.queryForEq("state", taskState)) {
                result.add(t.getTask());
                if (t.isExpired()) {
                    completeTask(context, t);
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized List<TaskStatus> getTaskStatusByState(Context context, TaskState taskState) {

        List<TaskStatus> result = new ArrayList<>();
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            result = taskStatusdao.queryForEq("state", taskState);

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return result;
    }

    public static synchronized TaskStatus getTaskStatus(Context context, TaskFlat task) {

        TaskStatus taskStatus = new TaskStatus();
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (TaskStatus taskStatusAux : taskStatusdao.queryForEq("task_id", task.getId())) {
                taskStatus = taskStatusAux;

                TaskFlat taskFlat = taskStatus.getTask();
                taskFlat.setTaskStatus(taskStatus);

                for (ActionFlat actionFlat : taskFlat.getActionsDB()) {
                    List<Question> questions = new ArrayList<>();
                    for (Question question : actionFlat.getQuestionsDB()) {
                        questions.add(question);
                    }
                    actionFlat.setQuestions(questions);
                }
                break;

            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
        return taskStatus;
    }


    public static synchronized void activateAllTask(Context context, TaskState taskState) {

        for (TaskFlat task : getTaskByState(context, taskState)) {
            changeTaskState(context, task, TaskState.RUNNING);
        }
        logger.info("Activating all tasks in state {}.", taskState);
    }

    public static synchronized void suspendAllTask(Context context, TaskState taskState) {

        for (TaskFlat task : getTaskByState(context, taskState)) {
            changeTaskState(context, task, TaskState.SUSPENDED);
        }
        logger.info("Deactivating all tasks in state {}.", taskState);
    }

    public static synchronized void freezeAllTask(Context context) {

        for (TaskFlat task : getTaskByState(context, TaskState.SUSPENDED)) {
            logger.info("Freezing suspended task with id {}.", task.getId());
            changeTaskState(context, task, TaskState.ERROR);
        }
        for (TaskFlat task : getTaskByState(context, TaskState.RUNNING)) {
            logger.info("Freezing running task with id {}.", task.getId());
            changeTaskState(context, task, TaskState.ERROR);
        }
        logger.info("Freezed all tasks.");
    }

    public static synchronized void defreezeAllTask(Context context) {

        for (TaskFlat task : getTaskByState(context, TaskState.ERROR)) {
            logger.info("Defreezing running task with id {}.", task.getId());
            changeTaskState(context, task, TaskState.RUNNING);
        }
        logger.info("Defreezed all tasks.");
    }

    public static synchronized void incrementSensingProgress(Context context) {
        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);
            Long timestamp = System.currentTimeMillis();

            for (TaskStatus task : getTaskStatusByState(context, TaskState.COMPLETED_NOT_SYNC_WITH_SERVER)) {
                completeTask(context, task);
            }

            for (TaskStatus task : getTaskStatusByState(context, TaskState.RUNNING_BUT_NOT_EXEC)) {
                if (task.isExpired()) {
                    completeTask(context, task);
                }
            }

            for (TaskStatus task : getTaskStatusByState(context, TaskState.HIDDEN)) {
                if (task.getTask().getDeadline().isBefore(new DateTime())) {
                    removeTask(context, task.getTask());
                }
            }

            for (TaskStatus task : getTaskStatusByState(context, TaskState.GEO_NOTIFIED_AVAILABLE)) {
                if (task.getTask().getDeadline().isBefore(new DateTime())) {
                    removeTask(context, task.getTask());
                }
            }

            for (TaskStatus task : getTaskStatusByState(context, TaskState.SUSPENDED)) {
                if (task.isExpired()) {
                    AlarmStateUtility.removeAlarm(context.getApplicationContext(), task.getTask().getId());
                    completeTask(context, task);
                }
            }

            for (TaskStatus task : getTaskStatusByState(context, TaskState.RUNNING)) {
                task.incrementSensingProgress(timestamp);
                if (task.getProgressSensingPercentual() == 100) {
                    stopSensing(context, task);
                }
                taskStatusdao.update(task);

                if (task.isExpired()) {
                    completeTask(context, task);
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    public static synchronized void stopSensing(Context context, TaskStatus task) {
        Log.d("StateUtility", "stopSensing");
        for(ActionFlat action:task.getTask().getSensingActions()){
            if (MoSTApplication.getInstance().isPipelineActive(action.getInput_type())) {
                Intent i = new Intent(context, MoSTService.class);
                i.setAction(MoSTService.STOP);
                i.putExtra(MoSTService.KEY_PIPELINE_TYPE, action.getInput_type());
                context.startService(i);
            }
        }
    }


    public static synchronized void incrementPhotoProgress(Context context, TaskFlat task, Long actionId) {
        incrementPhotoProgress(context, task.getId(), actionId);
    }

    public static synchronized void incrementPhotoProgress(Context context, Long taskId, Long actionId) {

        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<RemainingPhotoPerAction, Long> remainPhotoDao = databaseHelper.getRuntimeExceptionDao(RemainingPhotoPerAction.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (RemainingPhotoPerAction r : remainPhotoDao.queryForAll()) {
                TaskStatus taskStatus = r.getTaskStatus();
                taskStatusdao.refresh(taskStatus);
                remainPhotoDao.refresh(r);
                if (r.getTaskStatus().getTask().getId().equals(taskId) && r.getAction().getId().equals(actionId)) {

                    if (r.getTaskStatus().getState() == TaskState.RUNNING) {
                        r.remaingPhoto--;
                        r.getTaskStatus().photoProgress++;
                        logger.info("Incremented photo progress of task with id {} and action id {}", taskId, actionId);
                        remainPhotoDao.update(r);
                        taskStatusdao.update(r.getTaskStatus());
                    } else {
                        //delete photo
                        //TODO review need for this
                        File[] files = ImageDescriptorUtility.getImageDescriptors(context);
                        for (File file : files) {
                            ImageDescriptor imgDescr = ImageDescriptorUtility.loadImageDescriptor(context, file.getName());
                            if (imgDescr.getTaskId().equals(taskId) && imgDescr.getActionId().equals(actionId)) {
                                ImageDescriptorUtility.deleteImageDescriptorAndRelatedImage(context, imgDescr.getImageName());
                                logger.info("Deleted photo of task with id {} and action id {} because taken not in activation area.", taskId, actionId);
                            }
                        }
                    }

                    if (r.getTaskStatus().isExpired()) {
                        completeTask(context, r.getTaskStatus());
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
            DataUploaderPhotoPreferences.getInstance(context).setPhotoUpload(true);
            DataUploader.getInstance(context).uploadOverWifi();
        }

    }

    public static synchronized void incrementQuestionnaireProgress(Context context, TaskFlat task, Long actionId) {
        incrementQuestionnaireProgress(context, task.getId(), actionId);
    }

    public static synchronized void incrementQuestionnaireProgress(Context context, Long taskId, Long actionId) {

        try {
            DomainDBHelper databaseHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
            RuntimeExceptionDao<QuestionnaireProgressPerAction, Long> questProgressDao = databaseHelper.getRuntimeExceptionDao(QuestionnaireProgressPerAction.class);
            RuntimeExceptionDao<TaskStatus, Long> taskStatusdao = databaseHelper.getRuntimeExceptionDao(TaskStatus.class);

            for (QuestionnaireProgressPerAction q : questProgressDao.queryForAll()) {
                if (q.getTaskStatus().getTask().getId().equals(taskId) && q.getAction().getId().equals(actionId)) {

                    q.setDone(true);
                    q.getTaskStatus().questionnaireProgress++;
                    logger.info("Incremented questionnaire progress of task with id {} and action id {}", taskId, actionId);
                    questProgressDao.update(q);
                    taskStatusdao.update(q.getTaskStatus());

                    if (q.getTaskStatus().isExpired()) {
                        completeTask(context, q.getTaskStatus());
                    }
                }
            }

        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    public static void completeTask(Context context, TaskStatus status) {
        try {
            logger.info("Trying to complete task with id {}.", status.getTask().getId());
            logger.info("Task accept time + task duration = {} + {}.", status.getAcceptedTime(), status.getTask().getDuration());
            logger.info("Task progress: sensing progress={}, task photo progress={}, task questionnaire progress={}", status.getSensingProgress(), status.getPhotoProgress(), status.getQuestionnaireProgress());

            if (status.getState() == TaskState.COMPLETED_WITH_SUCCESS || status.getState() == TaskState.COMPLETED_WITH_UNSUCCESS) {
                return;
            }

            if (status.getState() != TaskState.COMPLETED_NOT_SYNC_WITH_SERVER) {
                changeTaskState(context, status.getTask(), TaskState.COMPLETED_NOT_SYNC_WITH_SERVER);
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
                contentManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new CompleteTaskWithFailureListener(context, status.getTask()));
            }

        } catch (Exception e) {
            logger.warn("Exception completing task with id {}.", status.getTask().getId());
        }
    }

    public static synchronized List<DataQuestionnaireFlat> getAnswerForQuestion(Context context, Question question){
        DomainDBHelper dbHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
        RuntimeExceptionDao<DataQuestionnaireFlat, Long> dao = dbHelper.getRuntimeExceptionDao(DataQuestionnaireFlat.class);
        List<DataQuestionnaireFlat> listAnswers = new ArrayList<>();
        for(DataQuestionnaireFlat dataQuestionnaireFlat:dao.queryForAll()){
            if(dataQuestionnaireFlat.getQuestionId().equals(question.getId())) {
                listAnswers.add(dataQuestionnaireFlat);
            }
        }
        return listAnswers;
    }

    public static synchronized List<DataQuestionnaireFlat> getAnswerForAction(Context context, ActionFlat actionFlat){
        DomainDBHelper dbHelper = OpenHelperManager.getHelper(context, DomainDBHelper.class);
        RuntimeExceptionDao<DataQuestionnaireFlat, Long> dao = dbHelper.getRuntimeExceptionDao(DataQuestionnaireFlat.class);
        List<DataQuestionnaireFlat> listAnswers = new ArrayList<>();
        for(DataQuestionnaireFlat dataQuestionnaireFlat:dao.queryForAll()){
            if(dataQuestionnaireFlat.getActionId().equals(actionFlat.getId())) {
                listAnswers.add(dataQuestionnaireFlat);
            }
        }
        return listAnswers;
    }

    public static synchronized void clearDataBase(Context context){
        for(TaskFlat taskFlat : getAllTasks(context)){
            removeTask(context,taskFlat);
        }
    }



}
