package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.User;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> implements Filterable {

    private ArrayList<User> users;
    private ArrayList<User> usersFiltered;

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.usersFiltered = users;
    }

    User user;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        user = (User) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
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
        return usersFiltered.size();
    }

    @Override
    public User getItem(int position) {
        return usersFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = users.size();
                    filterResults.values = users;

                }else{
                    ArrayList<User> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(User user:users){
                        if(user.getName().toLowerCase().contains(searchStr.toLowerCase()) || user.getEmail().toLowerCase().contains(searchStr.toLowerCase())){
                            resultsModel.add(user);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                usersFiltered = (ArrayList<User>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }
}