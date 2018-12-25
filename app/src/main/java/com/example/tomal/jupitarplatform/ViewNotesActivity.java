package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class ViewNotesActivity extends AppCompatActivity {

    ListView xListView;
    ProgressBar xProgressBar;
    ViewNoteAdapter adapter;
    ArrayList<NoteModel> noteModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        xListView = findViewById(R.id.view_notes_list);
        xProgressBar = findViewById(R.id.view_notes_progress);

        noteModels.add(new NoteModel("", "Alan", "24-Nov-2018", "Dummy Note 1"));
        noteModels.add(new NoteModel("", "Jon", "23-Nov-2018", "Dummy Note 2"));
        noteModels.add(new NoteModel("", "Bob", "25-Nov-2018", "Dummy Note 3"));
        noteModels.add(new NoteModel("", "Alice", "26-Nov-2018", "Dummy Note 4"));
        noteModels.add(new NoteModel("", "Marsh", "27-Nov-2018", "Dummy Note 5"));

        adapter = new ViewNoteAdapter(this, noteModels);
        xListView.setAdapter(adapter);
        xListView.setVisibility(View.VISIBLE);
        xProgressBar.setVisibility(View.GONE);
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
