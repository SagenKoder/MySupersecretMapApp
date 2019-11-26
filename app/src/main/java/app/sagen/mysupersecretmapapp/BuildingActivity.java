package app.sagen.mysupersecretmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import app.sagen.mysupersecretmapapp.adapter.RoomListAdapter;
import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.data.Room;
import app.sagen.mysupersecretmapapp.util.Utils;

public class BuildingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "BuildingActivity";

    Building building;
    ListView listView;
    RoomListAdapter roomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        building = getIntent().getParcelableExtra(Building.class.getName());
        if(building == null) {
            throw new RuntimeException("Could not get building from intent!");
        }
        Utils.fixParcelableReferences(building);

        setTitle(building.getName());

        listView = findViewById(R.id.list_view);
        ExtendedFloatingActionButton fabCreate = findViewById(R.id.fab_create_reservation);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(BuildingActivity.this, CreateReservationActivity.class);
                intent.putExtra(Building.class.getName(), building);
                startActivityForResult(intent, Utils.CREATE_RESERVATION_REQUEST_CODE);
            }
        });

        roomListAdapter = new RoomListAdapter(this, building.getRooms());
        listView.setAdapter(roomListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.building_navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.building_menu_addroom) {
            Intent intent = new Intent(this, CreateRoomActivity.class);
            intent.putExtra(Building.class.getName(), building);
            startActivityForResult(intent, 20);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Utils.CREATE_ROOM_REQUEST_CODE) {
            if(resultCode == RESULT_OK && data != null) {
                Room room = data.getParcelableExtra(Room.class.getName());
                if(room != null) {
                    Utils.fixParcelableReferences(room);
                    room.setBuilding(building);
                    roomListAdapter.addItem(room);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Room room = roomListAdapter.getItem(position);

        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra(Room.class.getName(), room);
        startActivity(intent);
    }
}
