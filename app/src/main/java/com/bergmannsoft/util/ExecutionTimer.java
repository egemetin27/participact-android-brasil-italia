package com.bergmannsoft.util;

/**
 * Created by fabiobergmann on 27/12/16.
 */

public class ExecutionTimer {

    private long startTime;

    private ExecutionTimer() {
        startTime = System.currentTimeMillis();
    }

    public static ExecutionTimer start() {
        ExecutionTimer timer = new ExecutionTimer();
        return timer;
    }

    public String stop() {
        float diff = System.currentTimeMillis() - startTime;
        String symbol = " milliseconds";
        if (diff > 1000) {
            diff /= 1000;
            symbol = " second(s)";
        }
        if (diff > 60) {
            diff /= 60;
            symbol = " minute(s)";
        }
        return diff + symbol;
    }

}
