package com.bergmannsoft.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "GcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getExtras() == null) {
            return;
        }

        String regId = intent.getExtras().getString("registration_id");
        if (regId != null && !regId.equals("")) {
            Log.v(TAG, regId);
//            QuizApplication.getInstance().dispatchMessage(MessageType.GCM_REGISTRATION_RECEIVED, regId);
            return;
        }

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}