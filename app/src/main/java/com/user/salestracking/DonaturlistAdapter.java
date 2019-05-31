package com.user.salestracking;

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
public class DonaturlistAdapter extends ArrayAdapter<DataDonatur> {

    //the list values in the List of type hero
    List<DataDonatur> donaturList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public DonaturlistAdapter(Context context, int resource, List<DataDonatur> donaturList) {
        super(context, resource, donaturList);
        this.context = context;
        this.resource = resource;
        this.donaturList = donaturList;
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
        TextView email = view.findViewById(R.id.tv_email);
        TextView JK = view.findViewById(R.id.tv_jk);
        TextView no_hp = view.findViewById(R.id.tv_noHp);

        //getting the hero of the specified position
        DataDonatur donatur = donaturList.get(position);

        //adding values to the list item
//        imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
        textViewName.setText(donatur.getName());
        alamat.setText(donatur.getAlamat());
        email.setText(donatur.getEmail());
        JK.setText(donatur.getJenis_kelamin());
        no_hp.setText(donatur.getNo_hp());

        //finally returning the view
        return view;
    }
}
