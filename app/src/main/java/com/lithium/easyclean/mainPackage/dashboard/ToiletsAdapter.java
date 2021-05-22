package com.lithium.easyclean.mainPackage.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;
import com.lithium.easyclean.mainPackage.start.User;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_toilet_admin, parent, false);
        }
        // Lookup view for data population
        LinearLayout toiletStatus = convertView.findViewById(R.id.toilet_status);
        TextView toiletLocation = convertView.findViewById(R.id.toilet_location);
        TextView toiletTurbidity = convertView.findViewById(R.id.toilet_turbidity);
        TextView toiletID = convertView.findViewById(R.id.toilet_id);
        TextView toiletCleanerName = convertView.findViewById(R.id.cleaner_name);



            if(toilet.getTurbidity()>50)toiletStatus.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.unclean_background, null));
            else toiletStatus.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.clean_background, null));

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    rootRef.child("cleaners").child(toilet.getCleaner()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            toiletCleanerName.setText(user.getName());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

 toiletLocation.setText(toilet.getLocation());
        toiletTurbidity.setText(Integer.toString(toilet.getTurbidity()));
        toiletID.setText(toilet.getId());


        // Return the completed view to render on screen
        return convertView;
    }


}