package com.example.tomal.jupitarplatform;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.tomal.jupitarplatform.MainActivity.COMPANY_NAME;

public class ReviewNoteActivity extends AppCompatActivity {

    TextView toolbarSubText;
    Button xBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_note);

        toolbarSubText = (findViewById(R.id.toolbarSubText));
        xBack = (findViewById(R.id.back_button));

        toolbarSubText.setText(COMPANY_NAME);

        xBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
    }
}
