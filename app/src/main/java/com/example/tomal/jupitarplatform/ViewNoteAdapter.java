package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.emptybit.help.Format;

import java.util.ArrayList;

public class ViewNoteAdapter extends RecyclerView.Adapter<ViewNoteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NoteModel> arrayList;

    public ViewNoteAdapter(Context context, ArrayList<NoteModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_note_row_layout, viewGroup, false);
        ViewNoteAdapter.ViewHolder holder = new ViewNoteAdapter.ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewNoteAdapter.ViewHolder viewHolder, int position) {


        viewHolder.xName.setText(Format.Text(arrayList.get(position).getName()));
        viewHolder.xDate.setText(Format.Date(arrayList.get(position).getDate()));
        viewHolder.xNote.setText(Format.Text(arrayList.get(position).getNote()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView xName, xDate, xNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            xName = itemView.findViewById(R.id.view_notes_row_name);
            xDate = itemView.findViewById(R.id.view_notes_row_date);
            xNote = itemView.findViewById(R.id.view_notes_row_note);
        }
    }
}