package app.sagen.mysupersecretmapapp.data;

import com.google.android.gms.maps.model.LatLng;

public class LatLngResult {
    private LatLng latLng;
    private LatLng northEast;
    private LatLng southWest;
    private String searchedAddress;

    public LatLngResult(LatLng latLng, LatLng northEast, LatLng southWest, String searchedAddress) {
        this.latLng = latLng;
        this.northEast = northEast;
        this.southWest = southWest;
        this.searchedAddress = searchedAddress;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public String getSearchedAddress() {
        return searchedAddress;
    }

    @Override
    public String toString() {
        return "LatLngResult{" +
                "latLng=" + latLng +
                ", northEast=" + northEast +
                ", southWest=" + southWest +
                ", searchedAddress='" + searchedAddress + '\'' +
                '}';
    }
}
