package com.user.salestracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class ClosingAdapter extends ArrayAdapter<DataListClosing> {

    //the list values in the List of type hero
    List<DataListClosing> dataListClosings;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public ClosingAdapter(Context context, int resource, List<DataListClosing> dataListClosings) {
        super(context, resource, dataListClosings);
        this.context = context;
        this.resource = resource;
        this.dataListClosings = dataListClosings;
    }

    //this will return the ListView Item as a View
    @SuppressLint("SetTextI18n")
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
        TextView nominal = view.findViewById(R.id.tv_nominal);
        TextView type_transfer = view.findViewById(R.id.tv_type_transfer);
        TextView registerBy = view.findViewById(R.id.tv_registeredBy);

        //getting the hero of the specified position
        DataListClosing dataListClosing = dataListClosings.get(position);

        //adding values to the list item
//        imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
        textViewName.setText(dataListClosing.getNama());
        alamat.setText(dataListClosing.getAlamat());
        no_hp.setText(dataListClosing.getNo_hp());
        date.setText(dataListClosing.getDate_record());
        type_transfer.setText(dataListClosing.getType_transfer());
        nominal.setText("Rp. "+dataListClosing.getNominal());
        registerBy.setText(dataListClosing.getAssign_by());

        //finally returning the view
        return view;
    }
}

