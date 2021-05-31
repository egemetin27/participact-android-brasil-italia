package br.udesc.esag.participactbrasil.activities.login;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import br.com.bergmannsoft.gps.Locator;
import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.DeviceUtils;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.network.request.SignUpRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestResult;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;


public class UserRegistrationFragment extends Fragment {

    private static final String TAG = UserRegistrationFragment.class.getSimpleName();

    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    // Use to find user location and provide data like city and country.
    private Locator mLocator;
    private EditText txtLastName;

    public UserRegistrationFragment() {
        // Required empty public constructor
    }

    public static UserRegistrationFragment newInstance() {
        Bundle args = new Bundle();

        UserRegistrationFragment fragment = new UserRegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mLocator = new Locator(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!_contentManager.isStarted()) {
            _contentManager.start(getActivity());
        }
        if (!mLocator.isStarted()) {
            mLocator.start();
        }
    }

    @Override
    public void onStop() {
        if (_contentManager.isStarted()) {
            _contentManager.shouldStop();
        }
        if (mLocator.isStarted()) {
            mLocator.stop();
        }
        super.onStop();
    }

    private View fragment;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPasswordConfirmation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_user_registration, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        txtName = (EditText) fragment.findViewById(R.id.txtName);
        txtName.setOnFocusChangeListener(focusChangeListener);

        txtLastName = (EditText) fragment.findViewById(R.id.txtLastName);
        txtLastName.setOnFocusChangeListener(focusChangeListener);

        txtEmail = (EditText) fragment.findViewById(R.id.txtEmail);
        txtEmail.setOnFocusChangeListener(focusChangeListener);

        txtEmail = (EditText) fragment.findViewById(R.id.txtEmail);
        txtEmail.setOnFocusChangeListener(focusChangeListener);

        txtPassword = (EditText) fragment.findViewById(R.id.txtPassword);
        txtPassword.setOnFocusChangeListener(focusChangeListener);

        txtPasswordConfirmation = (EditText) fragment.findViewById(R.id.txtPasswordConfirmation);
        txtPasswordConfirmation.setOnFocusChangeListener(focusChangeListener);

        Button btRegister = (Button) fragment.findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Verify if data provided by user is OK to register.
                if (dataOK()) {

                    // Show progress dialog while processing.
                    ProgressDialog.show(getActivity(), getString(R.string.progress_registering));

                    // Build sign up request.
                    SignUpRequest request = new SignUpRequest(
                            getActivity(),
                            new SignUpRestRequest(
                                    txtName.getText().toString(),
                                    txtLastName.getText().toString(),
                                    txtEmail.getText().toString(),
                                    txtPassword.getText().toString(),
                                    DeviceUtils.getDeviceInfo(),
                                    mLocator.getCity(),
                                    mLocator.getCountry(),
                                    "",
                                    "",
                                    ""
                            )
                    );

                    // Execute sign up request
                    _contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED,

                            // Listener block
                            new RequestListener<SignUpRestResult>() {

                                @Override
                                public void onRequestFailure(SpiceException spiceException) {

                                    // Request failed

                                    Log.e(TAG, null, spiceException);
                                    ProgressDialog.dismiss(getActivity());
                                    AlertDialogUtils.showError(getActivity(), spiceException.getLocalizedMessage());
                                }

                                @Override
                                public void onRequestSuccess(SignUpRestResult result) {

                                    // Server returned a response HTTP code 200

                                    ProgressDialog.dismiss(getActivity());
                                    if (result == null) {
                                        // It was not supposed to be null. Something is really wrong here.
                                        AlertDialogUtils.showError(getActivity(), getString(R.string.alert_signup_null_response));
                                        return;
                                    }

                                    // Check if account was created successfully
                                    if (result.isSuccess()) {

                                        Toast.makeText(getActivity(), getString(R.string.toast_registered), Toast.LENGTH_LONG).show();

                                        // Save user account
                                        UserAccountPreferences.getInstance(getActivity().getApplicationContext()).saveUserAccount(
                                                new UserAccount(
                                                        txtEmail.getText().toString(),
                                                        txtPassword.getText().toString()
                                                )
                                        );
                                        // Go back and sign in
                                        getActivity().onBackPressed();

                                    } else {
                                        // If it failed, show message from server.
                                        AlertDialogUtils.showError(getActivity(), result.getMessage());
                                    }
                                }
                            });
                }
            }
        });

        return fragment;
    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        }
    };

    /**
     * Verify if data is OK for registration.
     *
     * If NOK, it shows a message to user asking to provide necessary data.
     *
     * @return Return true if data is OK, otherwise return false.
     */
    private boolean dataOK() {
        if (!Utils.isTextValidNotEmpty(txtName)) {
            AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_name));
        } else if (!Utils.isTextEmailValidNotEmpty(txtEmail)) {
            AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_your_email));
        } else if (!Utils.isTextPasswordValidNotEmpty(txtPassword, 8)) {
            AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_provide_a_password));
        } else if ((!Utils.areTextsValidNotEmptyAndEqual(txtPassword, txtPasswordConfirmation))) {
            AlertDialogUtils.showAlert(getActivity(), getString(R.string.alert_password_are_not_the_same));
        } else {
            return true;
        }
        return false;
    }

}
