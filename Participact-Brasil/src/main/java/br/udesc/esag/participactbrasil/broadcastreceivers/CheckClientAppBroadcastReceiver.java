package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;

import br.udesc.esag.participactbrasil.services.NetworkService;

public class CheckClientAppBroadcastReceiver extends BroadcastReceiver {

    NetworkStateChecker networkChecker = new DefaultNetworkStateChecker();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (networkChecker.isNetworkAvailable(context)) {
            Intent i = new Intent(context, NetworkService.class);
            i.setAction(NetworkService.CHECK_CLIENT_APP_VERSION);
            context.startService(i);
        }
    }

}
