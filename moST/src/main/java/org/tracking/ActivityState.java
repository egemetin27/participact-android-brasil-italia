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

/**
 * Represent a state for the Activity Recognition
 *
 * @author marcomoschettini
 */
public class ActivityState {
    private String state;
    private String pole;
    private long timestamp;

    private double score;

    public ActivityState(String state, String pole, double score, long timestamp) {
        super();
        this.state = state;
        this.timestamp = timestamp;
        this.setPole(pole);
        this.setScore(score);
    }

    public boolean isWalking() {
        if (this.state.equals("walk"))
            return true;
        else
            return false;
    }

    public boolean isStill() {
        if (this.state.equals("still"))
            return true;
        else
            return false;
    }

    public boolean isRunning() {
        if (this.state.equals("run"))
            return true;
        else
            return false;
    }

    public boolean isOnVehicle() {
        if (this.state.equals("vehicle"))
            return true;
        else
            return false;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long from) {
        this.timestamp = from;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getPole() {
        return pole;
    }

    public void setPole(String pole) {
        this.pole = pole;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public String toString() {
        return "Activity [state=" + state + ", pole=" + pole
                + ", timestamp=" + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date(timestamp * 1000)) + ", score=" + score + "]";
    }


}
