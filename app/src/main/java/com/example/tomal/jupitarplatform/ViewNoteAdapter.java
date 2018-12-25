package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewNoteAdapter extends ArrayAdapter<NoteModel> {

    private Context context;
    private ArrayList<NoteModel> arrayList;

    public ViewNoteAdapter(@NonNull Context context, ArrayList<NoteModel> array) {
        super(context, R.layout.row_layout, array);
        this.context = context;
        this.arrayList = array;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.view_note_row_layout, parent, false);

        TextView xName = convertView.findViewById(R.id.view_notes_row_name);
        TextView xDate = convertView.findViewById(R.id.view_notes_row_date);
        TextView xNote = convertView.findViewById(R.id.view_notes_row_note);

        xName.setText(arrayList.get(position).getName());
        xDate.setText(arrayList.get(position).getDate());
        xNote.setText(arrayList.get(position).getNote());

        return convertView;
    }


}