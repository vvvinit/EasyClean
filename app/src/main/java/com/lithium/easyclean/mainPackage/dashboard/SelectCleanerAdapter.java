package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class SelectCleanerAdapter extends ArrayAdapter<User> implements Filterable {

    private ArrayList<User> users;

    public SelectCleanerAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
    }

    User user;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        user = (User) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_each_cleaner, parent, false);
        }
        // Lookup view for data population
        TextView userName = convertView.findViewById(R.id.userName);
        TextView email = convertView.findViewById(R.id.email);
        // Populate the data into the template view using the data object
        userName.setText(user.getName());
        email.setText(user.getEmail());

        // Return the completed view to render on screen
        return convertView;
    }



    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}