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

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.R;

public class BusStopFinder {
    public static final String TAG = BusStopFinder.class.getSimpleName();
    //public static final String API_URL = "http://whooma.net/MoST/getBusStop.php?";
    public static final String API_URL = "https://pabrain.ing.unibo.it:8443/participact-server/opendata/tper/busstop?";

    public BusStopFinder() {
        super();
    }

    public List<BusStop> searchBusStops(GPSPosition position, double radius, Context context) {
        List<BusStop> bus_stops = new ArrayList<BusStop>();
        String json = loadJson(position, radius, context);
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
                bus_stops.add(new BusStop(new_position, stop_obj.getString("name"), stop_obj.getString("stop_code"), stop_obj.getString("location"), bus_lines, stop_obj.getInt("zone_code"), radius));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bus_stops;
    }


    private String loadJson(GPSPosition position, double radius, Context context) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = getNewHttpClient(context);

        String request_url = API_URL + "latitude=" + position.getLatitude() + "&longitude=" + position.getLongitude() + "&radius=" + radius;
        HttpGet httpGet = new HttpGet(request_url);
        try {
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

            } else
                Log.e(TAG, "Failed to download file");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public HttpClient getNewHttpClient(Context context) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = newSslSocketFactory(context);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static SSLSocketFactory newSslSocketFactory(Context context) {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = context.getResources().openRawResource(R.raw.keystore_labges);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "123456".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}
