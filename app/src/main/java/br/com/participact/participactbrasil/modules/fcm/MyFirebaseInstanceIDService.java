package br.com.participact.participactbrasil.modules.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import br.com.participact.participactbrasil.modules.support.UserSettings;

// NOT USED - DEPRECATED
public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);
        UserSettings.getInstance().setRegid(s);
        UserSettings.getInstance().setSendRegid(true);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    /*@Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        UserSettings.getInstance().setRegid(refreshedToken);
        UserSettings.getInstance().setSendRegid(true);
    }*/

}