package fr.wildcodeschool.cafeconcert;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BarAdapter extends ArrayAdapter<Bar> {

    private final static int MARKER_HEIGHT = 72;
    private final static int MARKER_WIDTH = 72;
    private final static int ICON_HEIGHT = 100;
    private final static int ICON_WIDTH = 100;
    private boolean filter = false;
    private String mUId;
    private SingletonBar mSingleton;

    public BarAdapter(Context context, ArrayList<Bar> bars) {
        super(context, 0, bars);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        filter = sharedPreferences.getBoolean("filter", false);
        // Get the data item for this position
        final Bar bar = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bar, parent, false);
        }

        mSingleton = SingletonBar.getInstance();
        mUId = mSingleton.getUserID();

        // Lookup view for data population
        TextView tvBarName = convertView.findViewById(R.id.text_bar_name);
        final ImageView ibBar = convertView.findViewById(R.id.image_bar);
        ImageView navigate = convertView.findViewById(R.id.navigationButton);
        ImageView phone = convertView.findViewById(R.id.ib_phone);
        ImageButton ibWeb = convertView.findViewById(R.id.ib_web);
        ImageButton zoomAddress = convertView.findViewById(R.id.icon_adress);
        ImageButton icon = convertView.findViewById(R.id.status_icon);
        TextView barAdress = convertView.findViewById(R.id.adress_bar);
        final ImageView ivLogoBar = convertView.findViewById(R.id.iv_logobar);
        String[] parts = bar.getAddress().split(" ");
        String adressTerm = "";
        for (int i = 0; i < parts.length - 2; i++) {
            adressTerm += parts[i] + " ";
        }
        adressTerm.trim();
        barAdress.setText(adressTerm);

        ivLogoBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivLogoBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
            }
        });

        zoomAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("latitute", bar.getGeoPoint());
                intent.putExtra("longitude", bar.getGeoShape());
                getContext().startActivity(intent);
            }
        });

        // Populate the data into the template view using the data object
        tvBarName.setText(bar.getBarName());
        Glide.with(getContext()).load(bar.getPicture()).into(ibBar);
        Glide.with(getContext()).load(bar.getLogo()).into(ivLogoBar);

        MainActivity.setNavigation(navigate, bar, getContext());

        //Adding efficient likes/dislikes buttons
        setLikeIcon(icon, bar.getIsLiked());
        adaptLikesButton(icon, bar);
        setUserOpinion(icon, bar);

        // Drawer hide/shown
        final ConstraintLayout drawerBar = convertView.findViewById(R.id.drawer_bar);
        ibBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerBar.getVisibility() == View.GONE) {
                    drawerBar.setVisibility(View.VISIBLE);
                } else {
                    drawerBar.setVisibility(View.GONE);
                }
            }
        });

        //"To Map" button
        zoomAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = bar.getBarName();
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("BAR_NAME", name);
                getContext().startActivity(intent);
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
               if (bar.getPhoneNumber().isEmpty()){
                   Toast.makeText(getContext(), R.string.aucun_numero, Toast.LENGTH_LONG);
               }
            }
        });

        //Website button
        ibWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bar.getWebUrl();
                if (bar.getWebUrl().isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_website, Toast.LENGTH_LONG).show();
                } else {
                    if (url.charAt(0) == 'w') {
                        url = "http://" + url;
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    getContext().startActivity(i);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void adaptLikesButton(final ImageView icon, Bar bar) {

        Bitmap initialLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.neutral_like_icon);
        Bitmap neutralLikeMarker = Bitmap.createScaledBitmap(nLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        icon.setImageBitmap(neutralLikeMarker);

        //0 dislike, 1 like, 2 neutral
        if (bar.getIsLiked() == 1) {
            icon.setImageBitmap(likeMarker);
        } else if (bar.getIsLiked() == 0) {
            icon.setImageBitmap(dislikeMarker);
        } else {
            icon.setImageBitmap(neutralLikeMarker);
        }
        setLikeIcon(icon, bar.getIsLiked());
    }

    private void setUserOpinion(final ImageView icon, final Bar bar) {

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Guest restriction
                if (checkIfGuest(mUId)) {
                    Toast.makeText(getContext(), R.string.you_need_to_be_connected, Toast.LENGTH_LONG).show();
                    return;
                }

                if (bar.getIsLiked() == 1) {
                    mSingleton.setNewPreferences(0, bar);
                    bar.setIsLiked(0);
                    adaptLikesButton(icon, bar);
                } else if (bar.getIsLiked() == 0){
                    mSingleton.setNewPreferences(2, bar);
                    bar.setIsLiked(2);
                    adaptLikesButton(icon, bar);
                } else if (bar.getIsLiked() == 2){
                    mSingleton.setNewPreferences(1, bar);
                    bar.setIsLiked(1);
                    adaptLikesButton(icon, bar);
                }
                if (filter) {
                    Intent intent = new Intent(getContext(), BarListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    (getContext()).startActivity(intent);
                }
            }
        });
    }

    private void setLikeIcon(final ImageView icon, int likeStatus) {

        Bitmap nLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.neutral_like_icon);
        Bitmap neutralLikeMarker = Bitmap.createScaledBitmap(nLikeMarker, ICON_WIDTH, ICON_HEIGHT, false);

        Bitmap initialLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, ICON_WIDTH, ICON_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, ICON_WIDTH, ICON_HEIGHT, false);

        switch (likeStatus) {
            case 0:
                icon.setImageBitmap(dislikeMarker);
                break;
            case 1:
                icon.setImageBitmap(likeMarker);
                break;
            case 2:
                icon.setImageBitmap(neutralLikeMarker);
                break;
        }

    }

    private boolean checkIfGuest(String uId) {
        return uId.equals("guest");
    }

}
