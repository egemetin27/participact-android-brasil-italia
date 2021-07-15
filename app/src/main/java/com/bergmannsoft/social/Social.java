package com.bergmannsoft.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fabiobergmann on 23/03/18.
 */

public abstract class Social {

    public interface LoginCallback {
        void onSuccess(SocialUser user);
        void onCancel();
        void onError(Exception e);
    }

    public static Facebook facebook(Context context) {
        return Facebook.getInstance(context);
    }

    public static Google google(Context context) {
        return Google.getInstance(context);
    }

    public abstract void logIn(Activity activity, LoginCallback callback);
    public abstract void logOut();
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

}
