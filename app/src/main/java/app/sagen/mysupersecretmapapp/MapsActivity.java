package app.sagen.mysupersecretmapapp;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions().position(new LatLng(59.946159, 	10.735050)).title("Hjem <3"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(59.954660, 	10.765170)).title("Gaute :'("));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(59.9139, 10.7522)));
        googleMap.setTrafficEnabled(true);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(59.946159, 	10.735050));
        circleOptions.fillColor(Color.argb(100, 255, 0, 0));
        circleOptions.radius(600);
        circleOptions.clickable(false);
        circleOptions.strokeColor(Color.TRANSPARENT);
        googleMap.addCircle(circleOptions);
    }
}
