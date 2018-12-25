package com.example.tomal.jupitarplatform;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LeadCentralAdapter extends ArrayAdapter<LeadCentralModel> {


    private Context context;
    private ArrayList<LeadCentralModel> arrayList;

    public LeadCentralAdapter(@NonNull Context context, ArrayList<LeadCentralModel> arrays) {
        super(context, R.layout.lead_central_layout, arrays);

        this.context=context;
        this.arrayList=arrays;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.lead_central_layout, parent, false);


        TextView lName = convertView.findViewById(R.id.callerNameTextview);
        TextView lCallType = convertView.findViewById(R.id.callTypeTextview);
        TextView lAddress = convertView.findViewById(R.id.addressTextview);
        TextView lNumber = convertView.findViewById(R.id.numberTextview);
        TextView lDate = convertView.findViewById(R.id.dateTextView);
        if(arrayList.get(position).getCompany().equals("null") || arrayList.get(position).getCompany().equals("") || arrayList.get(position).getCompany()==null) {

            if(
                    (((arrayList.get(position).getFname().equals("null"))
                            ||arrayList.get(position).getFname().equals("")
                            || (arrayList.get(position).getFname()==null))
                            &&        ((arrayList.get(position).getLname().equals("null"))
                            || (arrayList.get(position).getLname().equals(""))
                            || arrayList.get(position).getLname()==null)))
            {
                lName.setText("-");
            }
            else{

                lName.setText(arrayList.get(position).getFname() + " ".concat(arrayList.get(position).getLname()));
            }

        }
        else{
            lName.setText(arrayList.get(position).getCompany());

        }
        lCallType.setText(arrayList.get(position).getFormType());
        lAddress.setText(arrayList.get(position).getCity()+", " + arrayList.get(position).getState());
        lNumber.setText(arrayList.get(position).getPhone());

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat myCreated = new SimpleDateFormat("EEEE, dd MMMM, yyyy hh:mm:ss a");
        try {
            lDate.setText(myCreated.format(fromUser.parse(arrayList.get(position).getTimestamp())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
