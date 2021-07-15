package br.com.participact.participactbrasil.modules.db;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorWrapper {

    private static final String TAG = SensorWrapper.class.getSimpleName();
    Sensor sensor;

    private SensorWrapper(Sensor sensor) {
        this.sensor = sensor;
    }

    public static SensorWrapper wrap(Sensor sensor) {
        return new SensorWrapper(sensor);
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
