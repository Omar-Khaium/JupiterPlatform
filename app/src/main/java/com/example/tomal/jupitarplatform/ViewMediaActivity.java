package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;

public class ViewMediaActivity extends AppCompatActivity {

    ViewMediaAdapter viewMediaAdapter;
    Button xBack;
    List list = new ArrayList();
    RecyclerView xMediaRecycler;
    TextView xNoDataFound;
    AlertDialog.Builder alertDialog;
    AlertDialog dialog;
    private ArrayList<NoteModel> noteModels = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        //++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        xMediaRecycler = findViewById(R.id.media_recycler);
        xBack = findViewById(R.id.back_button);
        xNoDataFound = findViewById(R.id.no_data_found);
        alertDialog = new AlertDialog.Builder(this);
        dialog = alertDialog.create();
        xMediaRecycler.setLayoutManager(new LinearLayoutManager((getApplicationContext())));

        xBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        getData();


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getData() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/getfiles.php?leadid=" + LEAD_ID)
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
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
                            System.out.println(json.toString());
                            JSONArray jsonResult = json.getJSONArray("Files");
                            if (jsonResult != null) {
                                xNoDataFound.setVisibility(View.GONE);
                                for (int i = jsonResult.length() - 1; i >= 0; i--) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    noteModels.add(new NoteModel(
                                                    jsonObject.getString("id"),
                                                    jsonObject.getString("title"),
                                                    jsonObject.getString("timestamp"),
                                                    jsonObject.getString("description"),
                                                    jsonObject.getString("file_type"),
                                                    jsonObject.getString("path"),
                                                    ""
                                            )

                                    );
                                }

                                viewMediaAdapter = new ViewMediaAdapter(getApplicationContext(), noteModels, alertDialog, dialog);
                                xMediaRecycler.setAdapter(viewMediaAdapter);
                                xMediaRecycler.setVisibility(View.VISIBLE);

                            }
                            if (jsonResult.length() == 0) {
                                xNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                xNoDataFound.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
