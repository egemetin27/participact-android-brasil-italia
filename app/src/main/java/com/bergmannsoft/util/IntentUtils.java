package com.bergmannsoft.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Locale;


/**
 * Created by fabiobergmann on 03/01/17.
 */

public class IntentUtils {

    public static final String TAG = IntentUtils.class.getSimpleName();

    public static void call(Context context, String phoneNumber) throws IllegalArgumentException, SecurityException {
        if (Utils.isValidNotEmpty(phoneNumber)) {
//            throw new IllegalArgumentException(context.getString(R.string.invalid_phone_number) + " '" + phoneNumber + "'");
        } else {
//            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//            context.startActivity(intent);
        }
    }

    public static void directions(Context context, String address, double latitude, double longitude) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", address, latitude, longitude), "UTF-8");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

}
