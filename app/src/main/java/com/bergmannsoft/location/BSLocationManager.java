package com.bergmannsoft.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.modules.App;

import static android.content.Context.LOCATION_SERVICE;

public class BSLocationManager {

    private static final boolean USE_FUSED_LOCATION = true;

    private static final String TAG = BSLocationManager.class.getSimpleName();
    private static final String PREFERABLE_PROVIDER = "preferable_provider";
    private static BSLocationManager instance;
    private LocationManager mLocationManager;
    private final Context context;
    private Timer requestLocationUpdatesTimer;
    private int requestLocationUpdatesTries = 0;


    private List<BSLocationListener> listeners = new ArrayList<>();

    public enum Status {
        SEARCHING_USING_GPS,
        SEARCHING_USING_NETWORK
    }

    private boolean running = false;

    private boolean automaticallyUpdateProviderOnStatusChanged = false;

    private String currentProvider = LocationManager.GPS_PROVIDER;
    private Map<String, Integer> providerStatus = new HashMap<String, Integer>() {{
        put(LocationManager.GPS_PROVIDER, LocationProvider.OUT_OF_SERVICE);
        put(LocationManager.NETWORK_PROVIDER, LocationProvider.OUT_OF_SERVICE);
        put(LocationManager.PASSIVE_PROVIDER, LocationProvider.OUT_OF_SERVICE);
    }};

    FusedLocationProviderClient mFusedLocationClient;

    // region Constructor

    private BSLocationManager(Context context) {
        this.context = context;
        if (USE_FUSED_LOCATION) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        } else {
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }
    }

    // endregion

    /**
     * Location manager instance.
     *
     * @param context Context.
     * @return The instance
     */
    public static BSLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new BSLocationManager(context);
        }
        instance.currentProvider = App.getInstance().getPreferenceString(PREFERABLE_PROVIDER, LocationManager.GPS_PROVIDER);
        instance.requestLocationUpdatesTries = 0;
        return instance;
    }

    /**
     * Request location updates.
     *
     * @param listener Callback when location is updated.
     */
    public void requestLocationUpdates(BSLocationListener listener) {
        if (listener == null) {
            return;
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        running = true;
        try {
            if (USE_FUSED_LOCATION) {
                requestFusedLocationUpdates(listener.getUpdateTimeInterval());
            } else {
                updateProvider();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeListener(BSLocationListener listener) {
        listeners.remove(listener);
    }

    // region Google Fused Location

    @SuppressLint("MissingPermission")
    private void requestFusedLocationUpdates(long intervalMillis) {
        if (!running) {
            return;
        }
        if (!hasPermission()) {
            return;
        }
        // Remove if already running
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        // Start a new request
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(intervalMillis);
        mFusedLocationClient.requestLocationUpdates(request, mLocationCallback, null);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            // Find best location
            Location best = null;
            for (Location location : locationResult.getLocations()) {
                if (best == null) {
                    best = location;
                } else {
                    if (location.getAccuracy() < best.getAccuracy()) {
                        best = location;
                    }
                }
            }
            boolean didRemovedListeners = false;
            if (best != null) {
                running = false;
                List<BSLocationListener> removedListeners = new ArrayList<>();
                for (BSLocationListener listener : listeners) {
                    listener.onLocationChanged(best);
                    if (!listener.shouldContinueUpdating()) {
                        removedListeners.add(listener);
                    }
                }
                for (BSLocationListener listener : removedListeners) {
                    listeners.remove(listener);
                    didRemovedListeners = true;
                }

            }
            // If there's no more listeners, stop updating location
            if (listeners.size() == 0) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            } else {
                // If removed any listener, restart location update using time interval from last listener still in the queue.
                if (didRemovedListeners) {
                    BSLocationListener listener = listeners.get(listeners.size() - 1);
                    requestLocationUpdates(listener);
                }
            }
        }
    };

    // endregion

    // region Default GPS implementation

    private void updateProvider() {
        if (!running) {
            return;
        }
        if (!hasPermission()) {
            return;
        }

        try {

            if (!automaticallyUpdateProviderOnStatusChanged) {
                setupRequestLocationUpdatesTimeout();
                for (BSLocationListener listener : listeners) {
                    listener.onStatusChanged(currentProvider.equals(LocationManager.NETWORK_PROVIDER) ? Status.SEARCHING_USING_NETWORK : Status.SEARCHING_USING_GPS);
                }
                if (mLocationManager.isProviderEnabled(currentProvider)) {
                    mLocationManager.requestLocationUpdates(currentProvider, 0, 0, mLocationListener);
                } else {
                    requestLocationUpdatesTries++;
                    if (currentProvider.equals(LocationManager.NETWORK_PROVIDER)) {
                        currentProvider = LocationManager.GPS_PROVIDER;
                    } else {
                        currentProvider = LocationManager.NETWORK_PROVIDER;
                    }
                    for (BSLocationListener listener : listeners) {
                        listener.onStatusChanged(currentProvider.equals(LocationManager.NETWORK_PROVIDER) ? Status.SEARCHING_USING_NETWORK : Status.SEARCHING_USING_GPS);
                    }
                    if (mLocationManager.isProviderEnabled(currentProvider)) {
                        mLocationManager.requestLocationUpdates(currentProvider, 0, 0, mLocationListener);
                    } else {
                        for (BSLocationListener listener : listeners) {
                            listener.onNoProviderAvailable();
                            requestLocationUpdatesTimer.cancel();
                        }
                    }
                }
            } else {

                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    currentProvider = LocationManager.NETWORK_PROVIDER;
                    providerStatus.put(LocationManager.NETWORK_PROVIDER, LocationProvider.AVAILABLE);
                } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    currentProvider = LocationManager.GPS_PROVIDER;
                    providerStatus.put(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE);
                }

            }
        } catch (SecurityException e) {
            Log.e(TAG, null, e);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
    }

    private void setupRequestLocationUpdatesTimeout() {
        requestLocationUpdatesTimer = new Timer();
        requestLocationUpdatesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (requestLocationUpdatesTries == 0) {
                    requestLocationUpdatesTries++;
                    if (currentProvider.equals(LocationManager.NETWORK_PROVIDER)) {
                        currentProvider = LocationManager.GPS_PROVIDER;
                    } else {
                        currentProvider = LocationManager.NETWORK_PROVIDER;
                    }
                    updateProvider();
                } else {
                    for (BSLocationListener listener : listeners) {
                        listener.onTimeout();
                    }
                    mLocationManager.removeUpdates(mLocationListener);
                }
            }
        }, 8000);
    }

    // endregion

    public boolean hasPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            for (BSLocationListener listener : listeners) {
                listener.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            return false;
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            for (BSLocationListener listener : listeners) {
                listener.onPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            return false;
        }
        return true;
    }

    // region Location Listener
    /**
     * Location Listener
     */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.d(TAG, "Accuracy: " + location.getAccuracy() + ", provider: " + currentProvider);
            if (location.getAccuracy() > 100) {
                return;
            }
            mLocationManager.removeUpdates(mLocationListener);
            requestLocationUpdatesTimer.cancel();
            running = false;
            App.getInstance().savePreferenceString(PREFERABLE_PROVIDER, currentProvider);
            for (BSLocationListener listener : listeners) {
                listener.onLocationChanged(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (!automaticallyUpdateProviderOnStatusChanged) {
                return;
            }
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                if (status == LocationProvider.AVAILABLE) {
                    if (!currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                        updateProvider();
                    } else if (currentProvider.equals(LocationManager.GPS_PROVIDER) && providerStatus.get(provider).intValue() != status) {
                        updateProvider();
                    }
                } else {
                    if (currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                        updateProvider();
                    }
                }
            } else {
                if (currentProvider.equals(LocationManager.GPS_PROVIDER) && providerStatus.get(LocationManager.GPS_PROVIDER).intValue() != LocationProvider.AVAILABLE) {
                    updateProvider();
                }
                if (currentProvider.equals(provider) && status != LocationProvider.AVAILABLE) {
                    updateProvider();
                }
            }
            providerStatus.put(provider, status);
        }

        @Override
        public void onProviderEnabled(String s) {
            if (automaticallyUpdateProviderOnStatusChanged) {
                updateProvider();
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            if (automaticallyUpdateProviderOnStatusChanged) {
                updateProvider();
            }
        }
    };
    // endregion

}
