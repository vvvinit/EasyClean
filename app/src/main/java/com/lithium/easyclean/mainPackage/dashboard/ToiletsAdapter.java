package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;

import java.util.ArrayList;

public class ToiletsAdapter extends ArrayAdapter<Toilet> {


    public ToiletsAdapter(Context context, ArrayList<Toilet> toilets) {
        super(context, 0, toilets);
    }

    Toilet toilet;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        toilet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_toilet, parent, false);
        }
        // Lookup view for data population
        ImageView toiletStatus = convertView.findViewById(R.id.toilet_status);
        TextView toiletName = convertView.findViewById(R.id.toilet_name);
        TextView toiletCleaner = convertView.findViewById(R.id.cleaner);
        // Populate the data into the template view using the data object



        if(toilet.getCleaner().equals("null"))toiletStatus.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_toilet_blue, null));
        else {
            if(toilet.getTurbidity()>50)toiletStatus.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_toilet_red, null));
            else toiletStatus.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_toilet_green, null));
        }

        toiletName.setText(toilet.getId());
        toiletCleaner.setText(toilet.getCleaner());

        // Return the completed view to render on screen
        return convertView;
    }
}