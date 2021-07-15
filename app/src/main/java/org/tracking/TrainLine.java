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
 * Represents a Train Line.
 * @author giuseppe giammarino
 */

package org.tracking;

public class TrainLine {
    private String lineName;
    private int lineIdOSM;

    public TrainLine(int lineIdOSM, String lineName) {
        super();
        this.lineIdOSM = lineIdOSM;
        this.lineName = lineName;
    }

    public int getLineIdOSM() {
        return lineIdOSM;
    }

    public void setLineIdOsm(int lineIdOSM) {
        this.lineIdOSM = lineIdOSM;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    @Override
    public String toString() {
        return "TrainLine [lineIdOSM=" + lineIdOSM + ", lineName=" + lineName + "]";
    }

}
