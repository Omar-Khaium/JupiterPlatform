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

public class LeadDetailsAdapter extends ArrayAdapter<LeadDetailsModel> {

    private Context context;
    private ArrayList<LeadDetailsModel> arrayListLeadDetails;

    public LeadDetailsAdapter( Context context, ArrayList<LeadDetailsModel> array) {
        super(context, R.layout.activity_details_);
        this.context=context;
        this.arrayListLeadDetails=array;
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_details_, parent, false);

        TextView customerView = convertView.findViewById(R.id.customerViewText);
        TextView commentText = convertView.findViewById(R.id.commentText);

        customerView.setText(arrayListLeadDetails.get(position).getCustomer());
        commentText.setText(arrayListLeadDetails.get(position).getComment());

        return convertView;
    }
}
