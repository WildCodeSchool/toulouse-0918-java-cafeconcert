package fr.wildcodeschool.cafeconcert;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter{

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    //on récupère les infos du InfoWindowData, qui est initialisé dans le onMapCreate, avec le bar indiqué
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_adapter, null);
        TextView name = view.findViewById(R.id.barTitlePopup);
        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        //le bar qui est étudié dans cette infoWindow
        Bar studyBar = infoWindowData.getBar();
        name.setText(studyBar.getBarName());

        //TODO : Rajouter les autres infos des bars. Là j'ai juste mis le texte pour vérifier le fonctionnement

        return view;
    }
}



