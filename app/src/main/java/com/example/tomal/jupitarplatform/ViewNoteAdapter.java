package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.emptybit.help.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewNoteAdapter extends RecyclerView.Adapter<ViewNoteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NoteModel> arrayList;
    AlertDialog.Builder previewDialog;
    AlertDialog dialog;

    public ViewNoteAdapter(Context context, ArrayList<NoteModel> arrayList, AlertDialog.Builder previewDialog, AlertDialog dialog) {
        this.context = context;
        this.arrayList = arrayList;
        this.previewDialog = previewDialog;
        this.dialog = dialog;
    }
    @NonNull
    @Override
    public ViewNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_note_row_layout, viewGroup, false);
        ViewNoteAdapter.ViewHolder holder = new ViewNoteAdapter.ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewNoteAdapter.ViewHolder viewHolder, final int position) {


        viewHolder.xName.setText(Format.Text(arrayList.get(position).getName()));

        if (arrayList.get(position).getDate().equals("null") || arrayList.get(position).getDate().equals("")) {
            viewHolder.xDate.setText("-");
        } else {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat myCreated = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            try {
                viewHolder.xDate.setText(myCreated.format(fromUser.parse(arrayList.get(position).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        viewHolder.xNote.setText(Format.Text(arrayList.get(position).getNote()));
        viewHolder.xLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View convertView = inflater.inflate(R.layout.note_followup_layout, new LinearLayout(context), false);

                TextView xName = convertView.findViewById(R.id.name);
                TextView xDate = convertView.findViewById(R.id.date);
                TextView xNote = convertView.findViewById(R.id.note);

                Button xClose = convertView.findViewById(R.id.close);

                xName.setText(Format.Text(arrayList.get(position).getName()));

                if (arrayList.get(position).getDate().equals("null") || arrayList.get(position).getDate().equals("")) {
                    xDate.setText("-");
                } else {
                    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat myCreated = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                    try {
                        xDate.setText(myCreated.format(fromUser.parse(arrayList.get(position).getDate())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                xNote.setText(Format.Text(arrayList.get(position).getNote()));

                xClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                previewDialog.setView(convertView);
                previewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((ViewGroup) convertView.getParent()).removeView(convertView);
                    }
                });
                dialog = previewDialog.create();

                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView xName, xDate, xNote;
        LinearLayout xLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            xName = itemView.findViewById(R.id.view_notes_row_name);
            xDate = itemView.findViewById(R.id.view_notes_row_date);
            xNote = itemView.findViewById(R.id.view_notes_row_note);
            xLayout = itemView.findViewById(R.id.note_layout);
        }
    }
}