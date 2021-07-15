package com.bergmannsoft.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by fabiobergmann on 23/03/18.
 */

public class Facebook extends Social {

    private static final String TAG = Facebook.class.getSimpleName();
    private static Facebook instance;
    private final Context context;

    private CallbackManager callbackManager;
    private Social.LoginCallback callback;

    private Facebook(Context context) {
        this.context = context;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        getData(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        callback.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        callback.onError(exception);
                    }
                });
    }

    public static Facebook getInstance(Context context) {
        if (instance == null) {
            instance = new Facebook(context);
        }
        return instance;
    }

    private void getData(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d(TAG, object.toString());

                    final SocialUser user = SocialUser.withFacebook(object);
                    user.setAccessToken(accessToken.getToken());

                    Bundle params = new Bundle();
                    params.putString("fields", "id,email,gender,picture.type(large)");
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    if (response != null) {
                                        try {
                                            JSONObject data = response.getJSONObject();
                                            if (data.has("picture")) {
                                                user.setPictureUrl(data.getJSONObject("picture").getJSONObject("data").getString("url"));
                                            }
                                            callback.onSuccess(user);
                                        } catch (Exception e) {
                                            callback.onError(e);
                                        }
                                    } else {
                                        callback.onError(new Exception("response is null"));
                                    }
                                }
                            }).executeAsync();

                } catch (Exception e) {
                    Log.e(TAG, null, e);
                    callback.onError(e);
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture,email");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    public void logIn(Activity activity, LoginCallback callback) {
        this.callback = callback;
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void logOut() {
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
