/*
 * Copyright (C) 2012 <copyright_owner>
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

/**
 * @author marcomoschettini
 */

package org.tracking;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GPSPosition {
    private double latitude;
    private double longitude;
    private double accuracy;
    private long timestamp;

    public GPSPosition(double latitude, double longitude, double accuracy, long timestamp) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.setTimestamp(timestamp);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public String toString() {
        return "GPSPosition [latitude=" + latitude + ", longitude=" + longitude
                + ", accuracy=" + accuracy + ", time=" + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date(this.timestamp * 1000)) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GPSPosition other = (GPSPosition) obj;
        if (Double.doubleToLongBits(latitude) != Double
                .doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double
                .doubleToLongBits(other.longitude))
            return false;
        return true;
    }

}
