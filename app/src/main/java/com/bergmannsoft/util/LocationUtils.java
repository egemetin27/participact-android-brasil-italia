package com.bergmannsoft.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.participact.participactbrasil.modules.App;

//import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fabiobergmann on 21/12/16.
 *
 *
 * mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

 if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
 mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
 } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
 mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
 } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
 mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mLocationListener);
 }
 *
 */

public class LocationUtils {

    public static final String TAG = LocationUtils.class.getSimpleName();

    public static Location getLastKnownLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = getAvailableProvider(context);
        if (provider != null) {
//            try {
//                Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (loc == null) {
//                    loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if (loc == null) {
//                        loc = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                    }
//                }
//                return loc;
//            } catch (SecurityException e) {
//                Log.e(TAG, null, e);
//            }
        }
        return null;
    }

    public static String getAvailableProvider(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            return LocationManager.PASSIVE_PROVIDER;
        }
        return "none";
    }

//    public static float distanceFromMe(Context context, LatLng ll) {
//        Location me = getLastKnownLocation(context);
//        if (me != null) {
//            Location h = new Location("");
//            h.setLatitude(ll.latitude);
//            h.setLongitude(ll.longitude);
//            return me.distanceTo(h);
//        }
//        return -1;
//    }
//
//    public static float distanceFromMe(Context context, double lat, double lng) {
//        return distanceFromMe(context, new LatLng(lat, lng));
//    }
//
    public static float distanceTo(Context context, LatLng from, LatLng to) {
        Location locFrom = new Location("");
        locFrom.setLatitude(from.latitude);
        locFrom.setLongitude(from.longitude);

        Location locTo = new Location("");
        locTo.setLatitude(to.latitude);
        locTo.setLongitude(to.longitude);

        return locFrom.distanceTo(locTo);
    }

    public static String getCity(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(App.getInstance(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d(TAG, addresses.get(0).toString());
            return addresses.get(0).getSubAdminArea();
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
        return "";
    }

    public static String getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(App.getInstance(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d(TAG, addresses.get(0).toString());
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
        return "";
    }

}
