package br.com.bergmannsoft.util;

import android.os.Build;

/**
 * Created by fabiobergmann on 10/6/16.
 */

public class DeviceUtils {

    public static String getDeviceInfo() {
        return String.format("%s %s %d", Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT);
    }

}
