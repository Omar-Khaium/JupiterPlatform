package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.emptybit.help.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewMediaAdapter extends RecyclerView.Adapter<ViewMediaAdapter.ViewHolder> {
    Context context;
    AlertDialog.Builder alertDialog;
    AlertDialog dialog;
    ArrayList<NoteModel> arrayList;
    private MediaController mediaController;


    public ViewMediaAdapter(Context context, ArrayList<NoteModel> arrayList, AlertDialog.Builder previewDialog, AlertDialog dialog) {
        this.context = context;
        this.arrayList = arrayList;
        this.alertDialog = previewDialog;
        this.dialog = dialog;
        this.mediaController = new MediaController(context);
    }

    @NonNull
    @Override
    public ViewMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMediaAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.xFileName.setText(Format.Text(arrayList.get(i).getName()));

        if (arrayList.get(i).getDate().equals("null") || arrayList.get(i).getDate().equals("")) {
            viewHolder.xDate.setText("-");
        } else {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat myCreated = new SimpleDateFormat("EEEE, dd MMMM, yyyy");
            try {
                viewHolder.xDate.setText(myCreated.format(fromUser.parse(arrayList.get(i).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        viewHolder.xFileType.setText(Format.Text(arrayList.get(i).getFileType()));
        viewHolder.xDescription.setText(Format.Text(arrayList.get(i).getNote()));
        viewHolder.xLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(i).getFileType().equals("image")) {

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View convertView = inflater.inflate(R.layout.image_view_layout, new LinearLayout(context), false);

                    ImageView xPhoto = convertView.findViewById(R.id.alert_image);
                    ImageView xClose = convertView.findViewById(R.id.alert_close);

                    Picasso.get().load(Format.Text(arrayList.get(i).getPhoto())).into(xPhoto);
                    xClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setView(convertView);
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ((ViewGroup) convertView.getParent()).removeView(convertView);
                        }
                    });
                    dialog = alertDialog.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.show();
                } else if (arrayList.get(i).getFileType().equals("video")) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View convertView = inflater.inflate(R.layout.video_view_layout, new LinearLayout(context), false);

                    VideoView xPhoto = convertView.findViewById(R.id.alert_image);
                    ImageView xClose = convertView.findViewById(R.id.alert_close);

                    //Uri videoFileUri = Uri.parse(arrayList.get(i).getFileType());
                    xPhoto.setVideoPath(arrayList.get(i).getPhoto());
                    xPhoto.start();
                    xClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setView(convertView);
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ((ViewGroup) convertView.getParent()).removeView(convertView);
                        }
                    });
                    dialog = alertDialog.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.show();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView xFileName;
        TextView xDate;
        TextView xFileType;
        TextView xDescription;
        LinearLayout xLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            xFileName = itemView.findViewById(R.id.view_media_row_name);
            xDate = itemView.findViewById(R.id.view_media_row_date);
            xFileType = itemView.findViewById(R.id.view_media_row_filetype);
            xDescription = itemView.findViewById(R.id.view_media_row_file_description);
            xLayout = itemView.findViewById(R.id.media_layout);
        }


    }
}
