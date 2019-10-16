package iut.desvignes.mymeteo;

import com.google.android.gms.maps.model.LatLng;

public interface MapsView {
    void addMarker(String name, String icon, LatLng latLng);
}
