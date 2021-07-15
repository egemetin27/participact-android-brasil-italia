package com.bergmannsoft.support;

import android.content.Context;
import android.content.Intent;

public class Share {

    public static void url(Context context, String url, String title, String subject) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(i, title));
    }

}