package app.sagen.roombooking;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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

import app.sagen.roombooking.data.Building;
import app.sagen.roombooking.data.LatLngResult;
import app.sagen.roombooking.task.FetchDataTask;
import app.sagen.roombooking.task.LatLngFromAddressTask;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        FetchDataTask.FetchRoomTaskCallback,
        LatLngFromAddressTask.LatLngFromAddressCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener {

    private static final String TAG = "MainActivity";

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;

    private ExtendedFloatingActionButton fab;
    private LinearLayout fabLayout1;
    private LinearLayout fabLayout2;
    private LinearLayout fabLayout3;
    private View fabBackground;

    private boolean fabExtended = false;
    private boolean setSelectedMarkerMode = false;

    private Map<Building, Marker> markers = new HashMap<>();

    private Marker selectedLocationMarker = null;
    private Marker selectedBuildingMarker = null;

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
        FloatingActionButton fab1 = findViewById(R.id.fab1);
        FloatingActionButton fab2 = findViewById(R.id.fab2);
        FloatingActionButton fab3 = findViewById(R.id.fab3);
        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        fabLayout3 = findViewById(R.id.fabLayout3);
        fabBackground = findViewById(R.id.fabBackground);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBuildingMarker != null) { // manage building mode
                    fab.setText(getString(R.string.opprett_nytt_rom));
                    fab.setIcon(getDrawable(R.drawable.ic_add_circle_outline_white_24dp));

                    Building building = (Building) selectedBuildingMarker.getTag();

                    selectedBuildingMarker = null;

                    Intent intent = new Intent(MainActivity.this, BuildingActivity.class);
                    intent.putExtra(Building.class.getName(), building);
                    startActivity(intent);

                } else if (selectedLocationMarker != null) { // manage new location mode
                    fab.setText(getString(R.string.opprett_nytt_rom));
                    fab.setIcon(getDrawable(R.drawable.ic_add_circle_outline_white_24dp));

                    final LatLng markerPosition = selectedLocationMarker.getPosition();

                    selectedLocationMarker.remove();
                    selectedLocationMarker = null;

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Vil du opprette ett nytt bygg her?")
                            .setTitle("Opprette nytt bygg")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(MainActivity.this, CreateBuildingActivity.class);
                                    intent.putExtra("app.dagen.mysupersecretmapapp.location.lat", markerPosition.latitude);
                                    intent.putExtra("app.dagen.mysupersecretmapapp.location.lng", markerPosition.longitude);
                                    startActivity(intent);

                                }
                            })
                            .setNegativeButton("Nei", null)
                            .setCancelable(true)
                            .create();
                    alertDialog.show();

                } else {
                    if (!fabExtended) showMenu();
                    else closeMenu();
                }
            }
        });

        fabBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Trykk på kartet for å plassere en markør der du vil legge til et bygg")
                        .setTitle("Sett markøren")
                        .setNeutralButton("Ok", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();

                fab.setVisibility(View.GONE);
                fab.setClickable(false);

                setSelectedMarkerMode = true;
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null && googleMap != null) {

                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    setCreateBuildingMarker(latLng, null);

                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(bottomDialogView);
                bottomSheetDialog.show();

                final EditText searchForAddress = bottomDialogView.findViewById(R.id.search_for_address_field);
                Button searchForAddressButton = bottomDialogView.findViewById(R.id.search_for_address_button);

                searchForAddressButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (searchForAddress.getText().toString().trim().equals("")) {
                            return; // do nothing
                        }

                        bottomSheetDialog.dismiss();

                        LatLngFromAddressTask latLngFromAddressTask = new LatLngFromAddressTask(
                                getString(R.string.google_maps_key),
                                MainActivity.this);
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
        FetchDataTask fetchDataTask = new FetchDataTask(this);
        fetchDataTask.execute();
    }

    private void setCreateBuildingMarker(LatLng latLng, @Nullable LatLngBounds bounds) {

        closeMenu();

        if (selectedLocationMarker != null) {
            selectedLocationMarker.remove();
            selectedLocationMarker = null;
        }

        selectedLocationMarker = googleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(latLng)
                .title("Legg til bygg her")
                .draggable(true)
                .snippet("Flytt meg og trykk 'Ferdig'"));

        selectedLocationMarker.showInfoWindow();

        fab.setText("Ferdig");
        fab.setIcon(getDrawable(R.drawable.ic_create_white_24dp));

        GoogleMap.CancelableCallback cancelableCallback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Trykk og hold markøren for å flytte den rundt. Klikk 'Ferdig' når du har den der du vil.")
                        .setTitle("Flytte markøren")
                        .setNeutralButton("Ok", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            }

            @Override
            public void onCancel() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Trykk og hold markøren for å flytte den rundt. Klikk 'Ferdig' når du har den der du vil.")
                        .setTitle("Flytte markøren")
                        .setNeutralButton("Ok", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            }
        };

        if (bounds == null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), cancelableCallback);
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
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!fabExtended) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        fab.extend();

        AutoTransition autoTransition = new AutoTransition();
        TransitionManager.go(new Scene((CoordinatorLayout) fab.getParent()), autoTransition);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fab.setLayoutParams(layoutParams);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (selectedBuildingMarker != null) {
            fab.setText(getString(R.string.opprett_nytt_rom));
            fab.setIcon(getDrawable(R.drawable.ic_add_circle_outline_white_24dp));
            selectedBuildingMarker = null;
        }

        if (setSelectedMarkerMode) {
            setSelectedMarkerMode = false;

            fab.setVisibility(View.VISIBLE);
            fab.setClickable(true);

            setCreateBuildingMarker(latLng, null);
        }
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

        FetchDataTask fetchDataTask = new FetchDataTask(this);
        fetchDataTask.execute();
    }

    public void handleNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (lastLocation == null)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 2000, null);
        this.lastLocation = location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.9139, 10.7522), 21.0f));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void fetchedDataList(List<Building> buildings) {
        googleMap.clear();
        if (lastLocation != null) {
            handleNewLocation(lastLocation);
        }

        for (Map.Entry<Building, Marker> e : markers.entrySet()) {
            e.getValue().remove();
        }
        markers.clear();

        for (Building building : buildings) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(building.getLatLng())
                    .title(building.getName())
                    .snippet(building.getRooms().size() + " rom");
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(building);
            markers.put(building, marker);
        }

        Log.d(TAG, "roomCreated: " + markers.keySet());
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
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTag() instanceof Building) {

            selectedBuildingMarker = marker;

            fab.setText("Vis detaljer");
            fab.setIcon(getDrawable(R.drawable.ic_arrow_drop_up_white_24dp));

        }

        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }
}
