package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.tomal.jupitarplatform.MainActivity.COMPANY_NAME;
import static com.example.tomal.jupitarplatform.MainActivity.SITE_ID;
import static com.example.tomal.jupitarplatform.MainActivity.fromDateRange;
public class CentralStationLeadAdapter extends ArrayAdapter<DomainModel>  {

    private Context context;
    private ArrayList<DomainModel> arrayList;
    private CentralStationLeadAdapter adp;

    public CentralStationLeadAdapter(@NonNull Context context, ArrayList<DomainModel> array) {
        super(context, R.layout.row_layout, array);
        this.context = context;
        this.arrayList = array;
    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_layout, parent, false);

        LinearLayout xLayout = convertView.findViewById(R.id.central_station_lead_row_layout);
        TextView xWebsite = convertView.findViewById(R.id.headerText);
        TextView xName = convertView.findViewById(R.id.subText);

        xWebsite.setText(arrayList.get(position).getDomain_name());
        xName.setText(arrayList.get(position).getCompany_name());
        xLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                SITE_ID = String.valueOf(arrayList.get(position).getSite_id());
                COMPANY_NAME = arrayList.get(position).getDomain_name();
                fromDateRange = false;
                context.startActivity(new Intent(context, LeadCentralActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return convertView;
    }



}
