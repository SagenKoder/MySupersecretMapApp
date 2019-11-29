package app.sagen.roombooking;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import app.sagen.roombooking.data.Building;
import app.sagen.roombooking.task.CreateBuildingTask;

public class CreateBuildingActivity extends AppCompatActivity implements CreateBuildingTask.CreateBuildingCallback {

    private ExtendedFloatingActionButton fabCreate;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_building);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabCreate = findViewById(R.id.fab_create_building);
        nameField = findViewById(R.id.name_field);
        EditText locationFieldLat = findViewById(R.id.location_field_lat);
        EditText locationFieldLng = findViewById(R.id.location_field_lng);

        final float lat = (float) getIntent().getDoubleExtra("app.dagen.mysupersecretmapapp.location.lat", 0);
        final float lng = (float) getIntent().getDoubleExtra("app.dagen.mysupersecretmapapp.location.lng", 0);

        locationFieldLat.setText(String.format(Locale.US, "%.6f", lat));
        locationFieldLng.setText(String.format(Locale.US, "%.6f", lng));

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameField.getText().toString().trim().isEmpty()) {
                    Snackbar.make(view, "Du må skrive inn et navn på bygget", Snackbar.LENGTH_LONG).show();
                } else {
                    CreateBuildingTask createBuildingTask = new CreateBuildingTask(CreateBuildingActivity.this);
                    createBuildingTask.execute(new Building(nameField.getText().toString().trim(), lat, lng));

                    fabCreate.setClickable(false);
                    Snackbar.make(view, "Oppretter bygning....", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void buildingCreated() {
        Snackbar.make(fabCreate.getRootView(), "Bygg opprettet", Snackbar.LENGTH_LONG).show();

        finish();
    }
}
