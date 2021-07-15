/*
 * Copyright (C) 2014 University of Bologna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.most.input;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.most.DataBundle;
import org.most.MoSTApplication;
import org.most.utils.DelayedWakeLockRelease;

public class PeriodicGoogleActivityRecognitionInput extends PeriodicInput implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /** The Constant DEBUG. */
    private final static boolean DEBUG = true;

    /** The Constant TAG. */
    private final static String TAG = PeriodicGoogleActivityRecognitionInput.class.getSimpleName();

    /**
     * {@link SharedPreferences} key to set the location check period.
     */
    public final static String PREF_KEY_GOOGLE_ACTIVITY_RECOGNITION_PERIOD = "PeriodicGoogleActivityRecognitionInput.PeriodMs";
    public final static int DEFAULT_GOOGLE_ACTIVITY_RECOGNITION_PERIOD = 60 * 1000;

    public final static String PREF_KEY_GOOGLE_ACTIVITY_RECOGNITION_INTERVALL = "PeriodicGoogleActivityRecognitionInput.intervallMs";
    public final static int DEFAULT_GOOGLE_ACTIVITY_RECOGNITION_INTERVALL = 1000;

    public final static String KEY_CONFIDENCE = "confidence";
    public final static String KEY_RECOGNIZED_ACTIVITY = "recognized_activity";

    private final static String GOOGLE_ACTIVITY_RECOGNITION_ACTION = "org.most.GOOGLE_ACTIVITY_RECOGNITION";

    private Intent _intent;
    private IntentFilter _filter;
    private GoogleActivityRecognitionBroadcastReceiver _receiver;
    private PendingIntent _pendingIntent;
    private long _intervall;

    boolean _isLibraryAvailable;
    private GoogleApiClient googleApiClient;

    /**
     * Creates a new FusionLocationInput.
     *
     * @param context
     *            The reference {@link MoSTApplication} context.
     */
    public PeriodicGoogleActivityRecognitionInput(MoSTApplication context) {
        super(context, context.getSharedPreferences(MoSTApplication.PREF_INPUT,
                Context.MODE_PRIVATE).getInt(PREF_KEY_GOOGLE_ACTIVITY_RECOGNITION_PERIOD,
                DEFAULT_GOOGLE_ACTIVITY_RECOGNITION_PERIOD));

    }

    @Override
    public void onInit() {
        checkNewState(Input.State.INITED);

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());

        if (ConnectionResult.SUCCESS == result) {
            _isLibraryAvailable = true;

            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            _intervall = getContext().getSharedPreferences(MoSTApplication.PREF_INPUT,
                    Context.MODE_PRIVATE).getLong(PREF_KEY_GOOGLE_ACTIVITY_RECOGNITION_PERIOD,
                    DEFAULT_GOOGLE_ACTIVITY_RECOGNITION_PERIOD);

            _intent = new Intent();
            _intent.setAction(GOOGLE_ACTIVITY_RECOGNITION_ACTION);

            _filter = new IntentFilter();
            _filter.addAction(GOOGLE_ACTIVITY_RECOGNITION_ACTION);

            _receiver = new GoogleActivityRecognitionBroadcastReceiver();

            _pendingIntent = PendingIntent.getBroadcast(getContext(), 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            Log.e(TAG, "Google Play Service Library not available.");
        }

        super.onInit();
    }

    @Override
    public boolean onActivate() {
        if (_isLibraryAvailable) {
            getContext().registerReceiver(_receiver, _filter);
        }
        return super.onActivate();
    }

    @Override
    public void onDeactivate() {
        if (_isLibraryAvailable) {
            getContext().unregisterReceiver(_receiver);
        }
        super.onDeactivate();
    }

    @Override
    public void onFinalize() {
        super.onFinalize();
    }

    @Override
    public Type getType() {
        return Input.Type.PERIODIC_GOOGLE_ACTIVITY_RECOGNITION;
    }

    @Override
    public boolean isWakeLockNeeded() {
        return false;
    }

    public void onProviderEnabled(String provider) {
        Log.i(TAG, String.format("Provider %s enabled", provider));
    }

    public void onProviderDisabled(String provider) {
        Log.e(TAG, String.format("Provider %s disabled", provider));
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE) {
            Log.e(TAG, "Location provider out of service: " + provider);
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            Log.w(TAG, "Location provider temp unavailable: " + provider);
        }
    }

    public void onConnected(Bundle arg0) {
        Log.i(TAG, "Connected");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient,_intervall, _pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,
                "Can't connect to Google Play Library. Error code: "
                        + connectionResult.getErrorCode());
    }

    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void workToDo() {
        if (_isLibraryAvailable) {
            if (googleApiClient == null) {

                googleApiClient = new GoogleApiClient.Builder(getContext())
                        .addApi(ActivityRecognition.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                googleApiClient.connect();

                getContext().getWakeLockHolder().acquireWL();
                new DelayedWakeLockRelease(getContext(), 30000).start();
            }
        }

        scheduleNextStart();
    }

    private class GoogleActivityRecognitionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ActivityRecognitionResult.hasResult(intent)) {
                // Get the update
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                // Get the most probable activity
                DetectedActivity mostProbableActivity = result.getMostProbableActivity();
                int confidence = mostProbableActivity.getConfidence();
                int activityType = mostProbableActivity.getType();
                String activityName = getNameFromType(activityType);

                if (DEBUG)
                    Log.i(TAG, String.format("Recognized %s with confidence %d", activityName, confidence));

                DataBundle b = _bundlePool.borrowBundle();
                b.putInt(KEY_CONFIDENCE, confidence);
                b.putString(KEY_RECOGNIZED_ACTIVITY, activityName);
                b.putLong(Input.KEY_TIMESTAMP, System.currentTimeMillis());
                b.putInt(Input.KEY_TYPE, getType().toInt());
                post(b);

                if (googleApiClient != null && googleApiClient.isConnected()) {
                    ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,_pendingIntent);
                    googleApiClient.disconnect();
                    googleApiClient = null;
                }

            }

        }

        private String getNameFromType(int activityType) {
            switch (activityType) {
                case DetectedActivity.IN_VEHICLE:
                    return "in_vehicle";
                case DetectedActivity.ON_BICYCLE:
                    return "on_bicycle";
                case DetectedActivity.ON_FOOT:
                    return "on_foot";
                case DetectedActivity.STILL:
                    return "still";
                case DetectedActivity.UNKNOWN:
                    return "unknown";
                case DetectedActivity.TILTING:
                    return "tilting";
            }
            return "unknown";
        }

    }

}
