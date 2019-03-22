package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;

public class CentralStationLeadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    TextView xDataAvailability;
    ArrayList<DomainModel> arrayList = new ArrayList<>();
    private CentralStationLeadAdapter adp;
    LinearLayout xLayout, xShimmerLayout;
    protected static boolean trackLoginFromDashboard = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_station_lead_);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Findings

        listView = findViewById(R.id.listView);
        xDataAvailability = findViewById(R.id.no_data_found);
        xShimmerLayout = findViewById(R.id.shimmer_central_station_layout);
        xLayout = findViewById(R.id.central_station_layout);

        getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getData()
    {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "page=1&undefined=");
        Request request = new Request.Builder()

                .url("https://jupiter.centralstationmarketing.com/api/ios/domain.php")
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(CentralStationLeadActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                            JSONArray jsonResult = json.getJSONArray("Domains");

                            if (jsonResult != null) {
                                arrayList.clear();
                                xDataAvailability.setVisibility(View.GONE);
                                for (int i = 0; i < jsonResult.length(); i++) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    arrayList.add(new DomainModel(
                                            jsonObject.getInt("site_id"),
                                            jsonObject.getString("domain_name"),
                                            jsonObject.getInt("company_id"),
                                            jsonObject.getString("company_name"),
                                            jsonObject.getString("contact")));
                                }
                                Collections.sort(arrayList, DomainModel.checkDomainNameComparator);
                                adp = new CentralStationLeadAdapter(getApplicationContext(), arrayList);
                                listView.setAdapter(adp);
                                xShimmerLayout.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                xLayout.setVisibility(View.VISIBLE);
                            }
                            if (jsonResult.length()==0){
                                xDataAvailability.setVisibility(View.VISIBLE);
                            } else {
                                xDataAvailability.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CentralStationLeadActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        trackLoginFromDashboard = true;
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
        } else if (id == R.id.nav_sign_out) {
            trackLoginFromDashboard = true;
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                getSearchData(s);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onQueryTextChange(String s) {

                xShimmerLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                if (s.isEmpty()) {
                    //getData();
                    if (arrayList.size()==0){
                        xDataAvailability.setVisibility(View.VISIBLE);
                    } else {
                        xDataAvailability.setVisibility(View.GONE);
                    }
                    adp = new CentralStationLeadAdapter(CentralStationLeadActivity.this, arrayList);
                    listView.setAdapter(adp);
                    xShimmerLayout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                } else if (!s.isEmpty()) {
                    getSearchData(s.toLowerCase());
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getSearchData(String s) {
        ArrayList<DomainModel> companies = new ArrayList<>();
        for (DomainModel domainModels : arrayList) {
            if (domainModels.getDomain_name().toLowerCase().contains(s) || domainModels.getCompany_name().toLowerCase().contains(s)) {
                companies.add(domainModels);
            }
        }
        adp = new CentralStationLeadAdapter(CentralStationLeadActivity.this, companies);
        listView.setAdapter(adp);
        xShimmerLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        if (companies.size()==0){
            xDataAvailability.setVisibility(View.VISIBLE);
        } else {
            xDataAvailability.setVisibility(View.GONE);
        }
    }

}




