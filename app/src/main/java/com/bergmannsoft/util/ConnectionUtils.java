package com.bergmannsoft.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bergmannsoft.location.BSLocationManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.participact.participactbrasil.modules.enums.Constants;

public class ConnectionUtils {

    private static final String TAG = ConnectionUtils.class.getSimpleName();

    public static boolean isWiFiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connManager != null) {
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return mWifi != null && mWifi.isConnected();
            }
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo i = conMgr.getActiveNetworkInfo();
            if (i == null)
                return false;
            if (!i.isConnected())
                return false;
            if (!i.isAvailable())
                return false;
            return true;
        }
        return false;
    }

    public interface CheckOnlineCallback {
        void onResponse(boolean isOnline);
    }

    public static void isOnline(final Context context, final CheckOnlineCallback callback) {
        isOnline(context, String.format("%s/v2/", Constants.HOST), callback);
    }

    public static void isOnline(final Context context, final String url, final CheckOnlineCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected(context)) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL(url).openConnection());
                        urlc.setRequestProperty("User-Agent", "ParticipACT");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(60000);
                        urlc.connect();
                        final int responseCode = urlc.getResponseCode();

                        replyIsOnline(callback, responseCode == 200);

                    } catch (IOException e) {
                        Log.e(TAG, "Error: ", e);
                        replyIsOnline(callback, false);
                    }
                } else {
                    replyIsOnline(callback, false);
                }

            }
        }).start();
    }

    private static void replyIsOnline(final CheckOnlineCallback callback, final boolean isOnline) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse (isOnline);
            }
        });
    }

}
