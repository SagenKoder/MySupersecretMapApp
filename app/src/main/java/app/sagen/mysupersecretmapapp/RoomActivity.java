package app.sagen.mysupersecretmapapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListView;

import app.sagen.mysupersecretmapapp.adapter.ReservationListAdapter;
import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.data.Room;
import app.sagen.mysupersecretmapapp.util.Utils;

public class RoomActivity extends AppCompatActivity {

    Room room;
    ListView listView;
    ReservationListAdapter reservationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        room = getIntent().getParcelableExtra(Room.class.getName());
        if(room == null) {
            throw new RuntimeException("Could not get room from intent!");
        }
        Utils.fixParcelableReferences(room);

        setTitle("Reservasjoner på rom " + room.getName() + " idag");

        listView = findViewById(R.id.list_view);

        reservationListAdapter = new ReservationListAdapter(this, Utils.getAllReservationsToday(room));
        listView.setAdapter(reservationListAdapter);
    }

}
