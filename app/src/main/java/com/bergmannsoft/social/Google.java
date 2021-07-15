package com.bergmannsoft.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class Google extends Social {

    private static final String TAG = Google.class.getSimpleName();
    private static Google instance;
    private Social.LoginCallback callback;
    private final Context context;

    private static final int RC_SIGN_IN = 5000;
    private GoogleApiClient mGoogleApiClient;

    private Google(Context context) {
        this.context = context;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, connectionResult.getErrorMessage());
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public static Google getInstance(Context context) {
        if (instance == null) {
            instance = new Google(context);
        }
        return instance;
    }

    @Override
    public void logIn(Activity activity, LoginCallback callback) {
        this.callback = callback;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void logOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status != null)
                                Log.i(TAG, "" + status.getStatusMessage());
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount acct = result.getSignInAccount();
                final SocialUser user = SocialUser.withGoogle(acct);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String token = GoogleAuthUtil.getToken(context, acct.getAccount(), "oauth2:" + Scopes.EMAIL);
                            user.setAccessToken(token);
                            ((FragmentActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(user);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onError(e);
                        } catch (GoogleAuthException e) {
                            e.printStackTrace();
                            callback.onError(e);
                        }
                    }
                }).start();

            } else {
                String status = "status is null";
                if (result.getStatus() != null) {
                    Status st = result.getStatus();
                    status = "Status code: " + st.getStatusCode() + ", status message: " + st.getStatusMessage() + ", resolution: " + st.getResolution();
                }
                callback.onError(new Exception("Google Sign-In error - " + status));
            }
        }
    }

}
