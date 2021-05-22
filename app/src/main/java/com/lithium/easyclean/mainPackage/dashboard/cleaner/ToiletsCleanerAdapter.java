package com.lithium.easyclean.mainPackage.dashboard.cleaner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.start.Toilet;

import java.util.ArrayList;

public class ToiletsCleanerAdapter extends ArrayAdapter<Toilet> {

    private ArrayList<Toilet> toilets;
    private ArrayList<Toilet> toiletsUnfiltered;
    public ToiletsCleanerAdapter(Context context, ArrayList<Toilet> toilets) {
        super(context, 0, toilets);
        this.toilets = toilets;
        this.toiletsUnfiltered = toilets;
    }

    Toilet toilet;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        toilet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_toilet_cleaner, parent, false);
        }
        // Lookup view for data population
        LinearLayout toiletStatus = convertView.findViewById(R.id.toilet_status);
        TextView toiletLocation = convertView.findViewById(R.id.toilet_location);
        TextView toiletTurbidity = convertView.findViewById(R.id.toilet_turbidity);
        TextView toiletID = convertView.findViewById(R.id.toilet_id);

        int threshold = Integer.parseInt(getContext().getString(R.string.TURBIDITY_THRESHOLD));
        if (toilet.getTurbidity() > threshold)
            toiletStatus.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.unclean_background, null));
        else
            toiletStatus.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.clean_background, null));


        toiletLocation.setText(toilet.getLocation());
        toiletTurbidity.setText(Integer.toString(toilet.getTurbidity()));
        toiletID.setText(toilet.getId());


        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return toiletsUnfiltered.size();
    }

    @Override
    public Toilet getItem(int position) {
        return toiletsUnfiltered.get(position);
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
                    filterResults.count = toilets.size();
                    filterResults.values = toilets;

                }else{
                    ArrayList<Toilet> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(Toilet toilet:toilets){
                        if(toilet.getLocation().toLowerCase().contains(searchStr.toLowerCase())){
                            resultsModel.add(toilet);
                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                toiletsUnfiltered = (ArrayList<Toilet>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}