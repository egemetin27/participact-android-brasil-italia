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

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.most.DataBundle;
import org.most.MoSTApplication;

public class FusionLocationInput extends Input implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

	/** The Constant DEBUG. */
    private final static boolean DEBUG = true;

	/** The Constant TAG. */
    private final static String TAG = FusionLocationInput.class.getSimpleName();

    /**
     * {@link SharedPreferences} key to set the location check period.
     */
    public final static String PREF_KEY_PERIODIC_LOCATION_PERIOD = "FusionLocationInput.PeriodicLocationInputPeriodMs";

    /**
     * Default location monitoring interval in milliseconds. Currently set to
     * {@value #PREF_DEFAULT_PERIODIC_LOCATION_PERIOD}.
     */
    public final static int PREF_DEFAULT_PERIODIC_LOCATION_PERIOD = 60 * 1000;

    public static final String PREF_KEY_LOCATION_FASTEST_INTERVAL_MS = "FusionLocationInput.FastestInterval";
    public static final long PREF_DEFAULT_LOCATION_FASTEST_INTERVAL = 1000;

    public static final String PREF_KEY_LOCATION_PRIORITY = "FusionLocationInput.Priority";
	public static final int PREF_DEFAULT_LOCATION_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    public final static String KEY_LONGITUDE = "LocationInput.Longitude";
    public final static String KEY_LATITUDE = "LocationInput.Latitude";
    public final static String KEY_ACCURACY = "LocationInput.Accuracy";
    public final static String KEY_PROVIDER = "LocationInput.Provider";

    public static final long SIGNIFICANT_TIME_DIFFERENCE = 15000; // 15 seconds

    Location _lastLocation;
    private GoogleApiClient googleApiClient;
    LocationRequest _locationRequest;
    boolean _isLibraryAvailable;

	/**
	 * Creates a new FusionLocationInput.
	 *
	 * @param context
	 *            The reference {@link MoSTApplication} context.
	 */
    public FusionLocationInput(MoSTApplication context) {
        super(context);

    }

    @Override
    public void onInit() {
        checkNewState(Input.State.INITED);

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getContext());

        if (ConnectionResult.SUCCESS == resultCode) {
            _isLibraryAvailable = true;

            _locationRequest = LocationRequest.create();

            _locationRequest.setInterval(getContext().getSharedPreferences(MoSTApplication.PREF_INPUT, Context.MODE_PRIVATE).getLong(
                    PREF_KEY_PERIODIC_LOCATION_PERIOD, PREF_DEFAULT_PERIODIC_LOCATION_PERIOD));

            _locationRequest.setPriority(getContext().getSharedPreferences(MoSTApplication.PREF_INPUT, Context.MODE_PRIVATE).getInt(
                    PREF_KEY_LOCATION_PRIORITY, PREF_DEFAULT_LOCATION_PRIORITY));

            _locationRequest.setFastestInterval(getContext().getSharedPreferences(MoSTApplication.PREF_INPUT, Context.MODE_PRIVATE).getLong(
                    PREF_KEY_LOCATION_FASTEST_INTERVAL_MS, PREF_DEFAULT_LOCATION_FASTEST_INTERVAL));

           googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } else {
            Log.e(TAG, "Google Play Service Library not available.");
        }

        super.onInit();
    }

    @Override
    public boolean onActivate() {
        if (_isLibraryAvailable) {
            googleApiClient.connect();
        }
        return super.onActivate();
    }

    @Override
    public void onDeactivate() {
        if (_isLibraryAvailable) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
            // After disconnect() is called, the client is considered "dead".
            googleApiClient.disconnect();
        }
        super.onDeactivate();
    }

    @Override
    public void onFinalize() {
        super.onFinalize();
    }

    @Override
    public Type getType() {
        return Input.Type.CONTINUOUS_FUSION_LOCATION;
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
        Log.w(TAG, "Connected");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, _locationRequest, this);
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG,"Location Client suspended.");
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Can't connect to Google Play Library. Error code: " + connectionResult.getErrorCode());
    }

    public void onDisconnected() {
        Log.w(TAG, "Disconnected");
    }

    public void onLocationChanged(Location newLocation) {
        if (newLocation == null || (newLocation.getLatitude() == 0.0 && newLocation
                .getLongitude() == 0.0)) {
            // filter out 0.0, 0.0 locations
            return;
        }
        if (DEBUG) {
            Log.d(TAG, String.format(
                    "New location:  lat %f - long %f (accuracy: %f)",
                    newLocation.getLatitude(), newLocation.getLongitude(),
                    newLocation.getAccuracy(), newLocation.getProvider()));
        }
        if (isBetterThanCurrent(newLocation)) {
            _lastLocation = newLocation;
            DataBundle b = _bundlePool.borrowBundle();
            b.putDouble(KEY_LATITUDE, _lastLocation.getLatitude());
            b.putDouble(KEY_LONGITUDE, _lastLocation.getLongitude());
            b.putDouble(KEY_ACCURACY, _lastLocation.getAccuracy());
            b.putString(KEY_PROVIDER, _lastLocation.getProvider());
            b.putLong(Input.KEY_TIMESTAMP, System.currentTimeMillis());
            b.putInt(Input.KEY_TYPE, getType().toInt());
            post(b);

        }
    }

    private boolean isBetterThanCurrent(Location newLocation) {
        if (_lastLocation == null) {
            return true;
        }
        long timeDiff = newLocation.getTime() - _lastLocation.getTime();
        return timeDiff > SIGNIFICANT_TIME_DIFFERENCE
                || (newLocation.getAccuracy() <= _lastLocation.getAccuracy());
    }

}
