package com.ap.pacyourcultureman;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ap.pacyourcultureman.Helpers.ApiHelper;
import com.ap.pacyourcultureman.Helpers.JSONDeserializer;
import com.ap.pacyourcultureman.Helpers.JSONSerializer;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Login extends Activity {
    private String email, password, targetURL, reply;
    Button btn_login, btn_register, btn_dev;
    EditText edit_email, edit_password;
    TextView errorChecker;
    CheckBox chb_rememberme, chb_loginauto;
    Boolean rememberMe, loginauto;
    HttpURLConnection conn;
    URL url;
    private Handler mHandler;
    RequestQueue queue;  // this = context
    static List<Assignment> assignments;
    List<String> steps = new ArrayList<>();
    ApiHelper apiHelper, apiHelper2, apiHelper3;
    Boolean running;
    Handler handler;
    int userId;
    String jwt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        apiHelper = new ApiHelper();
        apiHelper2 = new ApiHelper();
        apiHelper3 = new ApiHelper();
        targetURL = "https://aspcoreapipycm.azurewebsites.net/Users/authenticate";
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_dev = findViewById(R.id.btn_dev);
        edit_email = findViewById(R.id.edit_mail);
        edit_password = findViewById(R.id.edit_pass);
        errorChecker = findViewById(R.id.txt_errorchecker);
        chb_rememberme = findViewById(R.id.login_chb_rememember);
        chb_loginauto = findViewById(R.id.login_chb_autologin);
        Intent intent = getIntent();
        queue = Volley.newRequestQueue(this);
        Load();
        String intentuser = intent.getStringExtra("username");
        String intentpassword = intent.getStringExtra("pass");
        if (intentuser != null && intentpassword != null) {
            edit_password.setText(intentpassword);
            edit_email.setText(intentuser);
            chb_loginauto.setChecked(false);
            chb_rememberme.setChecked(false);
        }
        if (chb_loginauto.isChecked()) {
            email = edit_email.getText().toString();
            password = edit_password.getText().toString();
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             errorSetter("Logging in");
                                             String user = edit_email.getText().toString();
                                             String pass = edit_password.getText().toString();
                                             JSONSerializer jsonSerializer = new JSONSerializer();
                                             JSONObject jsonObject = jsonSerializer.jsonPostLogin(user, pass);
                                             apiHelper.sendPost("https://aspcoreapipycm.azurewebsites.net/Users/authenticate", jsonObject);
                                             while (apiHelper.run) {
                                             }
                                             if (apiHelper.getResponse() == "Success") {
                                                 errorSetter("Logging in");
                                                 apiHelper.setPlayer(apiHelper.getReply());
                                                 Thread thread = new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         try {
                                                             apiHelper.getArray("https://aspcoreapipycm.azurewebsites.net/Sights");
                                                             while (apiHelper.run) {
                                                             }
                                                             JSONDeserializer jsonDeserializer = new JSONDeserializer();
                                                             ApiHelper.assignments = jsonDeserializer.getAssignnments(apiHelper.getJsonArray());
                                                             startGame();
                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 });
                                                 thread.start();
                                                 Thread thread2 = new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         try {
                                                             apiHelper2.getArray("https://aspcoreapipycm.azurewebsites.net/Dot");
                                                             errorSetter("Fetching data");
                                                             while (apiHelper2.run) {
                                                             }
                                                             JSONDeserializer jsonDeserializer = new JSONDeserializer();
                                                             ApiHelper.dots = jsonDeserializer.getDots(apiHelper2.getJsonArray());
                                                             if (chb_rememberme.isChecked()) {
                                                                 Save();
                                                             }
                                                             startGame();

                                                             Log.d("Nailed", "it");

                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 });
                                                 thread2.start();
                                                 Thread thread3 = new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         try {
                                                             apiHelper3.getDirectionsApi("https://maps.googleapis.com/maps/api/directions/json?origin=51.229963%2C%204.420749&destination=51.226304%2C%204.426475&mode=walking&key=AIzaSyB4HgIDhaV6sv3ddo_Xol9r4fDLj7RpOaU&fbclid=IwAR3KBusU_zvFk_F4-6K9bhHoT6B2thi_nceJHXLXdMdtCzeuB0k-1m1tMzE");
                                                             while (apiHelper3.run) {
                                                             }
                                                             JSONDeserializer jsonDeserializer = new JSONDeserializer();
                                                             steps = jsonDeserializer.getSteps(apiHelper3.getJsonObject());
                                                             startGame();

                                                         }
                                                         catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 });
                                                 thread3.start();
                                                 userId = apiHelper.getUserId();
                                                 jwt = apiHelper.getJwt();
                                             }
                                             else {
                                                 errorSetter(apiHelper.getResponse());
                                             }
                                         }

                                         });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Register.class);
                startActivity(intent);
            }
        });
        btn_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), GameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void errorSetter(final String errormsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorChecker.setVisibility(View.VISIBLE);
                errorChecker.setText(errormsg);
            }
        });
    }

    private void Save() {
        SharedPreferences sp = getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        email = edit_email.getText().toString();
        password = edit_password.getText().toString();
        if (chb_rememberme.isChecked()) {
            rememberMe = true;
            editor.putString("email", email);
            editor.putString("pass", password);
        } else {
            rememberMe = false;
            loginauto = false;
            editor.putString("email", "");
            editor.putString("pass", "");
        }
        if (chb_loginauto.isChecked()) {
            loginauto = true;
        } else {
            loginauto = false;
        }
        editor.putBoolean("chbremember", rememberMe);
        editor.putBoolean("chbloginauto", loginauto);
        editor.apply();
    }

    public void Load() {
        SharedPreferences sp = getSharedPreferences("DATA", MODE_PRIVATE);
        String txtuser = sp.getString("email", null);
        String txtpass = sp.getString("pass", null);
        Boolean remembered = sp.getBoolean("chbremember", false);
        Boolean loginauto = sp.getBoolean("chbloginauto", false);
        edit_email.setText(txtuser);
        edit_password.setText(txtpass);
        chb_rememberme.setChecked(remembered);
        chb_loginauto.setChecked(loginauto);
        if (loginauto) {
            JSONObject loginParams = new JSONObject();
            try {
                loginParams.put("password", edit_password.getText().toString());
                loginParams.put("email", edit_email.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void startGame() {
        if(!apiHelper.run && !apiHelper2.run && !apiHelper3.run) {
                  Intent intent = new Intent(getBaseContext(), GameActivity.class);
                                                         intent.putExtra("userid", userId);
                                                         intent.putExtra("jwt", jwt);
                                                         startActivity(intent);
        }

    }

}
