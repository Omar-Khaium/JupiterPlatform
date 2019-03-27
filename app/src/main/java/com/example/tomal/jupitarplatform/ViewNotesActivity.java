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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;

public class ViewNotesActivity extends AppCompatActivity {

    RecyclerView xListView;
    //ProgressBar xProgressBar;
    ViewNoteAdapter adapter;
    ArrayList<NoteModel> noteModels = new ArrayList<>();
    LinearLayout xLayout, xShimmerLayout;
    AlertDialog.Builder previewDialog;
    AlertDialog dialog;
    TextView xNoDataFound;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        Toolbar toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        xListView = findViewById(R.id.view_notes_list);
        xLayout = findViewById(R.id.note_layout);
        xShimmerLayout = findViewById(R.id.shimmer_note);
        xNoDataFound = findViewById(R.id.no_data_found);
        previewDialog = new AlertDialog.Builder(this);
        dialog = previewDialog.create();
        xListView.setLayoutManager(new LinearLayoutManager(this));

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getData() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "leadid=" + LEAD_ID);
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/getnotes.php")
                .post(body)
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
                            JSONArray jsonResult = json.getJSONArray("Notes");
                            if (jsonResult != null) {
                                xNoDataFound.setVisibility(View.GONE);
                                noteModels.clear();
                                for (int i = jsonResult.length() - 1; i >= 0; i--) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);
                                    noteModels.add(new NoteModel(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("u_fullname"),
                                            jsonObject.getString("timestamp"),
                                            jsonObject.getString("comment")));
                                }

                                adapter = new ViewNoteAdapter(getApplicationContext(), noteModels, previewDialog, dialog);
                                xListView.setAdapter(adapter);
                                xShimmerLayout.setVisibility(View.GONE);
                                xListView.setVisibility(View.VISIBLE);
                                xLayout.setVisibility(View.VISIBLE);
                                if (jsonResult.length() == 0) {
                                    xNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    xNoDataFound.setVisibility(View.GONE);
                                }

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
