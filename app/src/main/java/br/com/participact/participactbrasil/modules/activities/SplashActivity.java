package br.com.participact.participactbrasil.modules.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.util.ConnectionUtils;
import com.bergmannsoft.util.Utils;
import com.crashlytics.android.Crashlytics;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.BuildConfig;
import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.MeResponse;
import br.com.participact.participactbrasil.modules.network.requests.SignInResponse;
import br.com.participact.participactbrasil.modules.support.UserSettings;
import io.fabric.sdk.android.services.common.Crash;

public class SplashActivity extends BaseActivity {

    private int loginTries = 0;

    private Timer timeoutTimer;
    private boolean signed = false;

    private boolean needStartApp() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1024);

        try {
            if (!tasksInfo.isEmpty()) {
                final String ourAppPackageName = getPackageName();
                ActivityManager.RunningTaskInfo taskInfo;
                final int size = tasksInfo.size();
                for (int i = 0; i < size; i++) {
                    taskInfo = tasksInfo.get(i);
                    if (ourAppPackageName.equals(taskInfo.baseActivity.getPackageName())) {
                        return taskInfo.numActivities == 1;
                    }
                }
            }
        } catch (SecurityException e) {
            // Some ROM can weirdly don't allow it
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "username: " + UserSettings.getInstance().getUsername());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        setTextViewValue(R.id.version, BuildConfig.VERSION_NAME);

        if (!needStartApp()) {
            finish();
            return;
        }

        doLogin();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!signed) {
                    if (UserSettings.getInstance().getAccessToken() != null) {
                        go();
                    } else {
                        showError();
                    }
                }
            }
        }, 60000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        testClicks = 0;
    }

    private void stopTimeoutTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
        }
    }

    private void doLogin() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.textError).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonRetry).setVisibility(View.INVISIBLE);
        final long t1 = System.currentTimeMillis();
        login(t1);
    }

    private void login(final long t1) {
        ConnectionUtils.isOnline(this, new ConnectionUtils.CheckOnlineCallback() {
            @Override
            public void onResponse(boolean isOnline) {
//                if (true) {
//                    showError();
//                    return;
//                }
                if (isOnline) {
                    stopTimeoutTimer();
                    SessionManager.getInstance().signIn(new SessionManager.RequestCallback<SignInResponse>() {
                        @Override
                        public void onResponse(SignInResponse response) {
                            if (response == null) {
                                if (loginTries == 0) {
                                    loginTries++;
                                    UserSettings.getInstance().setAccessToken(null);
                                    login(t1);
                                } else {
                                    showError();
                                }
                                return;
                            }
                            if (response.isSuccess()) {
                                Crashlytics.setUserName(UserSettings.getInstance().getUsername());
                                Crashlytics.setUserEmail(UserSettings.getInstance().getEmail());
                                Crashlytics.setString("Name", UserSettings.getInstance().getName());
                                UserSettings.getInstance().setAccessToken(response.getToken());
                                me(t1);
                            } else {
                                if (loginTries == 0) {
                                    loginTries++;
                                    UserSettings.getInstance().setAccessToken(null);
                                    login(t1);
                                } else {
                                    showError();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showError();
                        }
                    });
                } else {
                    if (UserSettings.getInstance().getAccessToken() != null) {
                        go();
                    } else {
                        showError();
                    }
                }
            }
        });

    }

    private void showError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                findViewById(R.id.textError).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonRetry).setVisibility(View.VISIBLE);
            }
        });

    }

    private void me(final long t1) {
        Log.d(TAG, "me");
        SessionManager.getInstance().me(new SessionManager.RequestCallback<MeResponse>() {
            @Override
            public void onResponse(MeResponse response) {
                if (response != null && response.isSuccess()) {
                    response.save();
                }
                long diff = System.currentTimeMillis() - t1;
                long time = 2000 - diff;
                if (time > 0) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            go();
                        }
                    }, 2000);
                } else {
                    go();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });
    }

    private void go() {
        if (signed) {
            return;
        }
        stopTimeoutTimer();
        signed = true;
        if (Utils.hasAllNecessaryPermissions(SplashActivity.this)) {
            present(MainActivity.class);
        } else {
            present(PermissionsActivity.class);
        }
        finish();
    }

    public void retryAction(View view) {
        UserSettings.getInstance().setAccessToken(null);
        doLogin();
    }

    private int testClicks = 0;

    public void testAction(View view) {
        testClicks++;
        if (testClicks >= 4) {
            testClicks = 0;
            present(TestActivity.class);
        }
    }
}
