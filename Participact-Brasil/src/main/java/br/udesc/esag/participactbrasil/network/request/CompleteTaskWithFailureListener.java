package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.LoginUtility;

public class CompleteTaskWithFailureListener implements RequestListener<ResponseMessage> {

    private final static Logger logger = LoggerFactory.getLogger(CompleteTaskWithFailureListener.class);


    private Context context;
    private TaskFlat task;

    public CompleteTaskWithFailureListener(Context context, TaskFlat task) {
        this.task = task;
        this.context = context;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.e("TaskWithFailureListener", null, spiceException);
        LoginUtility.checkIfLoginException(context, spiceException);
        logger.warn("Exception uploading the final state of the task with id {}.", task.getId(), spiceException);
    }

    @Override
    public void onRequestSuccess(ResponseMessage result) {
        if (result.getResultCode() == ResponseMessage.RESULT_OK) {
            logger.info("Successfully uploaded the final state of the task with id {}.", task.getId());
            //no longer necessary, the tasks can't be deleted for showing in history
            StateUtility.changeTaskState(context, task, TaskState.COMPLETED_WITH_UNSUCCESS);
        }

    }

}
