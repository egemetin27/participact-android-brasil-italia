package br.com.participact.participactbrasil.modules.db;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class UPSensorWrapper {

    private static final String TAG = UPSensorWrapper.class.getSimpleName();
    UPSensor sensor;

    private UPSensorWrapper(UPSensor sensor) {
        this.sensor = sensor;
    }

    public static UPSensorWrapper wrap(UPSensor sensor) {
        return new UPSensorWrapper(sensor);
    }

    public JSONObject jsonData() {
        try {
            return new JSONObject(sensor.getData());
        } catch (JSONException e) {
            Log.e(TAG, null, e);
        }
        return null;
    }

}
