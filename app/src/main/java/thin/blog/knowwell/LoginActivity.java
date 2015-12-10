package thin.blog.knowwell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import network.CustomRequest;
import network.VolleySingleton;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    ActionProcessButton login;
    @Bind(R.id.forgot_password)
    TextView forgotPassword;
    @Bind(R.id.create_account)
    TextView createAccount;

    String userInputEmail, userInputPassword;
    String serverMessage;
    int serverSuccess;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int userDataUserId;

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
    public void login(View v) {
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
        if (sharedPreferences.getBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, false)) {
            // if (true) {
            startActivity(new Intent(LoginActivity.this, SurveyList.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        //binding butterknife
        ButterKnife.bind(this);
        email.setText("test@test.com");
        password.setText("password");
    }

    @OnClick(R.id.forgot_password)
    public void forgotPasssword(View v) {
        //TODO
    }

    @OnClick(R.id.create_account)
    public void createAccount(View v) {
        //TODO
    }

    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
    }
}
