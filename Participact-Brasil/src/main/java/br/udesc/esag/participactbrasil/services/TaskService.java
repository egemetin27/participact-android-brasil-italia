package br.udesc.esag.participactbrasil.services;

import android.content.Context;
import android.content.Intent;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.domain.enums.SensingActionEnum;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;

public class TaskService {

    private final static Logger logger = LoggerFactory.getLogger(TaskService.class);

    public static void activateTask(Context context, TaskFlat task) {
        for (ActionFlat action : task.getActionsDB()) {
            switch (action.getType()) {
                case SENSING_MOST:
                    if (Pipeline.Type.fromInt(action.getInput_type()) != Pipeline.Type.DUMMY) {
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

    public static void suspendTask(Context context, TaskFlat task) {
        // At the moment only sensing tasks can be suspended
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

                default:
                    break;
            }
        }
    }

    public static boolean isTaskCompatibleWithThisAppVersion(TaskFlat task) {

        for (ActionFlat action : task.getActionsDB()) {
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
                        case DR:
                            return true;

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
                default:
                    return false;
            }
        }

        return true;

    }

}
