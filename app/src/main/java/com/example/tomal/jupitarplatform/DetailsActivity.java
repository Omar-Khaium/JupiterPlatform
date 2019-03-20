package com.example.tomal.jupitarplatform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COMPANY_NAME;
import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;


public class DetailsActivity extends AppCompatActivity {

    private ProgressDialog xDialog;
    private ImageView cameraImageView;
    private ImageView videoCameraImageView;
    private ImageView rattingImageView;
    private TextView callTypeText, dateText, stateText, callNumber, customerViewText, toolbarSubText, xViewNotes, xViewMedia;
    int flag = 0;
    TextView commentText;
    Button submitBtn;
    EditText noteEditText;
    DashboardProfileModel dashboardProfileModel;

    LinearLayout xLayout;
    ProgressBar xProgress;

    LeadCentralModel model;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_);

        Gson gson = new Gson();
        xDialog = new ProgressDialog(this);
        xDialog.setCancelable(false);

        model = gson.fromJson(getIntent().getStringExtra("myjson"), LeadCentralModel.class);
        //findings
        cameraImageView = (findViewById(R.id.cameraImageView));
        videoCameraImageView = (findViewById(R.id.videoCameraView));
        rattingImageView = (findViewById(R.id.rattingImageView));
        customerViewText = (findViewById(R.id.customerViewText));
        callTypeText = (findViewById(R.id.callTypeText));
        callNumber = (findViewById(R.id.callNumber));
        toolbarSubText = (findViewById(R.id.toolbarSubText));
        commentText = findViewById(R.id.commentText);
        stateText = (findViewById(R.id.stateText));
        dateText = (findViewById(R.id.dateText));
        noteEditText = findViewById(R.id.noteEditText);
        submitBtn = findViewById(R.id.noteSubmitBtn);
        xViewMedia = findViewById(R.id.details_view_media);

        xLayout = findViewById(R.id.details_layout);
        xProgress = findViewById(R.id.details_progress_bar);
        submitButton();

        toolbarSubText.setText(COMPANY_NAME);
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                submitBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                submitBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isReady = noteEditText.getText().toString().length() > 3;
                submitBtn.setEnabled(isReady);
            }
        });
        noteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        if (model.getCompanyName().equals("null") || model.getCompanyName().equals("") || model.getCompanyName() == null) {

            if (model.getFname().equals("null") || model.getFname().equals("") || model.getFname() == null) {
                if (model.getLname().equals("null") || model.getLname().equals("") || model.getLname() == null) {
                    customerViewText.setText("-");
                } else {
                    customerViewText.setText(model.getLname());
                }
            } else {
                if (model.getLname().equals("null") || model.getLname().equals("") || model.getLname() == null) {
                    customerViewText.setText(model.getFname());
                } else {
                    customerViewText.setText(model.getFname() + " ".concat(model.getLname()));
                }
            }
        } else {
            customerViewText.setText(model.getCompanyName());
        }
        if (model.getFormType().equals("null") || model.getFormType().equals("") || model.getFormType() == null) {
            callTypeText.setText("-");
        } else {
            callTypeText.setText(model.getFormType());
        }
        if (model.getPhone().equals("null") || model.getPhone().equals("") || model.getPhone() == null) {
            callNumber.setText("-");
        } else {
            callNumber.setText(model.getPhone());
        }
        if (model.getCity().equals("null") || model.getCity().equals("") || model.getCity() == null) {
            if (model.getState().equals("null") || model.getState().equals("") || model.getState() == null) {
                stateText.setText("-");
            } else {
                stateText.setText(model.getState());
            }
        } else {
            if (model.getState().equals("null") || model.getState().equals("") || model.getState() == null) {
                stateText.setText(model.getCity());
            } else {
                stateText.setText(model.getCity() + ", " + model.getState());
            }
        }
        if (model.getTimestamp().equals("null") || model.getTimestamp().equals("") || model.getTimestamp() == null) {
            dateText.setText("-");
        } else {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat myCreated = new SimpleDateFormat("dd MMM, yyyy hh:mm:ss a");
            try {
                dateText.setText(myCreated.format(fromUser.parse(model.getTimestamp())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (model.getCcComment().equals("null") || model.getCcComment().equals("") || model.getCcComment() == null) {
            commentText.setText("-");
        } else {
            commentText.setText(model.getCcComment());
        }

        dashboardProfileModel = new DashboardProfileModel();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

        videoCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TakeVideoActivity.class));
            }
        });

        //Camera
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TakeImageActivity.class));
            }
        });
        rattingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xDialog.setMessage("Fetching data...");
                xDialog.show();
                getData();
            }
        });
        callNumber.setLinkTextColor(Color.parseColor("#6cb53f"));
        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CallIntent = new Intent(Intent.ACTION_DIAL);
                CallIntent.setData(Uri.parse("tel:" + (model.getPhone())));
                startActivity(CallIntent);
            }
        });

        xViewNotes = findViewById(R.id.details_view_notes);
        xViewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewNotesActivity.class)
                        .putExtra("myjson", getIntent().getStringExtra("fname"))
                        .putExtra("lname", getIntent().getStringExtra("lname"))
                        .putExtra("state", getIntent().getStringExtra("state"))
                        .putExtra("city", getIntent().getStringExtra("city"))
                        .putExtra("Company Name", getIntent().getStringExtra("Company Name"))
                );
            }
        });
        xViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewMediaActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        xDialog.dismiss();
    }

    private void submitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (!noteEditText.getText().toString().isEmpty()) {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, "leadid=" + LEAD_ID +
                            "&api_userid=1562" +
                            "&add_note=" + noteEditText.getText().toString() +
                            "&undefined=");

                    Request request = new Request.Builder()
                            .url("https://jupiter.centralstationmarketing.com/api/ios/AddNote.php?Content-Type=application/x-www-form-urlencoded")
                            .post(body)
                            .addHeader("Cookie", COOKIE_FOR_API)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("add_note", noteEditText.getText().toString())
                            .addHeader("cache-control", "no-cache")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(getApplicationContext(), "Exception occured : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) {

                            DetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200) {
                                            Toast.makeText(DetailsActivity.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                                            // startActivity(new Intent(getApplicationContext(), CorrespondenceActivity.class).putExtra("Company ID", CompanyID));
                                        } else {
                                            Toast.makeText(DetailsActivity.this, "Error Occurred: ", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }


                    });
                } else {
                    if (noteEditText.getText().toString().isEmpty()) {
                        noteEditText.setError("*Please enter valid note at least 3 word.");
                    }
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getData() {
//        xLayout.setVisibility(View.GONE);
//        xProgress.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/getContactDetailUsingLead.php?leadid=" + LEAD_ID)
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("cache-control", "no-cache")
                .build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String myResponse = response.body().string();
                mainHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            JSONArray jsonArray = json.getJSONArray("Leads");
                            JSONObject zipObject = jsonArray.getJSONObject(0);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(model);
                            startActivity(new Intent(getApplicationContext(), RatingActivity.class)
                                    .putExtra("zipCode", zipObject.getString("d_zip"))
                                    .putExtra("myjson", myJson)
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
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



