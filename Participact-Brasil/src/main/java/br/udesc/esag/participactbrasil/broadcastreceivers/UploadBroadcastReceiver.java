package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;

import br.udesc.esag.participactbrasil.activities.settings.Settings;
import br.udesc.esag.participactbrasil.support.DataUploader;

public class UploadBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = UploadBroadcastReceiver.class.getSimpleName();
    NetworkStateChecker networkChecker = new DefaultNetworkStateChecker();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (networkChecker.isNetworkAvailable(context)) {
            Log.i(TAG, "UploadBroadcastReceiver will upload data.");
            if (Settings.getInstance(context).canSendData()) {
                DataUploader.getInstance(context).uploadOverWifi();
            } else {
                Log.i(TAG, "UploadBroadcastReceiver did not upload because user set to upload on when connected to a Wi-Fi network.");
            }
        }
    }


}