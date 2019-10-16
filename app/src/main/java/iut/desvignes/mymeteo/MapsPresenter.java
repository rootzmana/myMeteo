package iut.desvignes.mymeteo;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


public class MapsPresenter implements Serializable {
        private static final long serialVersionUID = 1L;

        private transient MapsView view;

    public void setView(MapsView view){
        this.view = view;
    }

    // Dessine tous les marqueurs pour toutes les villes dans la BD
    public void drawMarkers(String[] arrayName, String[] arrayIcon, double[] arrayLat, double[] arrayLng){
        for(int i = 0; i < arrayIcon.length; i++){
            view.addMarker(arrayName[i], arrayIcon[i], new LatLng(arrayLat[i], arrayLng[i]));
        }
    }
}
