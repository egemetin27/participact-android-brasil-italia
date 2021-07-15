package com.bergmannsoft.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import br.com.participact.participactbrasil.modules.App;

public class UniqueID {

    public static String get(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getAlternativeID();
        } else {
            try {
                String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (id == null || id.trim().length() == 0) {
                    return getAlternativeID();
                } else {
                    return id;
                }
            } catch (Exception e) {
                return getUUID();
            }
        }
    }

    private static String getAlternativeID() {
        try {
            String mac = getMacAddr();
            if (mac.trim().length() == 0) {
                return getUUID();
            } else {
                return mac.replaceAll(  ":", "").replaceAll(" ", "");
            }
        } catch (Exception e) {
            return getUUID();
        }
    }

    private static String getUUID() {
        String uuid = App.getInstance().getPreferenceString("android_user_unique_id", UUID.randomUUID().toString());
        App.getInstance().savePreferenceString(uuid, "android_user_unique_id");
        return uuid;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

}
