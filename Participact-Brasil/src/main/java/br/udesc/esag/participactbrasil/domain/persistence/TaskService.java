package br.udesc.esag.participactbrasil.domain.persistence;

import android.content.Context;
import android.content.Intent;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.domain.enums.SensingActionEnum;
import br.udesc.esag.participactbrasil.domain.persistence.support.State;


public class TaskService {

    private final static Logger logger = LoggerFactory.getLogger(TaskService.class);

    public static void activateTask(Context context, TaskFlat task) {
        State state = StateUtility.loadState(context);
        if (state.getTaskById(task.getId()) != null) {

            logger.info("Activating Task with id {} to MoST.", task.getId());

            for (ActionFlat action : task.getActionsDB()) {
                switch (action.getType()) {
                    case ACTIVITY_DETECTION: // bergmann - this case was not being handled
                        // input type is -1, so it won't be handled
                    case SENSING_MOST:
                        TaskStatus status = StateUtility.getTaskStatus(context, task);
                        if (Pipeline.Type.fromInt(action.getInput_type()) != Pipeline.Type.DUMMY && status.getProgressSensingPercentual() < 100) {
                            Intent i = new Intent(context, MoSTService.class);
                            i.setAction(MoSTService.START);
                            i.putExtra(MoSTService.KEY_PIPELINE_TYPE, action.getInput_type());
                            context.startService(i);
                            logger.info("Sending start intent of pipeline {} to MoST.",
                                    SensingActionEnum.Type.fromIntToHumanReadable(action.getInput_type().intValue()));
                        }
                        break;
                    case PHOTO:

                        break;
                    case QUESTIONNAIRE:

                        break;

                    default:
                        break;
                }
            }
        }
    }

    public static void suspendTask(Context context, TaskFlat task) {
        // At the moment only sensing tasks can be suspended
        State state = StateUtility.loadState(context);
        if (state.getTaskById(task.getId()) != null) {
            logger.info("Suspending Task with id {} to MoST.", task.getId());
            for (ActionFlat action : task.getActionsDB()) {
                switch (action.getType()) {
                    case SENSING_MOST:
                        if (Pipeline.Type.fromInt(action.getInput_type()) != Pipeline.Type.DUMMY) {
                            Intent i = new Intent(context, MoSTService.class);
                            i.setAction(MoSTService.STOP);
                            i.putExtra(MoSTService.KEY_PIPELINE_TYPE, action.getInput_type());
                            context.startService(i);
                            logger.info("Sending stop intent of pipeline {} to MoST.",
                                    SensingActionEnum.Type.fromIntToHumanReadable(action.getInput_type().intValue()));
                        }
                        break;

                    case ACTIVITY_DETECTION:
                        Intent i = new Intent(context, MoSTService.class);
                        i.setAction(MoSTService.STOP);
                        i.putExtra(MoSTService.KEY_PIPELINE_TYPE, Pipeline.Type.ACTIVITY_RECOGNITION_COMPARE.toInt());//TODO change
                        context.startService(i);

                        //TODO review implementation
                        // reset btn state
                       /* Editor editor = context.getSharedPreferences(TaskActiveCard.ACTIVITY_DETECTION_PREFS, Context.MODE_PRIVATE).edit();
                        editor.putInt(TaskActiveCard.KEY_AD_SELECTED, -1);
                        editor.apply();*/

                        break;

                    default:
                        break;
                }
            }
        }
    }

    public static boolean isTaskCompatibleWithThisAppVersion(TaskFlat task) {

        for (br.udesc.esag.participactbrasil.domain.persistence.ActionFlat action : task.getActionsDB()) {
            switch (action.getType()) {
                case SENSING_MOST:
                    Pipeline.Type inputType = Pipeline.Type.fromInt(action.getInput_type());
                    switch (inputType) {
                        case ACCELEROMETER:
                        case APP_ON_SCREEN:
                        case ACCELEROMETER_CLASSIFIER:
                        case BATTERY:
                        case CELL:
                        case BLUETOOTH:
                        case GYROSCOPE:
                        case INSTALLED_APPS:
                        case LIGHT:
                        case LOCATION:
                        case MAGNETIC_FIELD:
                        case PHONE_CALL_DURATION:
                        case PHONE_CALL_EVENT:
                        case SYSTEM_STATS:
                        case WIFI_SCAN:
                        case APPS_NET_TRAFFIC:
                        case DEVICE_NET_TRAFFIC:
                        case CONNECTION_TYPE:
                        case GOOGLE_ACTIVITY_RECOGNITION:
                        case ACTIVITY_RECOGNITION_COMPARE:
                        case DR:
                            break;

                        case AVERAGE_ACCELEROMETER:
                        case AUDIO_CLASSIFIER:
                        case RAW_AUDIO:
                            return false;

                        default:
                            return false;
                    }
                case PHOTO:
                    break;
                case QUESTIONNAIRE:
                    break;
                case ACTIVITY_DETECTION:
                    break;
                default:
                    return false;
            }
        }

        return true;

    }
}
