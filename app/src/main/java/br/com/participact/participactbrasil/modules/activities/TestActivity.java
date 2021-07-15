package br.com.participact.participactbrasil.modules.activities;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bergmannsoft.util.ConnectionUtils;
import com.crashlytics.android.Crashlytics;

import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.MeResponse;
import br.com.participact.participactbrasil.modules.network.requests.SignInResponse;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);

        startTesting();

    }

    private void startTesting() {
        setTextViewValue(R.id.textId, App.getInstance().getDeviceUuid());
        findViewById(R.id.progressId).setVisibility(View.INVISIBLE);

        setTextViewValue(R.id.textModel, Build.MODEL);
        findViewById(R.id.progressModel).setVisibility(View.INVISIBLE);

        setTextViewValue(R.id.textManufacturer, Build.MANUFACTURER);
        findViewById(R.id.progressManufacturer).setVisibility(View.INVISIBLE);

        setTextViewValue(R.id.textVersionOs, String.valueOf(Build.VERSION.SDK_INT));
        findViewById(R.id.progressVersionOs).setVisibility(View.INVISIBLE);

        int versionCode = -1;
        String versionName = "";

        try {
            PackageInfo packageInfo = App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTextViewValue(R.id.textVersionApp, versionName + " (" + versionCode + ")");
        findViewById(R.id.progressVersionApp).setVisibility(View.INVISIBLE);

        setTextViewValue(R.id.textConnectionType, ConnectionUtils.isWiFiConnected(this) ? "Wi-Fi" : "4G");
        findViewById(R.id.progressConnectionType).setVisibility(View.INVISIBLE);

        findViewById(R.id.progressConnectionTest1).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressConnectionTest2).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressConnectionTest3).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressConnectionTest4).setVisibility(View.INVISIBLE);
        findViewById(R.id.textConnectionTest1).setVisibility(View.INVISIBLE);
        findViewById(R.id.textConnectionTest2).setVisibility(View.INVISIBLE);
        findViewById(R.id.textConnectionTest3).setVisibility(View.INVISIBLE);
        findViewById(R.id.textConnectionTest4).setVisibility(View.INVISIBLE);

        testConnection1();

    }

    private void setConnectionTestResult(final boolean success, final int textId, final int progressId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextViewValue(textId, success ? "OK" : "Falhou");
                findViewById(progressId).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void testConnection1() {
        findViewById(R.id.progressConnectionTest1).setVisibility(View.VISIBLE);
        findViewById(R.id.textConnectionTest1).setVisibility(View.VISIBLE);
        ConnectionUtils.isOnline(this, "https://www.google.com", new ConnectionUtils.CheckOnlineCallback() {
            @Override
            public void onResponse(boolean isOnline) {
                setConnectionTestResult(isOnline, R.id.textConnectionTest1, R.id.progressConnectionTest1);
                testConnection2();
            }
        });
    }

    private void testConnection2() {
        findViewById(R.id.progressConnectionTest2).setVisibility(View.VISIBLE);
        findViewById(R.id.textConnectionTest2).setVisibility(View.VISIBLE);
        ConnectionUtils.isOnline(this, new ConnectionUtils.CheckOnlineCallback() {
            @Override
            public void onResponse(boolean isOnline) {
                setConnectionTestResult(isOnline, R.id.textConnectionTest2, R.id.progressConnectionTest2);
                testConnection3();
            }
        });
    }

    private int loginTries = 0;

    private void testConnection3() {
        findViewById(R.id.progressConnectionTest3).setVisibility(View.VISIBLE);
        findViewById(R.id.textConnectionTest3).setVisibility(View.VISIBLE);
        SessionManager.getInstance().signIn(new SessionManager.RequestCallback<SignInResponse>() {
            @Override
            public void onResponse(SignInResponse response) {
                if (response == null) {
                    if (loginTries == 0) {
                        loginTries++;
                        UserSettings.getInstance().setAccessToken(null);
                        testConnection3();
                    } else {
                        setConnectionTestResult(false, R.id.textConnectionTest3, R.id.progressConnectionTest3);
                        testConnection4(false);
                    }
                    return;
                }
                if (response.isSuccess()) {
                    setConnectionTestResult(true, R.id.textConnectionTest3, R.id.progressConnectionTest3);
                    testConnection4(true);
                } else {
                    if (loginTries == 0) {
                        loginTries++;
                        UserSettings.getInstance().setAccessToken(null);
                        testConnection3();
                    } else {
                        setConnectionTestResult(false, R.id.textConnectionTest3, R.id.progressConnectionTest3);
                        testConnection4(false);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                setConnectionTestResult(false, R.id.textConnectionTest3, R.id.progressConnectionTest3);
                testConnection4(false);
            }
        });
    }

    private void testConnection4(boolean test3Success) {
        findViewById(R.id.progressConnectionTest4).setVisibility(View.VISIBLE);
        findViewById(R.id.textConnectionTest4).setVisibility(View.VISIBLE);
        if (test3Success) {
            SessionManager.getInstance().me(new SessionManager.RequestCallback<MeResponse>() {
                @Override
                public void onResponse(MeResponse response) {
                    if (response != null && response.isSuccess()) {
                        setConnectionTestResult(true, R.id.textConnectionTest4, R.id.progressConnectionTest4);
                    } else {
                        setConnectionTestResult(false, R.id.textConnectionTest4, R.id.progressConnectionTest4);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    setConnectionTestResult(false, R.id.textConnectionTest4, R.id.progressConnectionTest4);
                }
            });
        } else {
            setConnectionTestResult(false, R.id.textConnectionTest4, R.id.progressConnectionTest4);
        }
    }

    public void backAction(View view) {
        finish();
    }
}
