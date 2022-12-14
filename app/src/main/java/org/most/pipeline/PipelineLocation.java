/*
 * Copyright (C) 2014 University of Bologna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.most.pipeline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.most.DataBundle;
import org.most.MoSTApplication;
import org.most.input.FusionLocationInput;
import org.most.input.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.SensorDaoImpl;

public class PipelineLocation extends Pipeline {

    public static final String PREF_KEY_DUMP_TO_DB = "PipelineLocation.DumpToDB";
    public static final boolean PREF_DEFAULT_DUMP_TO_DB = true;
    public static final String PREF_KEY_SEND_INTENT = "PipelineLocation.SendIntent";
    public static final boolean PREF_DEFAULT_SEND_INTENT = false;

    public static final String KEY_ACTION = "PipelineLocation";

    public final static String KEY_LONGITUDE = "PipelineLocation.Longitude";
    public final static String KEY_LATITUDE = "PipelineLocation.Latitude";
    public final static String KEY_ACCURACY = "PipelineLocation.Accuracy";
    public final static String KEY_PROVIDER = "PipelineLocation.Provider";

    public static final String TBL_LOCATION = "LOCATION";
    public static final String FLD_TIMESTAMP = "timestamp";
    public final static String FLD_LONGITUDE = "longitude";
    public final static String FLD_LATITUDE = "latitude";
    public final static String FLD_ACCURACY = "accuracy";
    public final static String FLD_PROVIDER = "provider";

    public static final String CREATE_LOCATION_TABLE = String.format(
            "_ID INTEGER PRIMARY KEY, %s INT NOT NULL, %s REAL NOT NULL, %s REAL NOT NULL, %s REAL, %s TEXT",
            FLD_TIMESTAMP, FLD_LATITUDE, FLD_LONGITUDE, FLD_ACCURACY, FLD_PROVIDER);

    protected boolean _isDump;
    protected boolean _isSend;

    public PipelineLocation(MoSTApplication context) {
        super(context);
    }

    @Override
    public boolean onActivate() {
        checkNewState(State.ACTIVATED);
        _isDump = getContext().getSharedPreferences(MoSTApplication.PREF_PIPELINES, Context.MODE_PRIVATE).getBoolean(
                PREF_KEY_DUMP_TO_DB, PREF_DEFAULT_DUMP_TO_DB);
        _isSend = getContext().getSharedPreferences(MoSTApplication.PREF_PIPELINES, Context.MODE_PRIVATE).getBoolean(
                PREF_KEY_SEND_INTENT, PREF_DEFAULT_SEND_INTENT);
        return super.onActivate();
    }

    public void onData(DataBundle b) {
        try {
//            if (_isDump) {
//                ContentValues cv = new ContentValues();
//                cv.put(FLD_TIMESTAMP, b.getLong(Input.KEY_TIMESTAMP));
//                cv.put(FLD_LONGITUDE, b.getDouble(FusionLocationInput.KEY_LONGITUDE));
//                cv.put(FLD_LATITUDE, b.getDouble(FusionLocationInput.KEY_LATITUDE));
//                cv.put(FLD_ACCURACY, b.getDouble(FusionLocationInput.KEY_ACCURACY));
//                cv.put(FLD_PROVIDER, b.getString(FusionLocationInput.KEY_PROVIDER));
//                getContext().getDbAdapter().storeData(TBL_LOCATION, cv, true);
//            }
//
//            if (_isSend) {
//                Intent i = new Intent(KEY_ACTION);
//                i.putExtra(KEY_TIMESTAMP, b.getLong(FusionLocationInput.KEY_TIMESTAMP));
//                i.putExtra(KEY_LONGITUDE, b.getDouble(FusionLocationInput.KEY_LONGITUDE));
//                i.putExtra(KEY_LATITUDE, b.getDouble(FusionLocationInput.KEY_LATITUDE));
//                i.putExtra(KEY_ACCURACY, b.getDouble(FusionLocationInput.KEY_ACCURACY));
//                i.putExtra(KEY_PROVIDER, b.getString(FusionLocationInput.KEY_PROVIDER));
//                getContext().sendBroadcast(i);
//            }

            Map<String, Object> map = new HashMap<>();
            map.put("latitude", b.getDouble(FusionLocationInput.KEY_LATITUDE));
            map.put("longitude", b.getDouble(FusionLocationInput.KEY_LONGITUDE));
            map.put("altitude", 0);
            map.put("accuracy", b.getDouble(FusionLocationInput.KEY_ACCURACY));
            map.put("horizontalAccuracy", b.getDouble(FusionLocationInput.KEY_ACCURACY));
            map.put("verticalAccuracy", b.getDouble(FusionLocationInput.KEY_ACCURACY));
            map.put("course", 0);
            map.put("speed", 0);
            map.put("floor", 0);
            map.put("provider", b.getString(FusionLocationInput.KEY_PROVIDER));
            map.put("timestamp", System.currentTimeMillis() / 1000);

            String data = new Gson().toJson(map);

            Sensor sensor = new Sensor();
            sensor.setPipelineTypeValue(getType().toInt());
            sensor.setUploaded(false);
            sensor.setData(data);

            new SensorDaoImpl().commit(sensor);

        } finally {
            b.release();
        }
    }

    @Override
    public Type getType() {
        return Type.LOCATION;
    }

    @Override
    public Set<org.most.input.Input.Type> getInputs() {
        Set<Input.Type> result = new TreeSet<Input.Type>();
        result.add(Input.Type.PERIODIC_FUSION_LOCATION);
        return result;
    }

}
