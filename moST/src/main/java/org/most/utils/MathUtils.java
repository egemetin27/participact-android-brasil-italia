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

/**
 * @author marcomoschettini
 */

package org.most.utils;

import org.tracking.GPSPosition;

public class MathUtils {
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

    public static double[] normalize(double[] input) {
        double norm = 0;
        norm = getNorm(input);

        for (int i = 0; i < input.length; i++)
            input[i] /= norm;

        return input;
    }

    public static Complex[] fft(double[] input) {
        Complex[] tofft;
        tofft = new Complex[input.length];

        for (int i = 0; i < input.length; i++)
            tofft[i] = new Complex(input[i], 0);

        return FFT.fft(tofft);
    }

    public static double getNorm(double[] input) {
        double sum = 0;
        for (int i = 0; i < input.length; i++)
            sum += Math.pow(input[i], 2);
        return Math.sqrt(sum);
    }

    public static double[] verticalShift(double[] input) {
        double avg = 0;
        for (int i = 0; i < input.length; i++)
            avg += input[i];

        avg /= input.length;

        //Shifting on the x axis
        for (int i = 0; i < input.length; i++)
            input[i] -= avg;

        return input;
    }

    public static double dot(double[] a, double[] b) {
        double dot = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
        }
        return dot;
    }

    public static double gauss_value(double avg, double std, double x) {
        double coeff = 1 / (std * Math.sqrt(2 * Math.PI));
        double exp = Math.exp(-Math.pow(x - avg, 2) / (2 * Math.pow(std, 2)));
        return exp * coeff;
    }

    public static double getDistance(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (AVERAGE_RADIUS_OF_EARTH * c) * 1000; //meters
    }

    public static double getSpeed(GPSPosition from, GPSPosition to) {
        double distance = getDistance(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude()); //meters
        double time = (to.getTimestamp() - from.getTimestamp()); //s
        return distance / time; //m/s
    }

    public static double sigma() {
        return 0;
    }

}
