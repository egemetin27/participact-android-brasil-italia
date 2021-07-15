package com.bergmannsoft.push;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;

public class GCM {

    static final String TAG = "GCM";

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public String senderId;

//    private GoogleCloudMessaging gcm;

    public interface GCMCallback {
        public void onRegistrationFound(String registrationId);

        public void onRegisterDone(String registrationId);

        public void onException(Exception e);
    }

    public GCM(String senderId) {
        super();
        this.senderId = senderId;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog_categories that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     *
     * @throws DeviceNotSupportedException
     */
    private boolean checkPlayServices(Activity activity)
            throws DeviceNotSupportedException {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(activity);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                throw new DeviceNotSupportedException();
//            }
//            return false;
//        }
        return true;
    }

    public void getRegistrationId(Activity activity, SharedPreferences prefs, GCMCallback callback) throws DeviceNotSupportedException, GooglePlayServicesNotFound {

        if (callback == null)
            throw new RuntimeException("callback cannot be null");
        if (!checkPlayServices(activity)) {
            throw new GooglePlayServicesNotFound();
        }
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            // return "";
            registerInBackground(activity, prefs, callback);
            return;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(activity);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            // return "";
            registerInBackground(activity, prefs, callback);
            return;
        }
        callback.onRegistrationFound(registrationId);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context,
                                      final SharedPreferences prefs, final GCMCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(context);
//                    }
//                    String regid = gcm.register(senderId);
                    // msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();
//                    callback.onRegisterDone(regid);

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
//                    storeRegistrationId(context, prefs, regid);
                } catch (Exception ex) {
                    callback.onException(ex);
                    // msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }

        }.execute();
    }

    public void storeRegistrationId(Context context, SharedPreferences prefs,
                                     String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}