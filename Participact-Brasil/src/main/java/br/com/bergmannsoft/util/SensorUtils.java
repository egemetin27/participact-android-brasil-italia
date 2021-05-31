package br.com.bergmannsoft.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

/**
 * Created by fabiobergmann on 17/10/15.
 */

public class SensorUtils {

    public static final String TAG = SensorUtils.class.getSimpleName();

    public static boolean isAccelerometerAvailable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    public static void debugAvailableSensorsList(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : deviceSensors) {
            Log.d(TAG, sensor.toString());
        }
    }

    public static String getAvailableSensors(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        String sensors = "";
        for (Sensor sensor : deviceSensors) {
            if (sensors.length() > 0)
                sensors += ",";
            sensors += sensor.getName().replaceAll(" ", "_");
        }
        return sensors;
    }

}
