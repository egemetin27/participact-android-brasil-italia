package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.preferences.DataUploaderLogPreferences;

public class StateUploadRequestListener implements RequestListener<ResponseMessage> {

    private final static Logger logger = LoggerFactory.getLogger(StateUploadRequestListener.class);

    Context context;

    public StateUploadRequestListener(Context context) {
        this.context = context;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        logger.warn("Failed to upload state file to server.", spiceException);
    }

    @Override
    public void onRequestSuccess(ResponseMessage result) {
        if (result != null && result.getResultCode() == ResponseMessage.RESULT_OK) {
            DataUploaderLogPreferences.getInstance(context).setLogUpload(false);
            logger.info("Successfully uploaded state file to server.");
        }
    }

}
