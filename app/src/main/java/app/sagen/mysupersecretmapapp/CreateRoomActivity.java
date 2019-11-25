package app.sagen.mysupersecretmapapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.data.Reservation;
import app.sagen.mysupersecretmapapp.data.Room;
import app.sagen.mysupersecretmapapp.task.CreateBuildingTask;
import app.sagen.mysupersecretmapapp.task.CreateRoomTask;
import app.sagen.mysupersecretmapapp.util.Utils;

public class CreateRoomActivity extends AppCompatActivity implements CreateRoomTask.CreateRoomCallback {

    Building building;

    ExtendedFloatingActionButton fabCreate;
    EditText nameField;
    EditText descField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        building = getIntent().getParcelableExtra(Building.class.getName());
        if(building == null) {
            throw new RuntimeException("Could not get building from intent!");
        }
        Utils.fixParcelableReferences(building);

        setTitle("Nytt rom i " + building.getName());

        fabCreate = findViewById(R.id.fab_create_room);
        nameField = findViewById(R.id.create_room_name);
        descField = findViewById(R.id.create_room_desc);

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameField.getText().toString().trim().isEmpty() || nameField.getText().toString().trim().isEmpty()) {
                    Snackbar.make(view, "Du må skrive et navn og beskrivelse", Snackbar.LENGTH_LONG).show();
                } else {
                    Room room = new Room();
                    room.setName(nameField.getText().toString().trim());
                    room.setDescription(descField.getText().toString().trim());
                    room.setBuilding(building);
                    room.setReservations(new ArrayList<Reservation>());

                    CreateRoomTask createRoomTask = new CreateRoomTask(CreateRoomActivity.this);
                    createRoomTask.execute(room);

                    fabCreate.setClickable(false);
                    Snackbar.make(view, "Oppretter rom....", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void buildingCreated(Room room) {
        Snackbar.make(fabCreate.getRootView(), "Rom opprettet", Snackbar.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.putExtra(Room.class.getName(), room);
        setResult(RESULT_OK, intent);
        finish();
    }
}
