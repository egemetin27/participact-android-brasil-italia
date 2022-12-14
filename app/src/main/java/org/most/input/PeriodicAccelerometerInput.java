package org.most.input;
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

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.most.DataBundle;
import org.most.MoSTApplication;
import org.most.utils.DelayedWakeLockRelease;

import java.util.Date;

public class PeriodicAccelerometerInput extends PeriodicInput implements SensorEventListener {

    private final static String TAG = PeriodicAccelerometerInput.class.getSimpleName();

    /**
     * The Constant DEBUG.
     */
    private final static boolean DEBUG = false;

    /**
     * The _sensor manager.
     */
    private SensorManager _sensorManager = null;

    /**
     * The _sensor.
     */
    private Sensor _sensor = null;

    /**
     * The _sensor rate.
     */
    private int _sensorRate = 0;

    private Thread _deactivateAccelerometer;

    public final static String KEY_PERIODIC_ACCELERATIONS = "PeriodicInputAccelerometer.Accelerations";

    public final static String PREF_KEY_PERIODIC_ACCELEROMETER_RATE = "PeriodicInputAccelerometer.Rate";
    public final static int PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE = SensorManager.SENSOR_DELAY_FASTEST;


    /**
     * {@link SharedPreferences} key to set the statistics monitoring period.
     */
    public final static String PREF_KEY_PERIODIC_ACCELEROMETER_PERIOD = "PeriodicAccelerometerInputMs";

    /**
     * Default net traffic monitoring interval in milliseconds. Currently set to
     * {@value #PREF_DEFAULT_PERIODIC_ACCELEROMETER_PERIOD}.
     */
    public final static int PREF_DEFAULT_PERIODIC_ACCELEROMETER_PERIOD = 1000 * 60 * 1; //1 MINUTE

    public final static String KEY_PERIODIC_ACCELEROMETER_TYPE = "PeriodicAccelerometerInput.Accelerometer";

    /**
     * @param context
     */
    public PeriodicAccelerometerInput(MoSTApplication context) {
        super(context, context.getSharedPreferences(MoSTApplication.PREF_INPUT, Context.MODE_PRIVATE).getInt(
                PREF_KEY_PERIODIC_ACCELEROMETER_PERIOD, PREF_DEFAULT_PERIODIC_ACCELEROMETER_PERIOD));
    }

    @Override
    public void onInit() {
        checkNewState(State.INITED);

        if (_sensorManager == null) {
            _sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        }
        if (_sensor == null) {
            _sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (DEBUG)
            Log.d(TAG, "onInit()");

        super.onInit();
    }

    /*
     * (non-Javadoc)
     *
     * @see it.unibo.mobilesensingframework.input.Input#pause()
     */
    public void onDeactivate() {
        checkNewState(State.DEACTIVATED);
        try {
            _deactivateAccelerometer.interrupt();
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
        if (DEBUG)
            Log.d(TAG, "onDeactivate()");
        super.onDeactivate();
    }

    /*
     * (non-Javadoc)
     *
     * @see it.unibo.mobilesensingframework.input.IInput#stop()
     */
    @Override
    public void onFinalize() {
        checkNewState(State.FINALIZED);
        if (_sensorManager != null) {
            _sensorManager = null;
        }
        if (_sensor != null) {
            _sensor = null;
        }

        if (DEBUG)
            Log.d(TAG, "onFinalize()");

        super.onFinalize();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
     * .Sensor, int)
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.hardware.SensorEventListener#onSensorChanged(android.hardware
     * .SensorEvent)
     */
    public void onSensorChanged(SensorEvent event) {
        if (DEBUG) {
            Log.v(TAG, "Received accelerometer data");
        }

        if (!getState().equals(Input.State.ACTIVATED)) {
            return;
        }

        DataBundle b = _bundlePool.borrowBundle();
        b.allocateFloatArray(KEY_PERIODIC_ACCELERATIONS, 3);
        float[] data = b.getFloatArray(KEY_PERIODIC_ACCELERATIONS);
        data[0] = event.values[0];
        data[1] = event.values[1];
        data[2] = event.values[2];

        b.putLong(Input.KEY_TIMESTAMP, new Date(event.timestamp / 1000000).getTime());
        b.putInt(Input.KEY_TYPE, Input.Type.PERIODIC_ACCELEROMETER.toInt());

        post(b);
    }

    /**
     * Gets the sensor rate.
     *
     * @return the _sensor rate
     */
    public int getSensorRate() {
        return _sensorRate;
    }

    /**
     * Sets the sensor rate.
     *
     * @param sensorRate the new _sensor rate
     */
    public void setSensorRate(int sensorRate) {
        this._sensorRate = sensorRate;
        _sensorManager.unregisterListener(this, _sensor);
        _sensorManager.registerListener(this, _sensor, _sensorRate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof InputAccelerometer)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public void workToDo() {
        _sensorRate = getContext().getSharedPreferences(MoSTApplication.PREF_INPUT, Context.MODE_PRIVATE).getInt(
                PREF_KEY_PERIODIC_ACCELEROMETER_RATE, PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE);
        Object contest = getContext();
        try {
            System.out.println("++++++++++ TRY PERIODIC ACCELEROMETER INPUT _sensorRate= " + _sensorRate);
            System.out.println("++++++++++ TRY PERIODIC ACCELEROMETER INPUT _sensor=" + _sensor);
            System.out.println("++++++++++ TRY PERIODIC ACCELEROMETER INPUT _sensorManager: " + _sensorManager.toString());
            System.out.println("++++++++++ TRY PERIODIC ACCELEROMETER INPUT PREF_KEY_PERIODIC_ACCELEROMETER_RATE: " + PREF_KEY_PERIODIC_ACCELEROMETER_RATE);
            System.out.println("++++++++++ TRY PERIODIC ACCELEROMETER INPUT PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE: " + PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE);
            System.out.println("++++++++++ CONTEXT = " + contest);

            _sensorManager.registerListener(this, _sensor, _sensorRate);
        } catch (Exception e) {
            System.out.println("*****CATCH PERIODIC ACCELEROMETER INPUT _sensorRate= " + _sensorRate);
            System.out.println("*****CATCH PERIODIC ACCELEROMETER INPUT _sensor= " + _sensor);
            System.out.println("*****CATCH PERIODIC ACCELEROMETER INPUT _sensorManager: " + _sensorManager.toString());
            System.out.println("*****CATCH PERIODIC ACCELEROMETER INPUT PREF_KEY_PERIODIC_ACCELEROMETER_RATE= " + PREF_KEY_PERIODIC_ACCELEROMETER_RATE);
            System.out.println("*****CATCH PERIODIC ACCELEROMETER INPUT PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE= " + PREF_DEFAULT_PERIODIC_ACCELEROMETER_RATE);
            System.out.println("***** CONTEXT = " + contest);
        }


        getContext().getWakeLockHolder().acquireWL();
        _deactivateAccelerometer = new DeactivateAccelerometer(30000);
        _deactivateAccelerometer.start();
        new DelayedWakeLockRelease(getContext(), 30000).start();

        scheduleNextStart();
    }

    @Override
    public Type getType() {
        return Input.Type.PERIODIC_ACCELEROMETER;
    }


    public class DeactivateAccelerometer extends Thread {

        private long _delay;


        public DeactivateAccelerometer(long delay) {
            _delay = delay;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(_delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (_sensorManager != null) {
                try {
                    _sensorManager.unregisterListener(PeriodicAccelerometerInput.this, _sensor);
                } catch (Exception e) {
                    Log.e("Most", null, e);
                }
            }

        }
    }


}
