package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequest;
import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequestListener;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;

public class UpdateReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(UpdateReceiver.class);
    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    @Override
    public void onReceive(Context context, Intent intent) {

        GCMRegisterRequest request = new GCMRegisterRequest(context);
        String lastRequestCacheKey = request.createCacheKey();
        if (!_contentManager.isStarted()) {
            _contentManager.start(context);
        }
        _contentManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new GCMRegisterRequestListener(context));
        logger.info("Sending GCM id to server.");
    }

}
