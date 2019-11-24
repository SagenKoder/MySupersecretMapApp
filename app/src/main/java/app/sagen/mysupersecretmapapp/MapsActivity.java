package app.sagen.mysupersecretmapapp;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.data.LatLngResult;
import app.sagen.mysupersecretmapapp.task.FetchDataTask;
import app.sagen.mysupersecretmapapp.task.LatLngFromAddressTask;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        FetchDataTask.FetchRoomTaskCallback,
        LatLngFromAddressTask.LatLngFromAddressCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;

    ExtendedFloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    LinearLayout fabLayout1;
    LinearLayout fabLayout2;
    LinearLayout fabLayout3;
    View fabBackground;

    private boolean fabExtended = false;

    private Map<Building, Marker> markers = new HashMap<>();

    Marker selectedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        fabLayout3 = findViewById(R.id.fabLayout3);
        fabBackground = findViewById(R.id.fabBackground);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedMarker != null) { // secondary mode
                    fab.setText(getString(R.string.opprett_nytt_rom));
                    fab.setIcon(getDrawable(R.drawable.ic_add_circle_outline_white_24dp));

                    selectedMarker.remove();
                    selectedMarker = null;
                } else {
                    if (!fabExtended) showMenu();
                    else closeMenu();
                }
            }
        });

        fabBackground.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                if(lastLocation != null && googleMap != null) {

                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    setCreateBuildingMarker(latLng, null);

                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(bottomDialogView);
                bottomSheetDialog.show();

                final EditText searchForAddress = bottomDialogView.findViewById(R.id.search_for_address_field);
                Button searchForAddressButton = bottomDialogView.findViewById(R.id.search_for_address_button);

                searchForAddressButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(searchForAddress.getText().toString().trim().equals("")) {
                            return; // do nothing
                        }

                        bottomSheetDialog.dismiss();

                        LatLngFromAddressTask latLngFromAddressTask = new LatLngFromAddressTask(
                                getString(R.string.google_maps_key),
                                MapsActivity.this);
                        latLngFromAddressTask.execute(searchForAddress.getText().toString());
                    }
                });

                searchForAddressButton.setClickable(true);

                closeMenu();
                //fab.setVisibility(View.GONE);
                //fab.setClickable(false);
            }
        });

        // fetch data
        FetchDataTask fetchDataTask = new FetchDataTask( this);
        fetchDataTask.execute();
    }

    private void setCreateBuildingMarker(LatLng latLng, @Nullable LatLngBounds bounds) {

        closeMenu();

        if(selectedMarker != null) {
            selectedMarker.remove();
            selectedMarker = null;
        }

        selectedMarker = googleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(latLng)
                .title("Legg til bygg her")
                .draggable(true)
                .snippet("Flytt meg og trykk 'Ferdig'"));

        selectedMarker.showInfoWindow();

        fab.setText("Ferdig");
        fab.setIcon(getDrawable(R.drawable.ic_create_white_24dp));

        GoogleMap.CancelableCallback cancelableCallback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setMessage("Trykk og hold markøren for å flytte den rundt. Klikk 'Ferdig' når du har den der du vil.")
                        .setTitle("Flytte markøren")
                        .setNeutralButton("Ok", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            }

            @Override
            public void onCancel() {
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setMessage("Trykk og hold markøren for å flytte den rundt. Klikk 'Ferdig' når du har den der du vil.")
                        .setTitle("Flytte markøren")
                        .setNeutralButton("Ok", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            }
        };

        if(bounds == null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), cancelableCallback);
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), cancelableCallback);
        }

        //fab.setVisibility(View.GONE);
        //fab.setClickable(false);

    }

    private void showMenu() {
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        fabExtended = true;

        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);

        fabBackground.setVisibility(View.VISIBLE);

        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));

        AutoTransition autoTransition = new AutoTransition();
        TransitionManager.go(new Scene((CoordinatorLayout) fab.getParent()), autoTransition);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        fab.setLayoutParams(layoutParams);
    }

    private void closeMenu() {
        fabExtended = false;

        fabBackground.setVisibility(View.GONE);

        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener(){
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) {
                if(!fabExtended) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        fab.extend();

        AutoTransition autoTransition = new AutoTransition();
        TransitionManager.go(new Scene((CoordinatorLayout) fab.getParent()), autoTransition);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fab.setLayoutParams(layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    public void handleNewLocation (Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(lastLocation == null) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 2000, null);
        this.lastLocation = location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.9139, 10.7522), 21.0f));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        googleMap.setMyLocationEnabled(true);
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // handleNewLocation(location);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void fetchedDataList(List<Building> buildings) {
        googleMap.clear();
        if(lastLocation != null) {
            handleNewLocation(lastLocation);
        }

        for(Building building : buildings) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(building.getLatLng())
                    .title(building.getName())
                    .snippet(building.getLatLng().toString());
            Marker marker = googleMap.addMarker(markerOptions);

            markers.put(building, marker);
        }

        Log.d(TAG, "fetchedDataList: " + markers.keySet());
    }

    @Override
    public void fetchedLatLngFromAddress(LatLngResult result) {
        Log.d(TAG, "fetchedLatLngFromAddress: Fetched LatLng data from address: \nData: " + result);

        LatLngBounds latLngBounds = new LatLngBounds(result.getSouthWest(), result.getNorthEast());
        setCreateBuildingMarker(result.getLatLng(), latLngBounds);
    }

    @Override
    public void fetchLatLngFromAddressFailed() {
        Log.e(TAG, "fetchLatLngFromAddressFailed: Failed to get geolocation from address");
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }
    @Override public boolean onMarkerClick(Marker marker) {
        return false;
    }
    @Override public void onConnectionSuspended(int i) {}
    @Override public void onMarkerDragStart(Marker marker) { }
    @Override public void onMarkerDrag(Marker marker) { }
    @Override public void onMarkerDragEnd(Marker marker) { }
}
