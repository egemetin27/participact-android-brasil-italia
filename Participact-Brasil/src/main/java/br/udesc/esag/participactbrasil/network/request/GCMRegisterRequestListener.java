package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class GCMRegisterRequestListener implements RequestListener<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(GCMRegisterRequestListener.class);

    Context context;

    public GCMRegisterRequestListener(Context context) {
        this.context = context;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        logger.warn("GCM registration on server failed.");
    }

    @Override
    public void onRequestSuccess(Boolean result) {
        if (result == null) {
            return;
        }
        if (result) {
            UserAccountPreferences.getInstance(context).setGCMSetOnServer(true);
            logger.info("GCM registration on server ok.");
        }
    }
}