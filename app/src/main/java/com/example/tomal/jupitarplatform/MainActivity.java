package com.example.tomal.jupitarplatform;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.CentralStationLeadActivity.trackLoginFromDashboard;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity {

    LinearLayout xLayout;
    ProgressBar xProgress;
    Button loginbtn;
    EditText email;
    EditText Password;
    SwitchButton xRemember;
    TextView xPrivacy;
    public static String SITE_ID = "";
    public static String COMPANY_NAME = "";
    public static String LEAD_ID = "";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int PERMISSION_ALL = 1;
    static boolean fromDateRange = false;
    public static String COOKIE_FOR_API = "";
    public static final String DEVICE_TOKEN = "APA91bFDhsk59EK7er9r5XM-6s1hWjMK0WKIp2hZchQsnzVlPQwdWoqQe4Thxm2p_dhzeI-2omZMepaSffJgAgYhaZpOQQzLi8JTwOOxUNuu3QTrsdV3ASiHhfSrcLtr0gJ1xL7LwMVPP2636W2HY1um3963A0-0-g";


    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PRIVILEGED
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginbtn = (findViewById(R.id.loginButtton));



        email = (findViewById(R.id.emailEdit));
        Password = (findViewById(R.id.passwordEdit));
        xRemember = (findViewById(R.id.switch_button));
        xLayout = (findViewById(R.id.login_layout));
        xProgress = (findViewById(R.id.login_progressbar));
        xPrivacy = (findViewById(R.id.privacyText));

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

       xLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               hideKeyboard(v);
           }
       });
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (sharedPref.getBoolean("isRemembered", false)) {
            email.setText(sharedPref.getString("Email", ""));
            Password.setText(sharedPref.getString("Password", ""));
            xRemember.setChecked(true);
            if (!trackLoginFromDashboard) {
                Login();
            }
        }


        loginButton();

        xPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PrivacyStatementActivity.class));
            }
        });

    }



    private void loginButton() {
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (xRemember.isChecked()) {
                    editor.putString("Email", email.getText().toString());
                    editor.putString("Password", Password.getText().toString());
                    editor.putBoolean("isRemembered", true);
                    editor.apply();
                    editor.commit();
                } else {
                    editor.putString("Email", "");
                    editor.putString("Password", "");
                    editor.putBoolean("isRemembered", false);
                    editor.apply();
                    editor.commit();
                }
                email.setEnabled(false);
                Password.setEnabled(false);
                xRemember.setEnabled(false);
                loginbtn.setEnabled(false);
                xLayout.setVisibility(View.GONE);
                xProgress.setVisibility(View.VISIBLE);
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "Authorization=&userid=" + email.getText().toString() + "&password=" + Password.getText().toString() + "&Content-Type=application%2Fx-www-form-urlencode&device_token=" + DEVICE_TOKEN +
                        "&undefined=");
                Request request = new Request.Builder()
                        .url("https://jupiter.centralstationmarketing.com/api/ios/Login.php")
                        .post(body)
                        .addHeader("cache-control", "no-cache")
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

                    @Override
                    public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                xLayout.setVisibility(View.VISIBLE);
                                xProgress.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        final String myResponse = response.body().string();
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject(myResponse);
                                    COOKIE_FOR_API =  response.header("Set-Cookie").substring(0, response.header("Set-Cookie").indexOf(";"));

                                    if (json.getBoolean("Login")) {
                                        startActivity(new Intent(MainActivity.this, CentralStationLeadActivity.class));


                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        email.setEnabled(true);
                                        Password.setEnabled(true);
                                        xRemember.setEnabled(true);
                                        loginbtn.setEnabled(true);
                                        xLayout.setVisibility(View.VISIBLE);
                                        xProgress.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    email.setEnabled(true);
                                    Password.setEnabled(true);
                                    xRemember.setEnabled(true);
                                    loginbtn.setEnabled(true);
                                    xLayout.setVisibility(View.VISIBLE);
                                    xProgress.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @SuppressLint("MissingPermission")
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    private void Login() {
        xLayout.setVisibility(View.GONE);
        xProgress.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "Authorization=&userid=" + email.getText().toString() + "&password=" + Password.getText().toString() + "&Content-Type=application%2Fx-www-form-urlencode&device_token=" + DEVICE_TOKEN +
                "&undefined=");
        Request request = new Request.Builder()

                .url("https://jupiter.centralstationmarketing.com/api/ios/Login.php")
                .post(body)
                .addHeader("cache-control", "no-cache")
                .build();
            client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, final IOException e) {
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            COOKIE_FOR_API =  response.header("Set-Cookie").substring(0, response.header("Set-Cookie").indexOf(";"));

                            if (json.getBoolean("Login")) {
                                startActivity(new Intent(MainActivity.this, CentralStationLeadActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                xLayout.setVisibility(View.VISIBLE);
                                xProgress.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            xLayout.setVisibility(View.VISIBLE);
                            xProgress.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
