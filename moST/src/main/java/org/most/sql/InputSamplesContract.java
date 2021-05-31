package org.most.sql;

import android.provider.BaseColumns;

public class InputSamplesContract {
    public InputSamplesContract() {
    }

    public static abstract class InputSamplesEntry implements BaseColumns {
        public static final String TABLE_NAME = "input_samples";
        public static final String TIMESTAMP_COLUMN = "timestamp";
        public static final String NORM_COLUMN = "norm";
        public static final String SAMPLES_COLUMN = "samples";
        public static final String GPS_DISTANCE_COLUMN = "gps_distance";
        public static final String ACTIVITY_TYPE_COLUMN = "activity";
        public static final String ACTIVITY_POLE_COLUMN = "activity_pole";
    }

}
