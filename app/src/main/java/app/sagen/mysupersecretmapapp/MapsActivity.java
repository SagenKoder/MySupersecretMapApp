package app.sagen.mysupersecretmapapp;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.Animator;
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
import app.sagen.mysupersecretmapapp.task.FetchDataTask;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        FetchDataTask.FetchRoomTaskCallback,
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

        FetchDataTask fetchDataTask = new FetchDataTask("http://student.cs.hioa.no/~s326194/showBuildings.php", this);
        fetchDataTask.execute();

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
                if(!fabExtended) showMenu();
                else closeMenu();
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

                    closeMenu();

                    if(selectedMarker != null) {
                        selectedMarker.remove();
                        selectedMarker = null;
                    }

                    selectedMarker = googleMap.addMarker(new MarkerOptions()
                            .draggable(true)
                            .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                            .title("Legg til bygg"));

                    fab.setVisibility(View.GONE);
                    fab.setClickable(false);
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(bottomDialogView);
                bottomSheetDialog.show();

                closeMenu();
                fab.setVisibility(View.GONE);
                fab.setClickable(false);
            }
        });
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
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
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

        Log.e(TAG, "fetchedDataList: " + markers.keySet());
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }
    @Override public boolean onMarkerClick(Marker marker) {
        return false;
    }
    @Override public void onMarkerDragStart(Marker marker) { }
    @Override public void onMarkerDrag(Marker marker) { }
    @Override public void onMarkerDragEnd(Marker marker) { }
    @Override public void onConnectionSuspended(int i) {}
}
