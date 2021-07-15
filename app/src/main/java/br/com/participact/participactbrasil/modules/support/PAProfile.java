package br.com.participact.participactbrasil.modules.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.social.Social;
import com.bergmannsoft.social.SocialUser;
import com.bergmannsoft.util.FileCache;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.network.SessionManager;

public class PAProfile {

    private static final String TAG = PAProfile.class.getSimpleName();

    private static final Logger logger = Logger.getLogger(PAProfile.class);

    public interface SocialCompletion {
        void completion(boolean success, Bitmap profileImage);
    }

    public void facebookLogin(Context context, final SocialCompletion completion) {
        logger.info("facebookLogin");
        Social.facebook(context).logIn((FragmentActivity) context, new Social.LoginCallback() {

            @Override
            public void onSuccess(final SocialUser user) {
                logger.info("facebookLogin success");
                storeUserData(user);

                SessionManager.getInstance().sendFacebookToken(user.getAccessToken(), new SessionManager.RequestCallback<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null && response.isSuccess()) {
                            Log.i(TAG, "Success sending Facebook token.");
                            logger.info("Success sending Facebook token");
                        } else {
                            Log.e(TAG, "Error sending Facebook token. [1]");
                            logger.info("Error sending Facebook token. [1]");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "Error sending Facebook token.");
                        logger.info("Error sending Facebook token.");
                    }
                });

                if (user.hasProfilePicture()) {
                    App.getInstance().getFileCache().downloadAsync("profile_image", user.getPictureUrl(), new FileCache.FileCacheCallback() {
                        @Override
                        public void onDownloadDone(String key, final Bitmap bmp) {
                            logger.info("facebookLogin has picture");
                            completion.completion(true, bmp);
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("facebookLogin has NO picture");
                            completion.completion(true, null);
                        }
                    });
                }

            }

            @Override
            public void onCancel() {
                logger.info("facebookLogin cancel");
                completion.completion(false, null);
            }

            @Override
            public void onError(Exception e) {
                logger.error("facebookLogin error", e);
                completion.completion(false, null);
            }
        });
    }

    public void googleLogin(Context context, final SocialCompletion completion) {
        logger.info("googleLogin");
        Social.google(context).logIn((FragmentActivity) context, new Social.LoginCallback() {
            @Override
            public void onSuccess(final SocialUser user) {
                logger.info("googleLogin success");
                storeUserData(user);

                SessionManager.getInstance().sendGoogleToken(user.getAccessToken(), new SessionManager.RequestCallback<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null && response.isSuccess()) {
                            logger.info("Success sending Google token.");
                            Log.i(TAG, "Success sending Google token.");
                        } else {
                            logger.info("Error sending Google token. [1]");
                            Log.e(TAG, "Error sending Google token. [1]");
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "Error sending Google token.");
                        logger.info("Error sending Google token.");
                    }
                });

                if (user.hasProfilePicture()) {
                    App.getInstance().getFileCache().downloadAsync("profile_image", user.getPictureUrl(), new FileCache.FileCacheCallback() {
                        @Override
                        public void onDownloadDone(String key, final Bitmap bmp) {
                            completion.completion(true, bmp);
                            logger.info("googleLogin has picture");
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            completion.completion(true, null);
                            logger.info("googleLogin has NO picture");
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                logger.info("googleLogin cancel");
                completion.completion(false, null);
            }

            @Override
            public void onError(Exception e) {
                logger.error("googleLogin error", e);
                lastGoogleSigninError = e;
                completion.completion(false, null);
            }
        });
    }

    public Exception lastGoogleSigninError;

    private void storeUserData(SocialUser user) {
        // logger.info("\(user.name),\(user.email),\(user.ageRange)\(user.gender)")
        logger.info("storeUserData:" + user.getName() + "," + user.getEmail());
        UserSettings.getInstance().setName(user.getName());
        UserSettings.getInstance().setEmail(user.getEmail());
        UserSettings.getInstance().setPictureUrl(user.getPictureUrl());
        UserSettings.getInstance().setAgeRange("");
        UserSettings.getInstance().setGender("");
        UserSettings.getInstance().setIdentified(true);
        UserSettings.getInstance().setHasProfile(true);
    }

    public String welcomeMessage() {
        String text = "";
        if (UserSettings.getInstance().getName() != null && UserSettings.getInstance().getName().length() > 0) {
            text = String.format("Olá, %s", UserSettings.getInstance().getName());
        } else {
            text = "Olá\nVocê já criou a sua conta? É rápido e fácil!";
        }
        return text;
    }

    public Bitmap picture(final Context context, final FileCache.OnBitmapDownloadedListener listener) {
        Bitmap bmp = App.getInstance().getFileCache().getBitmap("profile_image");
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.profile_no_picture);
            App.getInstance().getFileCache().downloadAsync("profile_image", UserSettings.getInstance().getPictureUrl(), new FileCache.FileCacheCallback() {
                @Override
                public void onDownloadDone(String key, Bitmap bmp) {
                    if (bmp == null)
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.profile_no_picture);
                    listener.onDownloaded(bmp);
                }
            });
        }
        return bmp;
    }

}
