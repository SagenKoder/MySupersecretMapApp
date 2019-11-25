package app.sagen.mysupersecretmapapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListView;

import app.sagen.mysupersecretmapapp.adapter.RoomListAdapter;
import app.sagen.mysupersecretmapapp.data.Building;

public class BuildingActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Building building = getIntent().getParcelableExtra(Building.class.getName());
        if(building == null) {
            throw new RuntimeException("Could not get building from intent!");
        }
        setTitle(building.getName());

        listView = findViewById(R.id.list_view);
        listView.setAdapter(new RoomListAdapter(this, building.getRooms()));

    }

}
