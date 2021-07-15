package br.com.participact.participactbrasil.modules.network.requests.sensing;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.SensorWrapper;
import br.com.participact.participactbrasil.modules.db.UPSensor;
import br.com.participact.participactbrasil.modules.db.UPSensorWrapper;

public class LocationSensingData extends SensingData {

    Double latitude;
    Double longitude;
    Double altitude;
    Double horizontalAccuracy;
    Double verticalAccuracy;
    Double course;
    Double speed;
    Double floor;
    String provider;

    public LocationSensingData(Sensor sensor) {
        super();
        JSONObject object = SensorWrapper.wrap(sensor).jsonData();
        if (object != null) {
            try {
                this.latitude = object.isNull("latitude") ? null : object.getDouble("latitude");
                this.longitude = object.isNull("longitude") ? null : object.getDouble("longitude");
                this.altitude = object.isNull("altitude") ? null : object.getDouble("altitude");
                this.horizontalAccuracy = object.isNull("horizontalAccuracy") ? null : object.getDouble("horizontalAccuracy");
                this.verticalAccuracy = object.isNull("verticalAccuracy") ? null : object.getDouble("verticalAccuracy");
                this.course = object.isNull("course") ? null : object.getDouble("course");
                this.speed = object.isNull("speed") ? null : object.getDouble("speed");
                this.floor = object.isNull("floor") ? null : object.getDouble("floor");
                this.provider = object.isNull("provider") ? null : object.getString("provider");
            } catch (JSONException e) {
                Log.e(TAG, null, e);
            }
        }
    }

    public LocationSensingData(UPSensor sensor) {
        super();
        JSONObject object = UPSensorWrapper.wrap(sensor).jsonData();
        if (object != null) {
            try {
                this.latitude = object.isNull("latitude") ? null : object.getDouble("latitude");
                this.longitude = object.isNull("longitude") ? null : object.getDouble("longitude");
                this.altitude = object.isNull("altitude") ? null : object.getDouble("altitude");
                this.horizontalAccuracy = object.isNull("horizontalAccuracy") ? null : object.getDouble("horizontalAccuracy");
                this.verticalAccuracy = object.isNull("verticalAccuracy") ? null : object.getDouble("verticalAccuracy");
                this.course = object.isNull("course") ? null : object.getDouble("course");
                this.speed = object.isNull("speed") ? null : object.getDouble("speed");
                this.floor = object.isNull("floor") ? null : object.getDouble("floor");
                this.provider = object.isNull("provider") ? null : object.getString("provider");
            } catch (JSONException e) {
                Log.e(TAG, null, e);
            }
        }
    }

    @Override
    public boolean isValid() {
        return latitude != null && longitude != null && altitude != null && horizontalAccuracy != null && verticalAccuracy != null && course != null && speed != null && floor != null && provider != null;
    }
}
