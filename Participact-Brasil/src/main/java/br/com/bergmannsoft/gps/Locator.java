package br.com.bergmannsoft.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by fabiobergmann on 10/6/16.
 */

public class Locator {
    private static final String TAG = Locator.class.getSimpleName();

    // region Fields

    private Context context;

    private LocationManager mLocationManager;
    private Location mLocation;
    private String currentProvider;

    private long minTimeMilliseconds = 0;
    private float minDistanceMeters = 10;

    private boolean started;

    // endregion Fields

    // region Constructor

    public Locator(Context context) {
        this.context = context;
    }

    // endregion Constructor

    // region Start

    public void start() {
        start(0, 10);
    }

    public void start(long minTimeMilliseconds, float minDistanceMeters) {
        if (!started) {
            this.started = true;
            this.minDistanceMeters = minDistanceMeters;
            this.minTimeMilliseconds = minTimeMilliseconds;
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            startAvailableLocationProvider();
        }
    }

    // endregion Start

    // region Stop

    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);
        started = false;
    }

    // endregion Stop

    // region Getters

    public boolean isStarted() {
        return started;
    }

    public Location getLocation() {
        if (mLocation == null) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mLocation == null) {
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mLocation == null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
        }
        return mLocation;
    }

    // endregion Getters

    // region Methods

    public String getCity() {
        List<Address> addresses = getAddressesFromLocation(1);
        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0).getLocality();
        }
        return "";
    }

    public String getCountry() {
        List<Address> addresses = getAddressesFromLocation(1);
        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0).getCountryName();
        }
        return "";
    }

    public List<Address> getAddressesFromLocation(int maxResults) {
        Location location = getLocation();
        if (location != null) {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), maxResults);
                return addresses;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // endregion Methods

    // region Private Methods

    private void startAvailableLocationProvider() {

        stop();

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            currentProvider = LocationManager.GPS_PROVIDER;
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMilliseconds, minDistanceMeters, mLocationListener);
        } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            currentProvider = LocationManager.NETWORK_PROVIDER;
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMilliseconds, minDistanceMeters, mLocationListener);
        } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            currentProvider = LocationManager.PASSIVE_PROVIDER;
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTimeMilliseconds, minDistanceMeters, mLocationListener);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location loc) {
            mLocation = loc;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider) && !currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                startAvailableLocationProvider();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (currentProvider.equals(provider)) {
                startAvailableLocationProvider();
            }
        }
    };

    // endregion Private Methods

}
