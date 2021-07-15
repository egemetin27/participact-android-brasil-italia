package br.com.participact.participactbrasil.modules.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bergmannsoft.rest.Response;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.network.SessionManager;

public class TokenInsertActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_insert);
    }

    public void closeClick(View view) {
        finish();
    }

    public void confirmTokenClick(View view) {
        String token = getEditTextValue(R.id.textToken);
        if (token != null && token.trim().length() > 0) {
            token = token.trim();
            showProgress();
            SessionManager.getInstance().confirmToken(token, new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    if (response != null) {
                        if (response.isSuccess()) {
                            finish();
                        } else {
                            showError(response.getMessage());
                        }
                    } else {
                        showError(getString(R.string.network_response_error_default_message));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    showError(t.getLocalizedMessage());
                }
            });
        }
    }

    public void shootClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setPrompt(getString(R.string.read_qrcode));
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                final String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                uiHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        setEditTextValue(R.id.textToken, getCode(contents));
                    }
                });

                Crashlytics.log(Log.DEBUG, TAG, "OK, CONTENT: " + contents + ", FORMAT: " + format);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Crashlytics.log(Log.DEBUG, TAG, "CANCEL");
            }
        }
    }

    public String getCode(String link) {
        if (link != null && link.contains("/")) {
            return link.substring(link.lastIndexOf("/") + 1);
        }
        return link;
    }

}
