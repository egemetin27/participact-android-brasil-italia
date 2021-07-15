package com.bergmannsoft.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by fabiobergmann on 4/26/16.
 */
public class Restful {

    private static final String TAG = Restful.class.getSimpleName();

    public enum RequestType {
        POST, GET
    }

    public interface OnResponseListener<T extends Response> {
        void onResponse(T response);
    }

    // region POST

    public static <T extends Response> void post(final Context context, final String url, final Request request, final Class<T> responseClassType, final String authorization, final OnResponseListener listener) {
        new AsyncTask<Void, Integer, T>() {

            @Override
            protected T doInBackground(Void... voids) {
                return postSync(context, url, request, responseClassType, authorization);
            }

            @Override
            protected void onPostExecute(T response) {
                if (response == null) {
                    listener.onResponse(Response.getDefaultErrorResponse(context, responseClassType));
                } else {
                    listener.onResponse(response);
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

            }
        }.execute();
    }

    public static <T extends Response> T postSync(final Context context, final String url, Request request, final Class<T> responseClassType, String authorization) {
        HttpURLConnection conn = getURLConnection(url, RequestType.POST, authorization);
        if (conn == null) return null;
        T response = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        ByteArrayOutputStream baos = null;
        try {

            // write data
            String data = new Gson().toJson(request);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.write(data.getBytes(Charset.forName("UTF-8")));
            dos.flush ();

            // read response
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "response code: " + responseCode);
            String jsonString;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                jsonString = readResponse(conn.getInputStream());
            } else {
                jsonString = readResponse(conn.getErrorStream());
            }

            response = new Gson().fromJson(jsonString, responseClassType);

        } catch (Exception e) {
            Log.e(TAG, null, e);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    Log.e(TAG, null, e);
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    Log.e(TAG, null, e);
                }
            }
        }
        return response;
    }

    // endregion

    private static HttpURLConnection getURLConnection(String address, RequestType type, String authorization) {

        try {
            URL url;
            HttpURLConnection conn;
            url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            if (type == RequestType.POST)
                conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization);
            }
            conn.connect();
            return conn;
        } catch (Exception e) {
            Log.e(TAG, null, e);
            return null;
        }

    }

    private static String readResponse(InputStream is) {
        DataInputStream dis = null;
        ByteArrayOutputStream baos = null;
        try {

            // read response
            dis = new DataInputStream(is);
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024 * 1024 * 10];
            int read;
            while ((read = dis.read(buff, 0, buff.length)) > 0) {
                baos.write(buff, 0, read);
            }

            String jsonString = new String(baos.toByteArray(), Charset.forName("UTF-8"));

            return jsonString;

        } catch (Exception e) {
            Log.e(TAG, null, e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    Log.e(TAG, null, e);
                }
            }
        }
        return null;
    }

}
