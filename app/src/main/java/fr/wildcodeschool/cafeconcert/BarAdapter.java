package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
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
        final Bar bar = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bar, parent, false);
        }
        // Lookup view for data population
        TextView tvBarName = convertView.findViewById(R.id.text_bar_name);
        ImageButton ibBar = convertView.findViewById(R.id.image_bar);
        ImageView navigate = convertView.findViewById(R.id.navigationButton);
        ImageView phone = convertView.findViewById(R.id.ib_phone);
        ImageButton ibWeb = convertView.findViewById(R.id.ib_web);
        navigate.setImageResource(R.mipmap.navigate);

        // Populate the data into the template view using the data object
        tvBarName.setText(bar.getBarName());
        ibBar.setBackgroundResource(bar.getPicture());
        MainActivity.setNavigation(navigate, bar, getContext());

        // Drawer hide/shown
        final ConstraintLayout drawerBar = convertView.findViewById(R.id.drawer_bar);
        ibBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerBar.getVisibility() == View.GONE) {
                    drawerBar.setVisibility(View.VISIBLE);
                } else {
                    drawerBar.setVisibility(View.GONE);
                }
            }
        });

        //Phone button
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + bar.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });

        //Website button
        ibWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bar.getWebUrl();
                if (url.charAt(0) == 'w') {
                    url = "http://" + url;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        });
        
        // Return the completed view to render on screen
        return convertView;
    }

    public void adaptLikesButton(ImageView like, ImageView dontLike, Bar bar) {

        Bitmap initialLikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nDislikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_dislike_icon);
        Bitmap neutralDislikeMarker = Bitmap.createScaledBitmap(nDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nLikeMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_like_icon);
        Bitmap neutralLikeMarker = Bitmap.createScaledBitmap(nLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        /*Bitmap initialNeutralMarker = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.neutral_ping);
        Bitmap neutralMarker = Bitmap.createScaledBitmap(initialNeutralMarker, MARKER_WIDTH, MARKER_HEIGHT, false);*/

        like.setImageBitmap(neutralLikeMarker);
        dontLike.setImageBitmap(neutralDislikeMarker);

        //0 dislike, 1 like, 2 neutral
        if (bar.getIsLiked() == 1) {
            like.setImageBitmap(likeMarker);
            dontLike.setImageBitmap(neutralDislikeMarker);
        } else if (bar.getIsLiked() == 0) {
            dontLike.setImageBitmap(dislikeMarker);
            like.setImageBitmap(neutralLikeMarker);
        }
    }

    public void setUserOpinion(final ImageView like, final ImageView dontLike, final Bar bar) {

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.getIsLiked() != 1) {
                    bar.setIsLiked(1);
                    adaptLikesButton(like, dontLike, bar);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, bar);
                }
            }
        });


        dontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.getIsLiked() != 0) {
                    bar.setIsLiked(0);
                    adaptLikesButton(like, dontLike, bar);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, bar);
                }
            }
        });
    }


}
