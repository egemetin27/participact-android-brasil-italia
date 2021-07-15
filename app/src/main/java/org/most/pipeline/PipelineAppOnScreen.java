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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.most.DataBundle;
import org.most.MoSTApplication;
import org.most.input.AppOnScreenInput;
import org.most.input.Input;
import org.most.persistence.DBAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.com.participact.participactbrasil.modules.db.Sensor;
import br.com.participact.participactbrasil.modules.db.SensorDaoImpl;

/**

 *
 */
public class PipelineAppOnScreen extends Pipeline {

    private static final boolean DEBUG = true;
    private static final String TAG = PipelineAppOnScreen.class.getSimpleName();

    public static final String PREF_KEY_DUMP_TO_DB = "PipelineAppOnScreen.DumpToDB";
    public static final boolean PREF_DEFAULT_DUMP_TO_DB = true;
    public static final String PREF_KEY_SEND_INTENT = "PipelineAppOnScreen.SendIntent";
    public static final boolean PREF_DEFAULT_SEND_INTENT = false;

    public final static String FLD_APPNAME = "APP_NAME";
    public final static String FLD_STARTTIME = "START_TIME";
    public final static String FLD_ENDTIME = "END_TIME";

    public final static String TBL_APP_ON_SCREEN = "APP_ON_SCREEN";
    public static final String CREATE_APP_ON_SCREEN_TABLE = String.format(
            "_ID INTEGER PRIMARY KEY, %s INT NOT NULL, %s INT NOT NULL, %s TEXT NOT NULL", FLD_STARTTIME, FLD_ENDTIME,
            FLD_APPNAME);

    public static final String KEY_ACTION = "PipelineAppOnScreen";
    public static final String KEY_APPNAME = "PipelineAppOnScreen.AppName";
    public static final String KEY_STARTTIME = "PipelineAppOnScreen.StartTime";
    public static final String KEY_ENDTIME = "PipelineAppOnScreen.EndTime";

    protected AppEntry _lastApp;
    protected boolean _isDump;
    protected boolean _isSend;
    protected int _inputMonitoringPeriod;

    public PipelineAppOnScreen(MoSTApplication context) {
        super(context);
    }

    @Override
    public boolean onActivate() {
        checkNewState(State.ACTIVATED);
        _isDump = getContext().getSharedPreferences(MoSTApplication.PREF_PIPELINES, Context.MODE_PRIVATE).getBoolean(
                PREF_KEY_DUMP_TO_DB, PREF_DEFAULT_DUMP_TO_DB);
        _isSend = getContext().getSharedPreferences(MoSTApplication.PREF_PIPELINES, Context.MODE_PRIVATE).getBoolean(
                PREF_KEY_SEND_INTENT, PREF_DEFAULT_SEND_INTENT);
        _lastApp = null;
        _inputMonitoringPeriod = AppOnScreenInput.getMonitorPeriod(getContext());
        return super.onActivate();
    }

    @Override
    public void onDeactivate() {
        checkNewState(State.DEACTIVATED);
        if (_isDump && _lastApp != null) {
            storeAppEntry(_lastApp);
            _lastApp = null;
        }
        super.onDeactivate();
    }

    public void onData(DataBundle b) {
        try {
            String appName = b.getString(AppOnScreenInput.KEY_APPONSCREEN);
            long timestamp = b.getLong(AppOnScreenInput.KEY_TIMESTAMP);

            Map<String, Object> map = new HashMap<>();
            map.put("appname", appName);
            map.put("endTime", timestamp / 1000);
            map.put("startTime", timestamp / 1000);
            map.put("timestamp", System.currentTimeMillis() / 1000);

            String data = new Gson().toJson(map);

            Sensor sensor = new Sensor();
            sensor.setPipelineTypeValue(getType().toInt());
            sensor.setUploaded(false);
            sensor.setData(data);

            new SensorDaoImpl().commit(sensor);

//            if (_isDump || _isSend) {
//                if (_lastApp == null) {
//                    // new app
//                    _lastApp = new AppEntry();
//                    _lastApp.appName = appName;
//                    _lastApp.startTime = timestamp;
//                    _lastApp.endTime = timestamp + _inputMonitoringPeriod;
//                } else if (_lastApp.appName.equals(appName) && (timestamp - _lastApp.endTime) < (_inputMonitoringPeriod * 2)) {
//                    // extend time of current app
//                    _lastApp.endTime = timestamp;
//                } else {
//                    // dump last active app to db and set new current app
//                    _lastApp.endTime = timestamp;
//                    if (_isDump) {
//                        storeAppEntry(_lastApp);
//                    }
//                    _lastApp = new AppEntry();
//                    _lastApp.appName = appName;
//                    _lastApp.startTime = timestamp;
//                    _lastApp.endTime = timestamp + _inputMonitoringPeriod;
//                }
//            }
//            if (_isSend) {
//                Intent i = new Intent(KEY_ACTION);
//                i.putExtra(KEY_APPNAME, _lastApp.appName);
//                i.putExtra(KEY_STARTTIME, _lastApp.startTime);
//                i.putExtra(KEY_ENDTIME, System.currentTimeMillis());
//                getContext().sendBroadcast(i);
//            }
        } finally {
            b.release();
        }
    }

    protected void storeAppEntry(AppEntry appEntry) {
        if (appEntry == null) {
            Log.w(TAG, "Unable to save null AppEntry");
            return;
        }
        DBAdapter dbAdapter = getContext().getDbAdapter();
        if (DEBUG) {
            Log.d(TAG, "Saving app on screen " + appEntry);
        }
        ContentValues cv = new ContentValues();
        cv.put(FLD_APPNAME, appEntry.appName);
        cv.put(FLD_STARTTIME, appEntry.startTime);
        cv.put(FLD_ENDTIME, appEntry.endTime);
        dbAdapter.storeData(TBL_APP_ON_SCREEN, cv, true);
    }

    @Override
    public Type getType() {
        return Type.APP_ON_SCREEN;
    }

    @Override
    public Set<org.most.input.Input.Type> getInputs() {
        Set<org.most.input.Input.Type> result = new TreeSet<org.most.input.Input.Type>();
        result.add(Input.Type.APPONSCREEN);
        return result;
    }

    @SuppressLint("DefaultLocale")
    private static class AppEntry {
        String appName;
        long startTime;
        long endTime;

        @Override
        public String toString() {
            return String.format("Pkg: %s - Start: %d - End: %d - Duration %d", appName, startTime,
                    endTime, endTime - startTime);
        }
    }
}
