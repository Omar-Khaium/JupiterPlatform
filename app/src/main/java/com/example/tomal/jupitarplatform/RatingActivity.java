package com.example.tomal.jupitarplatform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.emptybit.help.Format;
import org.json.JSONArray;
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

import static com.example.tomal.jupitarplatform.MainActivity.COMPANY_NAME;
import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;
import static com.example.tomal.jupitarplatform.MainActivity.SITE_ID;


public class RatingActivity extends AppCompatActivity {

    TextView subText, numberTextView;
    Button saveBtn;
    LinearLayout rLayout;
    EditText xFirstname, xState, xCity, xZip, xComment, xDomainNameEditText;
    Spinner spinner;
    RatingBar first_rating, second_rating, third_rating, forth_rating, fifth_rating, sixth_rating;
    String val_new;
    LeadCentralModel model;
    LinearLayout xLayout, xShimmerLayout;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint({"WrongViewCast", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratting_);

//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        Toolbar toolbar = findViewById(R.id.toolbar);
        xFirstname = findViewById(R.id.xfirstname);
        xCity = findViewById(R.id.xCity);
        xState = findViewById(R.id.xState);
        xZip = findViewById(R.id.xZip);
        xComment = findViewById(R.id.xComment);
        spinner = findViewById(R.id.spinner);
        xShimmerLayout = findViewById(R.id.shimmer_ratting);
        xLayout = findViewById(R.id.ratting_layout);
        first_rating = findViewById(R.id.first_ratting);
        second_rating = findViewById(R.id.second_ratting);
        third_rating = findViewById(R.id.third_ratting);
        forth_rating = findViewById(R.id.forth_ratting);
        fifth_rating = findViewById(R.id.fifth_ratting);
        sixth_rating = findViewById(R.id.sixth_ratting);
        rLayout = findViewById(R.id.rLayout);
        xDomainNameEditText = findViewById(R.id.domainNameEditText);
        saveBtn = findViewById(R.id.saveButton);
        numberTextView = findViewById(R.id.numberTextview);
        val_new = numberTextView.getText().toString().replaceAll("-", "");
        Linkify.addLinks(numberTextView, Linkify.ALL);
        numberTextView.setLinkTextColor(Color.parseColor("#6cb53f"));
        numberTextView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Intent CallIntent = new Intent(Intent.ACTION_DIAL);
                CallIntent.setData(Uri.parse("tel:" + (val_new)));
                startActivity(CallIntent);
            }
        });

        /*Gson gson = new Gson();
        model = gson.fromJson(getIntent().getStringExtra("myjson"), LeadCentralModel.class);*/

        TextView mTitle = toolbar.findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        subText = findViewById(R.id.toolbarSubText);
        subText.setText(COMPANY_NAME);
        /*if (model.getFname().equals("") || model.getFname().equals("null") || model.getFname()==null)
        {
            if (model.getLname().equals("") || model.getLname().equals("null") || model.getLname()==null){
                xFirstname.setText("-");
            }
            else {
                xFirstname.setText(Format.Text(model.getLname()));
            }
        }
        else {
            if (model.getLname().equals("") || model.getLname().equals("null") || model.getLname()==null){
                xFirstname.setText(model.getLname());
            }
            else {
                if (model.getLname().length()>2) {
                    xFirstname.setText(model.getFname() + " " + (model.getLname().substring(0, 2) + "."));
                } else {
                    xFirstname.setText(model.getFname() + " " + (model.getLname().substring(0, 1) + "."));
                }
            }
        }
        xCity.setText(model.getCity());
        xState.setText(model.getState());
        xZip.setText(getIntent().getStringExtra("zipCode"));*/

//        getData();
        saveButton();
        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

    rLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(v);
        }
    });
        getData();

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

    private void saveButton() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (!xComment.getText().toString().isEmpty() && !xDomainNameEditText.getText().toString().isEmpty()) {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, "site_id=" + SITE_ID +
                            "&name=" + xFirstname.getText().toString() +
                            "&city=" + xCity.getText().toString() +
                            "&state=" + xState.getText().toString() +
                            "&zip=" + xZip.getText().toString() +
                            "comments=" + xComment.getText().toString() +
                            "review_questionArray=" + first_rating.getRating() +
                            "review_questionArray=" + second_rating.getRating() +
                            "review_questionArray=" + third_rating.getRating() +
                            "review_questionArray=" + forth_rating.getRating() +
                            "review_questionArray=" + fifth_rating.getRating() +
                            "review_questionArray=" + sixth_rating.getRating() +
                            "view_on_site=" + spinner.getSelectedItem().toString()

                    );
                    Request request = new Request.Builder()
                            .url("https://jupiter.centralstationmarketing.com/api/ios/add_new_review.php")
                            .get()
                            .addHeader("Cookie", COOKIE_FOR_API)
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
                            RatingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200) {
                                            Toast.makeText(RatingActivity.this, "Rating Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }


                    });
                } else {
                    if (xComment.getText().toString().isEmpty()) {
                        xComment.setError("*Please enter comment");
                    } else if (xDomainNameEditText.getText().toString().isEmpty()) {
                        xComment.setError("*Please enter domain name");
                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getData() {
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
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    xFirstname.setText(Format.Name(jsonObject.getString("d_fname"), jsonObject.getString("d_lname")));
                                    xCity.setText(jsonObject.getString("d_city"));
                                    xState.setText(jsonObject.getString("d_state"));
                                    xZip.setText(jsonObject.getString("d_zip"));
                                }
                            }
                            xLayout.setVisibility(View.VISIBLE);
                            xShimmerLayout.setVisibility(View.GONE);

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
