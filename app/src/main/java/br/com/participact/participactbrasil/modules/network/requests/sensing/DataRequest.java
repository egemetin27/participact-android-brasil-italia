package br.com.participact.participactbrasil.modules.network.requests.sensing;

import com.bergmannsoft.rest.Response;
import com.google.gson.Gson;

import org.most.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.UPSensor;
import br.com.participact.participactbrasil.modules.network.api.ReceiveDataAPI;
import retrofit2.Call;

public class DataRequest {

    public static final String TAG = DataRequest.class.getSimpleName();

    List<HashMap<String, Object>> data = new ArrayList<>();

    private DataRequest() {

    }

    public static DataRequest create(List<Sensor> entities) {
        DataRequest request = new DataRequest();
        for (Sensor sensor : entities) {
            HashMap map = new Gson().fromJson(sensor.getData(), (HashMap.class));
            request.data.add(map);
        }
//        switch (type) {
//            case LOCATION:
//                for (Sensor sensor : entities) {
//                    LocationSensingData lsd = new LocationSensingData(sensor);
//                    if (lsd.isValid()) {
//                        request.data.add(lsd);
//                    }
//                }
//                break;
//
//        }
        return request;
    }

    public static DataRequest createUp(List<UPSensor> entities) {
        DataRequest request = new DataRequest();
        for (UPSensor sensor : entities) {
            HashMap map = new Gson().fromJson(sensor.getData(), (HashMap.class));
            request.data.add(map);
//            LocationSensingData lsd = new LocationSensingData(sensor);
//            if (lsd.isValid()) {
//                request.data.add(lsd);
//            }
        }
        return request;
    }

    public boolean hasData() {
        return data != null && data.size() > 0;
    }

    public Call<Response> call(Pipeline.Type type, ReceiveDataAPI api, Map<String, String> defaultHeaders) {
        switch (type) {
            case LOCATION:
                return api.location(defaultHeaders, this);
            case ACCELEROMETER:
                return api.accelerometer(defaultHeaders, this);
            case ACCELEROMETER_CLASSIFIER:
                return api.accelerometerClassifier(defaultHeaders, this);
            case ACTIVITY_RECOGNITION_COMPARE:
                return api.activityRecognitionCompare(defaultHeaders, this);
            case APP_ON_SCREEN:
                return api.appOnScreen(defaultHeaders, this);
            case CONNECTION_TYPE:
                return api.connectionType(defaultHeaders, this);
            case GOOGLE_ACTIVITY_RECOGNITION:
                return api.googleActivityRecognition(defaultHeaders, this);
            case BATTERY:
                return api.battery(defaultHeaders, this);
            case GYROSCOPE:
                return api.gyroscope(defaultHeaders, this);

        }
        return null;
    }
}
