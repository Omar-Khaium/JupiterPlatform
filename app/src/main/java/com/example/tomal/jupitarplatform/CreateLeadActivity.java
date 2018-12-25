package com.example.tomal.jupitarplatform;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;

import static com.example.tomal.jupitarplatform.MainActivity.SITE_ID;


public class CreateLeadActivity extends AppCompatActivity {

    private Button backButton, createButton;
    EditText commentEditText;
    String Siteid;
    TextView firstNameEdit, lastNameEdit, streetEdit, cityEdit, stateEdit, zipEdit, phoneEdit, emaillEdit, commentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lead_activity);

        Intent intent = getIntent();
        intent.getStringExtra("float");

        backButton = findViewById(R.id.back_button);
        createButton = findViewById(R.id.createLeadButton);
        commentEditText = findViewById(R.id.commentEdit);
        firstNameEdit = findViewById(R.id.firstNameEdit);
        lastNameEdit = findViewById(R.id.lastNameEdit);
        streetEdit = findViewById(R.id.streetEdit);
        cityEdit = findViewById(R.id.cityEdit);
        stateEdit = findViewById(R.id.stateEdit);
        zipEdit = findViewById(R.id.zipEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        emaillEdit = findViewById(R.id.emaillEdit);
        commentEdit = findViewById(R.id.commentEdit);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

        createLeadButton();


    }

    @Override
    protected void onStart() {
        super.onStart();
//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }

    private void createLeadButton() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (!firstNameEdit.getText().toString().isEmpty() && !lastNameEdit.getText().toString().isEmpty() &&
                        !streetEdit.getText().toString().isEmpty() && !cityEdit.getText().toString().isEmpty()
                        && !stateEdit.getText().toString().isEmpty() && !zipEdit.getText().toString().isEmpty()
                        && !phoneEdit.getText().toString().isEmpty() && !emaillEdit.getText().toString().isEmpty()
                        && !commentEdit.getText().toString().isEmpty()) {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, "siteid=" + SITE_ID
                            + "&company=" + getIntent().getStringExtra("companyId") +
                            "&fname=" + firstNameEdit.getText().toString() +
                            "&lname=" + lastNameEdit.getText().toString() +
                            "&street=" + streetEdit.getText().toString() +
                            "&city=" + cityEdit.getText().toString() +
                            "&state=" + stateEdit.getText().toString() +
                            "&zip=" + zipEdit.getText().toString() +
                            "&phone=" + phoneEdit.getText().toString() +
                            "&email=" + emaillEdit.getText().toString() +
                            "&comment=" + commentEdit.getText().toString() +
                            "&undefined=");
                    Request request = new Request.Builder()
                            .url("https://jupiter.centralstationmarketing.com/api/add_new_lead.php?comment=" + commentEdit.getText().toString())
                            .post(body)
                            .addHeader("Cookie", COOKIE_FOR_API)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("cache-control", "no-cache")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(getApplicationContext(), "Exception occured : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            final String myreponce = response.body().string();
                            CreateLeadActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        /*if (response.code() == 200) {
                                            Toast.makeText(CreateLeadActivity.this, "Lead added successfully", Toast.LENGTH_SHORT).show();
                                            // startActivity(new Intent(getApplicationContext(), CorrespondenceActivity.class).putExtra("Company ID", CompanyID));
                                        } else {
                                            Toast.makeText(CreateLeadActivity.this, "Error Occurred: ", Toast.LENGTH_SHORT).show();
                                        }*/
                                        Toast.makeText(CreateLeadActivity.this, myreponce, Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    });
                } else {
                    if (firstNameEdit.getText().toString().isEmpty()) {
                        firstNameEdit.setError("*Please enter firstname");
                    } else if (lastNameEdit.getText().toString().isEmpty()) {
                        lastNameEdit.setError("*Please enter lastname");
                    } else if (streetEdit.getText().toString().isEmpty()) {
                        streetEdit.setError("*Please enter street");
                    } else if (cityEdit.getText().toString().isEmpty()) {
                        cityEdit.setError("*Please enter city");
                    } else if (stateEdit.getText().toString().isEmpty()) {
                        stateEdit.setError("*Please enter state");
                    } else if (zipEdit.getText().toString().isEmpty()) {
                        zipEdit.setError("*Please enter zip");
                    } else if (phoneEdit.getText().toString().isEmpty()) {
                        phoneEdit.setError("*Please enter phone");
                    } else if (emaillEdit.getText().toString().isEmpty()) {
                        emaillEdit.setError("*Please enter email");
                    } else if (commentEdit.getText().toString().isEmpty()) {
                        commentEdit.setError("*Please enter comment");
                    }

                }
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
