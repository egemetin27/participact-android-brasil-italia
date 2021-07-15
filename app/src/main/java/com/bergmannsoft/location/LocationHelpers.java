package com.bergmannsoft.location;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

public class LocationHelpers {

    public static boolean isLocationEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            statusOfGPS = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
//        if (!statusOfGPS) {
//            statusOfGPS = manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
//        }
        return statusOfGPS;
    }

    public static void startSettingsActivity(Context context) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}