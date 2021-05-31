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

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.most.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ORIGINALBusStopFinder {
    public static final String TAG = ORIGINALBusStopFinder.class.getSimpleName();
    public static final String API_URL = "http://whooma.net/MoST/getBusStop.php?";
    private Utils utils;
    private int version;

    public ORIGINALBusStopFinder() {
        super();
    }

    public List<BusStop> searchBusStops(Utils utils, int version, GPSPosition position, double radius) {
        this.utils = utils;
        this.version = version;
        List<BusStop> bus_stops = new ArrayList<BusStop>();
        String json = loadJson(position, radius);

        try {
            JSONObject json_stops = new JSONObject(json);
            JSONArray stops = json_stops.getJSONArray("BusStops");
            for (int i = 0; i < stops.length(); i++) {
                List<BusLine> bus_lines = new ArrayList<BusLine>();
                JSONObject stop_obj = stops.getJSONObject(i);
                JSONArray json_lines = stop_obj.getJSONArray("lines");
                for (int j = 0; j < json_lines.length(); j++) {
                    BusLine line = new BusLine(json_lines.getString(j));
                    if (!bus_lines.contains(line))
                        bus_lines.add(line);
                }
                JSONObject json_position = stop_obj.getJSONObject("GPSPosition");
                GPSPosition new_position = new GPSPosition(json_position.getDouble("latitude"), json_position.getDouble("longitude"), 1, System.currentTimeMillis() / 1000);
//				bus_stops.add(new BusStop(new_position, stop_obj.getString("name"), stop_obj.getString("stop_code"), stop_obj.getString("location"), bus_lines, stop_obj.getInt("zone_code"), radius));
            }
        } catch (Exception e) {
            e.printStackTrace();
            utils.appendLog("BusStopFinder.searchBusStops() " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY).format(new Date().getTime()) + "\n\tException: " + e.toString() + "\n", "errors", version);
        }
        return bus_stops;
    }

    private String loadJson(GPSPosition position, double radius) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        String request_url = API_URL + "latitude=" + position.getLatitude() + "&longitude=" + position.getLongitude() + "&radius=" + radius;
        HttpGet httpGet = new HttpGet(request_url);
        try {
            HttpParams httpParams = new BasicHttpParams();
            //Setting timeout 0,3 secs because this part of the App doesn't work
            HttpConnectionParams.setConnectionTimeout(httpParams, 300);
            client = new DefaultHttpClient(httpParams);
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
            } else {
                Log.e(TAG, "Failed to download file. StatusCode=" + statusCode);
                utils.appendLog("BusStopFinder.loadJson() " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY).format(new Date().getTime()) + "\n\tFailed to download file. StatusCode=" + statusCode + "\n", "errors", version);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            utils.appendLog("BusStopFinder.loadJson() " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY).format(new Date().getTime()) + "\n\tClientProtocolException: " + e.toString() + "\n", "errors", version);
        } catch (IOException e) {
            e.printStackTrace();
            utils.appendLog("BusStopFinder.loadJson() " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY).format(new Date().getTime()) + "\n\tIOException: " + e.toString() + "\n", "errors", version);
        }
        return builder.toString();
    }
}
