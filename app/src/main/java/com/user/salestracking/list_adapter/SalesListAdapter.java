package com.user.salestracking.list_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.user.salestracking.Data.DataSales;
import com.user.salestracking.R;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class SalesListAdapter extends ArrayAdapter<DataSales> {

    //the list values in the List of type hero
    List<DataSales> dataListCalls;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public SalesListAdapter(Context context, int resource, List<DataSales> dataListCalls) {
        super(context, resource, dataListCalls);
        this.context = context;
        this.resource = resource;
        this.dataListCalls = dataListCalls;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
//        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textViewName = view.findViewById(R.id.tv_name);
        TextView alamat = view.findViewById(R.id.tv_alamat);
        TextView no_hp = view.findViewById(R.id.tv_noHp);
        TextView date = view.findViewById(R.id.tv_date);
        TextView registerBy = view.findViewById(R.id.tv_registeredBy);

        //getting the hero of the specified position
        DataSales dataListCall = dataListCalls.get(position);

        //adding values to the list item
//        imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
        textViewName.setText(dataListCall.getName());
        alamat.setText(dataListCall.getAlamat());
        no_hp.setText(dataListCall.getNo_hp());
        date.setVisibility(View.GONE);
        registerBy.setVisibility(View.GONE);
//        date.setText(dataListCall.getDate_record());
//        registerBy.setText(dataListCall.getAssign_by());

        //finally returning the view
        return view;
    }
}


