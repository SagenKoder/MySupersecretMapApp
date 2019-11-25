package app.sagen.mysupersecretmapapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import app.sagen.mysupersecretmapapp.adapter.RoomListAdapter;
import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.util.Util;

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
        Util.fixParcelableReferences(building);

        setTitle(building.getName());

        listView = findViewById(R.id.list_view);
        listView.setAdapter(new RoomListAdapter(this, building.getRooms()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.building_navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.building_menu_addroom) {

            // todo: create new room

        }
        return super.onOptionsItemSelected(item);
    }
}
