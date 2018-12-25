package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.tomal.jupitarplatform.MainActivity.*;


public class DateRangeActivity extends AppCompatActivity {

    TextView toolbarSubText;
    DatePicker xDateFrom, xDateTo;
    Button xBack, xSubmit;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date__range);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarSubText = (TextView) toolbar.findViewById(R.id.toolbarSubText);
        toolbarSubText.setText(COMPANY_NAME);

        xBack = findViewById(R.id.back_button);
        xSubmit = findViewById(R.id.date_range_submit);
        xDateFrom = findViewById(R.id.date_range_from);
        xDateTo = findViewById(R.id.date_range_to);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        xDateTo.updateDate(mYear, mMonth, mDay);
        xDateFrom.updateDate(mYear, mMonth - 1, mDay);

        xBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
        xSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                int fromDate = (xDateFrom.getYear() * 10000) + ((xDateFrom.getMonth() + 1) * 100) + xDateFrom.getDayOfMonth();
                int toDate = (xDateTo.getYear() * 10000) + ((xDateTo.getMonth() + 1) * 100) + xDateTo.getDayOfMonth();
                if (fromDate > toDate) {
                    Toast.makeText(DateRangeActivity.this, "Incorrect Date Range", Toast.LENGTH_SHORT).show();
                } else {
                    fromDateRange = true;
                    startActivity(new Intent(getApplicationContext(), LeadCentralActivity.class).putExtra("From", fromDate).putExtra("To", toDate).putExtra("ID", getIntent().getStringExtra("ID")));
                }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
}
