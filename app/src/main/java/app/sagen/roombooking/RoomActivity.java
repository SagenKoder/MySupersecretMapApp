package app.sagen.roombooking;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.sagen.roombooking.adapter.ReservationListAdapter;
import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.util.Utils;

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
