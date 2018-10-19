package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BarAdapter extends ArrayAdapter<Bar> {

    private final static int MARKER_HEIGHT = 72;
    private final static int MARKER_WIDTH = 72;
    private final static int ICON_HEIGHT = 100;
    private final static int ICON_WIDTH = 100;
    //private static ArrayList<Bar> filterBars; // TODO to delete ?
    //private ArrayList<Bar> bars;
    private boolean filter = false;
    private String mUId;
    private FirebaseAuth mAuth;


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

        //Is user guest or registered ?
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mUId = "guest";
        } else {
            mUId = mAuth.getCurrentUser().getUid();
        }



        // Lookup view for data population
        TextView tvBarName = convertView.findViewById(R.id.text_bar_name);
        ImageButton ibBar = convertView.findViewById(R.id.image_bar);
        ImageView navigate = convertView.findViewById(R.id.navigationButton);
        navigate.setBackgroundResource(R.mipmap.navigate);
        ImageView phone = convertView.findViewById(R.id.ib_phone);
        phone.setBackgroundResource(R.mipmap.phonelogo);
        ImageButton ibWeb = convertView.findViewById(R.id.ib_web);
        ibWeb.setBackgroundResource(R.mipmap.globeicon);
        navigate.setImageResource(R.mipmap.navigate);
        ImageView likeButton = convertView.findViewById(R.id.like_button);
        ImageView dontLikeButton = convertView.findViewById(R.id.dont_like_button);
        ImageView icon = convertView.findViewById(R.id.status_icon);
        ImageView fondIcon = convertView.findViewById(R.id.fond_icon);
        ImageView fondAddress = convertView.findViewById(R.id.fond_bar_address);
        ImageView fondName = convertView.findViewById(R.id.fond_bar_name);
        TextView barAdress = convertView.findViewById(R.id.adress_bar);
        fondIcon.setBackgroundResource(R.drawable.fond_icone_like);
        fondName.setBackgroundResource(R.drawable.fond_txt);
        ImageView iconAdress = convertView.findViewById(R.id.icon_adress);
        iconAdress.setBackgroundResource(R.drawable.ic_my_location_black_24dp);
        ConstraintLayout constraintLayout = convertView.findViewById(R.id.drawer_bar);
        constraintLayout.setBackgroundResource(R.drawable.fondpopup);
        fondAddress.setBackgroundResource(R.drawable.fond_txt);
        String[] parts = bar.getAddress().split(" ");
        String adressTerm = "";
        for (int i = 0; i < parts.length - 2; i++) {
            adressTerm += parts[i] + " ";
        }
        adressTerm.trim();
        barAdress.setText(adressTerm);

        // Populate the data into the template view using the data object
        tvBarName.setText(bar.getBarName());
        MainActivity.setNavigation(navigate, bar, getContext());

        //Adding efficient likes/dislikes buttons
        setLikeIcon(icon, bar.getIsLiked());
        adaptLikesButton(likeButton, dontLikeButton, icon, bar);
        setUserOpinion(likeButton, dontLikeButton, icon, bar);

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

        TextView textPhone = convertView.findViewById(R.id.phone_text);
        TextView textWebSite = convertView.findViewById(R.id.web_text);
        textPhone.setText(bar.getPhoneNumber());
        if (bar.getWebUrl().isEmpty()) {
            textWebSite.setText(R.string.no_website);
        } else {
            textWebSite.setText(bar.getWebUrl());
        }

        //"To Map" button
        fondAddress.setOnClickListener(new View.OnClickListener() {
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
            }
        });

        //Website button
        ibWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bar.getWebUrl();
                if (bar.getWebUrl().isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_website, Toast.LENGTH_LONG);
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

    private void adaptLikesButton(ImageView like, ImageView dontLike, final ImageView icon, Bar bar) {

        Bitmap initialLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_ping);
        Bitmap likeMarker = Bitmap.createScaledBitmap(initialLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap initialDislikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.love_break_ping);
        Bitmap dislikeMarker = Bitmap.createScaledBitmap(initialDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nDislikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.neutral_dislike_icon);
        Bitmap neutralDislikeMarker = Bitmap.createScaledBitmap(nDislikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

        Bitmap nLikeMarker = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.neutral_like_icon);
        Bitmap neutralLikeMarker = Bitmap.createScaledBitmap(nLikeMarker, MARKER_WIDTH, MARKER_HEIGHT, false);

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
        setLikeIcon(icon, bar.getIsLiked());
    }

    private void setUserOpinion(final ImageView like, final ImageView dontLike, final ImageView icon, final Bar bar) {

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference refBar = firebaseDatabase.getReference("cafeconcert");
        final String[] barKey = new String[1];
        DatabaseReference refUser = firebaseDatabase.getReference("users");
        final DatabaseReference currentUser = refUser.child(mUId).child("bars");

        // Guest restriction
        if(!checkIfGuest(mUId)) {

            refBar.orderByChild("barName").equalTo(bar.getBarName()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        barKey[0] = childSnapshot.getKey();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Guest restriction
                if(checkIfGuest(mUId)) {
                    Toast.makeText(getContext(), R.string.you_need_to_be_connected, Toast.LENGTH_LONG).show();
                    return;
                }

                if (bar.getIsLiked() != 1) {
                    bar.setIsLiked(1);
                    adaptLikesButton(like, dontLike, icon, bar);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, icon, bar);
                }
                if (filter) {
                    Intent intent = new Intent(getContext(), BarListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    (getContext()).startActivity(intent);
                }
                currentUser.child(barKey[0]).child("isLiked").setValue(bar.getIsLiked());
            }
        });

        dontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Guest restriction
                if(checkIfGuest(mUId)) {
                    Toast.makeText(getContext(), R.string.you_need_to_be_connected, Toast.LENGTH_LONG).show();
                    return;
                }

                if (bar.getIsLiked() != 0) {
                    bar.setIsLiked(0);
                    adaptLikesButton(like, dontLike, icon, bar);
                } else {
                    bar.setIsLiked(2);
                    adaptLikesButton(like, dontLike, icon, bar);
                }
                if (filter) {
                    Intent intent = new Intent(getContext(), BarListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    (getContext()).startActivity(intent);
                }
                currentUser.child(barKey[0]).child("isLiked").setValue(bar.getIsLiked());
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

    public boolean checkIfGuest(String uId) {
        return uId.equals("guest");
    }

}
