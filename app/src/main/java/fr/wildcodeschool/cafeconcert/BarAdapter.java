package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BarAdapter extends ArrayAdapter<Bar> {

    public BarAdapter(Context context, ArrayList<Bar> bars) {
        super(context, 0, bars);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Bar bar = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bar, parent, false);
        }
        // Lookup view for data population
        TextView tvBarName = convertView.findViewById(R.id.text_bar_name);
        ImageButton ibBar = convertView.findViewById(R.id.image_bar);
        ImageView navigate = convertView.findViewById(R.id.navigationButton);
        navigate.setImageResource(R.mipmap.navigate);
        // Populate the data into the template view using the data object
        tvBarName.setText(bar.getBarName());
        ibBar.setBackgroundResource(bar.getPicture());
        MainActivity.setNavigation(navigate, bar, getContext());

        // Return the completed view to render on screen
        return convertView;
    }
}
