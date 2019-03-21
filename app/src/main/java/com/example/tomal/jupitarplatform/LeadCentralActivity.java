package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

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

import static com.example.tomal.jupitarplatform.MainActivity.COMPANY_NAME;
import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.DEVICE_TOKEN;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;
import static com.example.tomal.jupitarplatform.MainActivity.SITE_ID;
import static com.example.tomal.jupitarplatform.MainActivity.fromDateRange;


public class LeadCentralActivity extends AppCompatActivity {

    ListView listView;
    private ArrayList<LeadCentralModel> arrayListLeadCentral = new ArrayList<>();
    private LeadCentralAdapter centralLeadAdapter;
    private FloatingActionButton fab;
    private Button dateRangeChange;
    private Button backButton;
    LinearLayout xLayout, xShimmerLayout;
    private TextView subtext, xDataAvailability;
    DashboardProfileModel dashboardProfileModel;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_central_);

        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.floatingButton);
        dateRangeChange = findViewById(R.id.dateRangeButton);
        backButton = findViewById(R.id.back_button);
        subtext = (findViewById(R.id.toolbarSubText));
        xLayout = (findViewById(R.id.lead_central_layout));
        xShimmerLayout = (findViewById(R.id.shimmer_lead_central));
        xDataAvailability = findViewById(R.id.no_data_found);


        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dashboardProfileModel = new DashboardProfileModel();
        subtext.setText(COMPANY_NAME);

        if (fromDateRange) {
            getDateRangedData();
        } else {
            getData();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateLeadActivity.class));
            }
        });
        dateRangeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DateRangeActivity.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LEAD_ID = arrayListLeadCentral.get(position).getId();

                Gson gson = new Gson();
                String myJson = gson.toJson(arrayListLeadCentral.get(position));
                startActivity(new Intent(getApplicationContext(), DetailsActivity.class).putExtra("myjson", myJson));
            }
        });
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
        RequestBody body = RequestBody.create(mediaType, "userid=" + SITE_ID + "&deviceToken=" + DEVICE_TOKEN + "&undefined=");
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/LeadCentral.php?site_id=" + SITE_ID)
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
                            JSONArray jsonResult = json.getJSONArray("LeadCentral");
                            if (jsonResult != null) {
                                xDataAvailability.setVisibility(View.GONE);
                                for (int i = 0; i < jsonResult.length(); i++) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    arrayListLeadCentral.add(new LeadCentralModel(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("fname"),
                                            jsonObject.getString("lname"),
                                            jsonObject.getString("company"),
                                            jsonObject.getString("phone"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("timestamp"),
                                            jsonObject.getString("set_appointment"),
                                            jsonObject.getString("sold"),
                                            jsonObject.getString("valid_other_desc"),
                                            jsonObject.getString("directory_member_id"),
                                            jsonObject.getString("billable"),
                                            jsonObject.getString("form_type"),
                                            jsonObject.getString("recording_url"),
                                            jsonObject.getString("call_duration"),
                                            jsonObject.getString("city"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("valid"),
                                            jsonObject.getString("customer"),
                                            jsonObject.getString("parent_id"),
                                            jsonObject.getString("url_tag"),
                                            jsonObject.getString("url_tracking_id"),
                                            jsonObject.getString("revenue"),
                                            jsonObject.getString("cc_comment"),
                                            jsonObject.getString("csm_comment"),
                                            jsonObject.getString("company_name")));


                                }
                                centralLeadAdapter = new LeadCentralAdapter(getApplicationContext(), arrayListLeadCentral);
                                listView.setAdapter(centralLeadAdapter);
                                xShimmerLayout.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                xLayout.setVisibility(View.VISIBLE);
                            }
                            if (jsonResult.length()==0){
                                xDataAvailability.setVisibility(View.VISIBLE);
                            } else {
                                xDataAvailability.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                        if (arrayListLeadCentral.size()==0){
                            xDataAvailability.setVisibility(View.VISIBLE);
                        } else {
                            xDataAvailability.setVisibility(View.GONE);
                        }
                    centralLeadAdapter = new LeadCentralAdapter(LeadCentralActivity.this, arrayListLeadCentral);
                    listView.setAdapter(centralLeadAdapter);
                    xShimmerLayout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                } else {

                    getSearchData(s.toLowerCase());
                }
                return false;

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getSearchData(String s) {
        ArrayList<LeadCentralModel> leads = new ArrayList<>();
        for (LeadCentralModel leadCentralModel : arrayListLeadCentral) {
            if (
                    leadCentralModel.getCompany().toLowerCase().contains(s)
                            || leadCentralModel.getFname().toLowerCase().contains(s)
                            || leadCentralModel.getLname().toLowerCase().contains(s)
                            || leadCentralModel.getCompany().toLowerCase().contains(s)
                            || leadCentralModel.getEmail().toLowerCase().contains(s)
                            || leadCentralModel.getState().toLowerCase().contains(s)
                            || leadCentralModel.getCity().toLowerCase().contains(s)) {
                leads.add(leadCentralModel);
            }
        }
        centralLeadAdapter = new LeadCentralAdapter(LeadCentralActivity.this, leads);
        listView.setAdapter(centralLeadAdapter);
        xShimmerLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if (leads.size()==0){
            xDataAvailability.setVisibility(View.VISIBLE);
        } else {
            xDataAvailability.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getDateRangedData() {

        final int startDate = getIntent().getIntExtra("From", 0);
        final int endDate = getIntent().getIntExtra("To", 0);
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "userid=" + SITE_ID + "&deviceToken=" + DEVICE_TOKEN + "&undefined=");
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/LeadCentral.php?site_id=" + SITE_ID)
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
                            JSONArray jsonResult = json.getJSONArray("LeadCentral");
                            if (jsonResult != null) {

                                for (int i = 0; i < jsonResult.length(); i++) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    LeadCentralModel obj = new LeadCentralModel(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("fname"),
                                            jsonObject.getString("lname"),
                                            jsonObject.getString("company"),
                                            jsonObject.getString("phone"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("timestamp"),
                                            jsonObject.getString("set_appointment"),
                                            jsonObject.getString("sold"),
                                            jsonObject.getString("valid_other_desc"),
                                            jsonObject.getString("directory_member_id"),
                                            jsonObject.getString("billable"),
                                            jsonObject.getString("form_type"),
                                            jsonObject.getString("recording_url"),
                                            jsonObject.getString("call_duration"),
                                            jsonObject.getString("city"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("valid"),
                                            jsonObject.getString("customer"),
                                            jsonObject.getString("parent_id"),
                                            jsonObject.getString("url_tag"),
                                            jsonObject.getString("url_tracking_id"),
                                            jsonObject.getString("revenue"),
                                            jsonObject.getString("cc_comment"),
                                            jsonObject.getString("csm_comment"),
                                            jsonObject.getString("company_name"));
                                    int currentDate = (Integer.parseInt(obj.getTimestamp().substring(0, 4)) * 10000) +
                                            (Integer.parseInt(obj.getTimestamp().substring(5, 7)) * 100) +
                                            (Integer.parseInt(obj.getTimestamp().substring(8, 10)));
                                    if (currentDate >= startDate && currentDate <= endDate) {
                                        arrayListLeadCentral.add(obj);
                                    }
                                }
                                centralLeadAdapter = new LeadCentralAdapter(getApplicationContext(), arrayListLeadCentral);
                                listView.setAdapter(centralLeadAdapter);
                                xShimmerLayout.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                xLayout.setVisibility(View.VISIBLE);
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
