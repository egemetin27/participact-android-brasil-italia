package com.bergmannsoft.util;

import android.util.Log;

import com.bergmannsoft.application.BApplication;

/**
 * Created by fabiobergmann on 11/01/17.
 */

public class NumberUtils {

    public static boolean isInteger(String str) {
        try {
            int i = Integer.parseInt(str);
            Log.v(BApplication.TAG, "This is the number: " + i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            double i = Double.parseDouble(str);
            Log.v(BApplication.TAG, "This is the number: " + i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFloat(String str) {
        try {
            float i = Float.parseFloat(str);
            Log.v(BApplication.TAG, "This is the number: " + i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNumber(String str) {
        return isInteger(str) || isDouble(str) || isFloat(str);
    }

    public static int parseInt(String str) {
        return isInteger(str) ? Integer.parseInt(str) : 0;
    }
}
