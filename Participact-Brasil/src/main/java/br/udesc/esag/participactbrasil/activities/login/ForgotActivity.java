package br.udesc.esag.participactbrasil.activities.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.ForgotRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.ForgotRestResult;
import br.udesc.esag.participactbrasil.network.request.ForgotRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

/**
 * Created by fabiobergmann on 09/05/17.
 */

public class ForgotActivity extends AppCompatActivity {

    private static final String TAG = ForgotActivity.class.getSimpleName();
    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        findViewById(R.id.btSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendRequest() {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        if (!Utils.isValidEmail(email)) {
            AlertDialogUtils.showAlert(this, getString(R.string.alert_provide_your_email));
        } else {

            ProgressDialog.show(this, getString(R.string.sending));

            ForgotRequest request = new ForgotRequest(this, new ForgotRestRequest(email));
            _contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ForgotRestResult>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, null, spiceException);
                    ProgressDialog.dismiss(ForgotActivity.this);
                    AlertDialogUtils.showError(ForgotActivity.this, spiceException.getLocalizedMessage());
                }

                @Override
                public void onRequestSuccess(final ForgotRestResult result) {
                    ProgressDialog.dismiss(ForgotActivity.this);
                    if (result == null) {
                        // It was not supposed to be null. Something is really wrong here.
                        AlertDialogUtils.showError(ForgotActivity.this, getString(R.string.alert_signup_null_response));
                        return;
                    }

                    Log.d(TAG, "IsSuccess: " + result.isSuccess());

                    AlertDialogUtils.createDialog(ForgotActivity.this, getString(R.string.alert), result.getMessage(), "OK", null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (result.isSuccess()) {
                                finish();
                            }
                        }
                    }, null).show();

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!_contentManager.isStarted()) {
            _contentManager.start(this);
        }
    }

    @Override
    protected void onStop() {
        if (_contentManager.isStarted()) {
            _contentManager.shouldStop();
        }
        super.onStop();
    }
}
