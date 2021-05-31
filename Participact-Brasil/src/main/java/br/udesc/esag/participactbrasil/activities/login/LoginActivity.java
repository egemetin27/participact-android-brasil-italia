package br.udesc.esag.participactbrasil.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import br.com.bergmannsoft.gps.Locator;
import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.Connectivity;
import br.com.bergmannsoft.util.DeviceUtils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.domain.rest.ForgotRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestRequest;
import br.udesc.esag.participactbrasil.domain.rest.SignUpRestResult;
import br.udesc.esag.participactbrasil.network.request.ForgotRequest;
import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequest;
import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequestListener;
import br.udesc.esag.participactbrasil.network.request.LoginRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.network.request.SignUpRequest;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

import static br.udesc.esag.participactbrasil.ParticipActConfiguration.PAGES_URL;

public class LoginActivity extends AppCompatActivity {

    public enum SocialType {

        FACEBOOK("facebook"),
        GOOGLE("google"),
        NORMAL("normal");

        SocialType(String name) {
            this.name = name;
        }

        protected String name;

        public String getName() {
            return this.name;
        }
    }

    private SocialType socialType;
    private String socialName;
    private String socialSurname;
    private String socialEmail;
    private String socialId;
    private String socialPhoto;
    private String socialAgeRange = "";

    // Use to find user location and provide data like city and country.
    private Locator mLocator;

    private static final String KEY_LAST_REQUEST_CACHE_KEY = "loginlastRequestCacheKey";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 5000;


    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    private NetworkStateChecker networkChecker;

    private String _lastRequestCacheKey;

    private String _email;
    private String _password;

    private EditText _emailView;
    private EditText _passwordView;
    private UserRegistrationFragment userRegistrationFragment;
    private LinearLayout formsLayout;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLocator = new Locator(this);

        networkChecker = new DefaultNetworkStateChecker();

        formsLayout = (LinearLayout) findViewById(R.id.forms);

        // Set up the login form.
        _email = "";
        _emailView = (EditText) findViewById(R.id.email);
        _emailView.setText(_email);

        _passwordView = (EditText) findViewById(R.id.password);
        _passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.btRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserRegistrarionFragment();
            }
        });

        // region Facebook SignIn setup
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.d(TAG, object.toString());

                            socialType = SocialType.FACEBOOK;
                            if (!object.isNull("first_name") && !object.isNull("last_name")) {
                                socialName = object.getString("first_name");
                                socialSurname = object.getString("last_name");
                            } else {
                                String[] name = object.getString("name").split(" ");
                                if (name != null && name.length > 1) {
                                    socialName = name[0];
                                    socialSurname = "";
                                    for (int i = 1; i < name.length; i++) {
                                        socialSurname += name[i] + " ";
                                    }
                                    socialSurname = socialSurname.trim();
                                } else {
                                    socialName = object.getString("name");
                                    socialSurname = "";
                                }
                            }
                            socialEmail = object.getString("email");
                            socialId = object.getString("id");
                            socialPhoto = "";
                            socialAgeRange = "";

                            if (!object.isNull("age_range")) {
                                JSONObject age = object.getJSONObject("age_range");
                                if (!age.isNull("min")) {
                                    socialAgeRange = "" + age.getInt("min");
                                    if (!age.isNull("max")) {
                                        socialAgeRange = age.getInt("min") + "-" + age.getInt("max");
                                    }
                                }

                            }

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
                                                        socialPhoto = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                        Log.d(TAG, socialPhoto);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    performRequest(socialEmail, socialId/*, SocialType.FACEBOOK, facebookName*/);
                                                }
                                            }
                                        }
                                    }).executeAsync();

                        } catch (Exception e) {
                            Log.e(TAG, null, e);
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,age_range,picture.type(large),email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Facebook login - onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Facebook login - onError", exception);
            }
        });
        // endregion Facebook SignIn

        // region Google SignIn Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, connectionResult.getErrorMessage());
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        // endregion Google SignIn

        findViewById(R.id.btForgotPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle Facebook SignIn.
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    /**
     * Handle Google SignIn results
     *
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        socialAgeRange = "";
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            socialType = SocialType.GOOGLE;
            socialName = acct.getGivenName();
            socialSurname = acct.getFamilyName();
            socialEmail = acct.getEmail();
            socialId = acct.getId();

            socialPhoto = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";
            performRequest(acct.getEmail(), acct.getId()/*, SocialType.GOOGLE, acct.getDisplayName()*/);
        } else {
            AlertDialogUtils.showError(this, getString(R.string.alert_google_signin_error));
        }
    }

    private void loadUserRegistrarionFragment() {
        if (userRegistrationFragment == null) {
            userRegistrationFragment = UserRegistrationFragment.newInstance();
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.login_form, userRegistrationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (userRegistrationFragment != null && userRegistrationFragment.isVisible()) {
            if (UserAccountPreferences.getInstance(this).isUserAccountValid()) {
                formsLayout.setVisibility(View.GONE);
                registerGCMBackground();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!UserAccountPreferences.getInstance(this).isUserAccountValid()) {
            formsLayout.setVisibility(View.VISIBLE);
            socialSignOut();
        }
        if (!_contentManager.isStarted()) {
            _contentManager.start(this);
        }
        if (!mLocator.isStarted()) {
            mLocator.start();
        }
    }

    private void socialSignOut() {
        LoginManager.getInstance().logOut();
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
    protected void onStop() {
        if (_contentManager.isStarted()) {
            _contentManager.shouldStop();
        }
        if (mLocator.isStarted()) {
            mLocator.stop();
        }
        super.onStop();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        _emailView.setError(null);
        _passwordView.setError(null);

        // Store values at the time of the login attempt.
        _email = _emailView.getText().toString();
        _password = _passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(_password)) {
            _passwordView.setError(getString(R.string.error_field_required));
            focusView = _passwordView;
            cancel = true;
        } else if (_password.length() < 4) {
            _passwordView.setError(getString(R.string.error_invalid_password));
            focusView = _passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(_email)) {
            _emailView.setError(getString(R.string.error_field_required));
            focusView = _emailView;
            cancel = true;
        } else if (!_email.contains("@")) {
            _emailView.setError(getString(R.string.error_invalid_email));
            focusView = _emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            socialType = SocialType.NORMAL;

            // login request
            performRequest(_email, _password/*, SocialType.NORMAL, null*/);

        }
    }

    private void performRequest(final String user, final String password/*, SocialType type, String socialName*/) {
        // DashboardActivity.this.setSupportProgressBarIndeterminateVisibility( true );
        ProgressDialog.show(this, getString(R.string.alert_signing_in));
        _email = user;
        _password = password;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean connected = Connectivity.isConnected(LoginActivity.this);
                boolean reachable = Connectivity.isReachable(LoginActivity.this, PAGES_URL);
                if (connected && reachable) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoginRequest request = new LoginRequest(user, password/*, type.getName(), socialName, mLocator.getCity(), mLocator.getCountry()*/);
                            _lastRequestCacheKey = request.createCacheKey();
                            _contentManager.execute(request, _lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED,
                                    new LoginRequestListener());
                        }
                    });
                } else {
                    ProgressDialog.dismiss(LoginActivity.this);
                    AlertDialogUtils.showMessage(LoginActivity.this, getString(R.string.network_error_message) + (!reachable ? " [506]" : " [505]"), getString(R.string.network_error));
                }
            }
        }).start();

    }

    private void socialSignUp(String name, String surname, final String email, final String password, String photo, String social) {
        SignUpRequest request = new SignUpRequest(
                this,
                new SignUpRestRequest(
                        name,
                        surname,
                        email,
                        password,
                        DeviceUtils.getDeviceInfo(),
                        mLocator.getCity(),
                        mLocator.getCountry(),
                        photo,
                        social,
                        socialAgeRange
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
                        ProgressDialog.dismiss(LoginActivity.this);
                        AlertDialogUtils.showError(LoginActivity.this, spiceException.getLocalizedMessage());
                    }

                    @Override
                    public void onRequestSuccess(SignUpRestResult result) {

                        // Server returned a response HTTP code 200

                        ProgressDialog.dismiss(LoginActivity.this);
                        if (result == null) {
                            // It was not supposed to be null. Something is really wrong here.
                            AlertDialogUtils.showError(LoginActivity.this, getString(R.string.alert_signup_null_response));
                            return;
                        }

                        // Check if account was created successfully
                        if (result.isSuccess()) {

                            // Save user account
                            UserAccountPreferences.getInstance(getApplicationContext()).saveUserAccount(
                                    new UserAccount(
                                            email,
                                            password
                                    )
                            );
                            // Go back and sign in
                            performRequest(email, password);

                        } else {
                            // If it failed, show message from server.
                            AlertDialogUtils.showError(LoginActivity.this, result.getMessage());
                            socialSignOut();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(_lastRequestCacheKey)) {
            outState.putString(KEY_LAST_REQUEST_CACHE_KEY, _lastRequestCacheKey);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_LAST_REQUEST_CACHE_KEY)) {
            _lastRequestCacheKey = savedInstanceState.getString(KEY_LAST_REQUEST_CACHE_KEY);
            _contentManager.addListenerIfPending(Boolean.class, _lastRequestCacheKey, new LoginRequestListener());
            _contentManager.getFromCache(Boolean.class, _lastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                    new LoginRequestListener());
        }
    }

    private void registerGCMBackground() {

        GCMRegisterRequest request = new GCMRegisterRequest(this);
        _lastRequestCacheKey = request.createCacheKey();
        if (!_contentManager.isStarted()) {
            _contentManager.start(this);
        }
        _contentManager.execute(request, _lastRequestCacheKey, DurationInMillis.ONE_HOUR, new GCMRegisterRequestListener(getApplicationContext()));
    }


    private class LoginRequestListener implements RequestListener<Boolean> {
        @Override
        public void onRequestFailure(SpiceException e) {

            String errorMessage = getString(R.string.error_login_dialog_generic_error);

            if (e.getCause() instanceof HttpClientErrorException) {
                if (((HttpClientErrorException) e.getCause()).getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                    if (socialType == SocialType.NORMAL) {
                        errorMessage = getString(R.string.error_login_dialog_bad_credential);
                    } else {
                        socialSignUp(socialName, socialSurname, socialEmail, socialId, socialPhoto, socialType.getName());
                        return;
                    }
                }
            }

            ProgressDialog.dismiss(LoginActivity.this);

            AlertDialogUtils.showMessage(LoginActivity.this, errorMessage, getString(R.string.error_login_dialog_title));
            socialSignOut();
        }

        @Override
        public void onRequestSuccess(Boolean result) {
            ProgressDialog.dismiss(LoginActivity.this);
            if (result == null) {
                return;
            }
            Toast.makeText(LoginActivity.this, getString(R.string.success_login), Toast.LENGTH_LONG).show();
            if (result) {
                UserAccount account = new UserAccount();
                account.setUsername(_email);
                account.setPassword(_password);
                UserAccountPreferences.getInstance(getApplicationContext()).saveUserAccount(account);
                // register gcm
                registerGCMBackground();
                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
            }
        }
    }


}