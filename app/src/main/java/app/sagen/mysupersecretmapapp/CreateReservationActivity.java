package app.sagen.mysupersecretmapapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.util.Utils;

public class CreateReservationActivity extends AppCompatActivity {

    Building building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        building = getIntent().getParcelableExtra(Building.class.getName());
        if(building == null) {
            throw new RuntimeException("Could not get building from intent!");
        }
        Utils.fixParcelableReferences(building);

        setTitle("Reserver rom i " + building.getName());
    }

}
