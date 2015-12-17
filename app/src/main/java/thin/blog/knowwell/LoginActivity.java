package thin.blog.knowwell;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import network.CustomRequest;
import network.VolleySingleton;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    ActionProcessButton login;
    @Bind(R.id.google_sign_in)
    SignInButton googleSignIn;
    String userInputEmail, userInputPassword;
    String serverMessage;
    int serverSuccess;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int userDataUserId;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidPassword(String password) {
        if (password.contentEquals("")) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.login)
    public void login() {
        lockView(login);
        userInputEmail = email.getText().toString();
        userInputPassword = password.getText().toString();
        if (isValidEmailAddress(userInputEmail) && isValidPassword(userInputPassword)) {
            final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            Map<String, String> formData = new HashMap<>();
            formData.put("email", userInputEmail);
            formData.put("password", userInputPassword);
            final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.LOGIN, formData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonParser(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(login, "Network Error", Snackbar.LENGTH_SHORT).show();
                    login.setProgress(-1);
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            login.setProgress(0);
                            releaseView(login);
                        }
                    }.start();
                }
            });
            requestQueue.add(request);
            login.setProgress(1);
        } else {
            Snackbar.make(login, "Enter Valid Details", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("success");
            serverMessage = response.getString("message");
            userDataUserId = Integer.parseInt(response.getString("user_id"));
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (serverSuccess == 1) {
            login.setProgress(1);
            editor.putInt(Constants.USER_DATA_USER_ID, userDataUserId);
            editor.putString(Constants.USER_DATA_EMAIL, userInputEmail);
            editor.putString(Constants.USER_DATA_PASSWORD, userInputPassword);
            editor.putBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, true);
            editor.apply();
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    startActivity(new Intent(LoginActivity.this, SurveyList.class));
                    finish();
                }
            }.start();

        } else {
            login.setProgress(-1);
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    releaseView(login);
                }
            }.start();
            Snackbar.make(login, "Check Your Credentials", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //if (sharedPreferences.getBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, false)) {
        if (false) {
            startActivity(new Intent(LoginActivity.this, SurveyList.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        //binding butterknife
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        email.setText("jm.prathab@gmail.com");
        password.setText("password");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void signInWithGoogle() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = person.getDisplayName();
                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String personGoogleProfile = person.getUrl();
                String personProfilePhotoUrl = person.getImage().getUrl();
                String personCoverPhotoUrl = person.getCover().getCoverPhoto().getUrl();
                personProfilePhotoUrl = personProfilePhotoUrl.substring(0, personProfilePhotoUrl.length() - 2) + PROFILE_PIC_SIZE;
                editor.putString(Constants.USER_DATA_NAME, personName);
                editor.putString(Constants.USER_DATA_EMAIL, personEmail);
                editor.putString(Constants.USER_DATA_GOOGLE_EMAIL, personEmail);
                editor.putString(Constants.USER_DATA_GOOGLE_PROFILE, personGoogleProfile);
                editor.putString(Constants.USER_DATA_GOOGLE_PROFILE_PHOTO, personProfilePhotoUrl);
                editor.putString(Constants.USER_DATA_GOOGLE_COVER_PHOTO, personCoverPhotoUrl);
                editor.putBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, true);
                editor.putBoolean(Constants.SUCCESSFUL_REGISTRATION_HISTORY, true);
                editor.apply();
                startActivity(new Intent(LoginActivity.this, SurveyList.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Profile Cannot be fetched", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
    }

    @OnClick(R.id.google_sign_in)
    public void googleSignIn() {
        signInWithGoogle();
    }

    @OnClick(R.id.forgot_password)
    public void forgotPasssword() {
        SpannableString message = new SpannableString("To reset Password visit \nwww.knowwell/reset.php");
        Linkify.addLinks(message, Linkify.ALL);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogLight);
        builder.setCancelable(false);
        builder.setTitle("Password Reset");
        builder.setMessage(message);
        builder.setPositiveButton("Okay", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(R.id.create_account)
    public void createAccount() {
        startActivity(new Intent(LoginActivity.this, SignUp.class));
        finish();
    }
}
