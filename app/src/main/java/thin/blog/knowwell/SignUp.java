package thin.blog.knowwell;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.ProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import network.CustomRequest;
import network.VolleySingleton;

public class SignUp extends AppCompatActivity {
    @Bind(R.id.app_bar)
    Toolbar toolbar;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.name_wrapper)
    TextInputLayout nameWrapper;
    @Bind(R.id.create_account)
    ProcessButton signUp;
    String userInputName, userInputEmail, userInputPassword;
    int serverSuccess;
    String serverMessage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

    public static boolean isValidName(String name) {
        return !name.contentEquals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //for testing
        name.setText("Prathab");
        email.setText("jm.prathab@gmail.com");
        password.setText("password");
    }

    @OnClick(R.id.create_account)
    public void createAccount() {
        lockView(signUp);
        userInputName = name.getText().toString();
        userInputEmail = email.getText().toString();
        userInputPassword = password.getText().toString();
        serverSuccess = 0;
        serverMessage = "Cannot contact server\nCheck your Internet Connection and Try again";
        if (isValidName(userInputName) && isValidEmailAddress(userInputEmail) && isValidPassword(userInputPassword)) {
            signUp.setProgress(1);
            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            Map<String, String> formData = new HashMap<>();
            formData.put("name", userInputName);
            formData.put("email", userInputEmail);
            formData.put("password", userInputPassword);

            final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.SIGNUP, formData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonParser(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(signUp, "Network Error", Snackbar.LENGTH_SHORT).show();
                    signUp.setProgress(-1);
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            signUp.setProgress(0);
                            releaseView(signUp);
                        }
                    }.start();
                }
            });
            request.setTag(Constants.SIGNUP);
            requestQueue.add(request);

        } else {
            Snackbar.make(signUp, "Enter Valid Details", Snackbar.LENGTH_SHORT).show();
            signUp.setProgress(-1);
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    signUp.setProgress(0);
                    releaseView(signUp);
                }
            }.start();
        }
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("success");
            serverMessage = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this,R.style.AlertDialogDark);
        builder.setCancelable(false);
        if (serverSuccess == 1) {
            editor.putString(Constants.USER_DATA_EMAIL, userInputEmail);
            editor.putString(Constants.USER_DATA_PASSWORD, userInputPassword);
            editor.putString(Constants.USER_DATA_NAME, userInputName);
            editor.putBoolean(Constants.SUCCESSFUL_REGISTRATION_HISTORY, true);
            editor.apply();
            signUp.setProgress(100);
            builder.setTitle("Successfully Registered");
            builder.setMessage(serverMessage);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
        } else {
            signUp.setProgress(-1);
            builder.setTitle("Cannot Register");
            builder.setMessage(serverMessage);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    signUp.setProgress(0);
                    releaseView(signUp);
                    dialog.cancel();
                }
            });

        }
        AlertDialog alertDialog;
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
    }

}
