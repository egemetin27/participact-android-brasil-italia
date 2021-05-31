package org.most.input;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.most.DataBundle;
import org.most.MoSTApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabiobergmann on 09/12/16.
 */

public class LocationInput extends PeriodicInput {

    public final static String KEY_LONGITUDE = "LocationInput.Longitude";
    public final static String KEY_LATITUDE = "LocationInput.Latitude";
    public final static String KEY_ACCURACY = "LocationInput.Accuracy";
    public final static String KEY_PROVIDER = "LocationInput.Provider";

    private LocationManager mLocationManager;

    private String lastProvider;

    private Timer timer;

    public LocationInput(MoSTApplication context) {
        super(context, 1000 * 60 * 2);
    }

    @Override
    public Type getType() {
        return Input.Type.PERIODIC_FUSION_LOCATION;
    }

    @Override
    public void workToDo() {
        try {
            String availableProvider = null;
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                availableProvider = LocationManager.GPS_PROVIDER;
            } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                availableProvider = LocationManager.NETWORK_PROVIDER;
            } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                availableProvider = LocationManager.PASSIVE_PROVIDER;
            }
            if (availableProvider != null && !availableProvider.equals(lastProvider)) {
                lastProvider = availableProvider;
                mLocationManager.removeUpdates(mLocationListener);
                mLocationManager.requestLocationUpdates(availableProvider, 1000 * 60, 50, mLocationListener);
            }

        } catch (SecurityException e) {
            Log.e("Location", null, e);
        }
        scheduleNextStart();
    }

    @Override
    public boolean isWakeLockNeeded() {
        return false;
    }

    @Override
    public void onInit() {
        super.onInit();
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lastProvider = LocationManager.GPS_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 50, mLocationListener);
            } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lastProvider = LocationManager.NETWORK_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 50, mLocationListener);
            } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                lastProvider = LocationManager.PASSIVE_PROVIDER;
                mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000 * 60, 50, mLocationListener);
            }
        } catch (SecurityException e) {
            Log.e("Location", null, e);
        }

        if (timer != null)
            timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mLocationManager != null) {
                        Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc == null) {
                            loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (loc == null) {
                            loc = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        }
                        if (loc != null) {
                            _lastLocation = loc;
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
                } catch (SecurityException e) {
                    Log.e("Location", null, e);
                }
            }
        }, 1000 * 60 * 5);

    }

    @Override
    public void onDeactivate() {
        timer.cancel();
        super.onDeactivate();
    }

    private Location _lastLocation;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location loc) {
            _lastLocation = loc;
            DataBundle b = _bundlePool.borrowBundle();
            b.putDouble(KEY_LATITUDE, _lastLocation.getLatitude());
            b.putDouble(KEY_LONGITUDE, _lastLocation.getLongitude());
            b.putDouble(KEY_ACCURACY, _lastLocation.getAccuracy());
            b.putString(KEY_PROVIDER, _lastLocation.getProvider());
            b.putLong(Input.KEY_TIMESTAMP, System.currentTimeMillis());
            b.putInt(Input.KEY_TYPE, getType().toInt());
            post(b);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
