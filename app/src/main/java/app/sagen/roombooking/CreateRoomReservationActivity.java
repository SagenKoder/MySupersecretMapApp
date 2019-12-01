package app.sagen.roombooking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;

import app.sagen.roombooking.adapter.RoomListAdapter;
import app.sagen.roombooking.data.Reservation;
import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.task.CreateReservationTask;

public class CreateRoomReservationActivity extends AppCompatActivity implements CreateReservationTask.CreateReservationCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<Room> rooms = getIntent().getParcelableArrayListExtra("rooms");
        final Date from = new Date(getIntent().getLongExtra("from", 0));
        final Date to = new Date(getIntent().getLongExtra("to", 0));

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new RoomListAdapter(this, rooms)); // todo change layout

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                final Room room = rooms.get(position);

                AlertDialog alertDialog = new AlertDialog.Builder(CreateRoomReservationActivity.this)
                        .setMessage("Vil du reservere rom " + rooms.get(position).getName() + "?")
                        .setTitle("Reservere rom")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // todo: reservere rom
                                Reservation reservation = new Reservation(room);
                                reservation.setFrom(from);
                                reservation.setTo(to);
                                reservation.setDurationInSeconds((int)((to.getTime() - from.getTime()) / 1000));

                                CreateReservationTask createReservationTask = new CreateReservationTask(CreateRoomReservationActivity.this);
                                createReservationTask.execute(reservation);

                                Snackbar.make(view, "Reserverer rom....", Snackbar.LENGTH_LONG).show();

                            }
                        })
                        .setNegativeButton("Nei", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();

            }
        });
    }

    @Override
    public void reservationCreated(Reservation reservation) {
        Intent intent = new Intent();
        intent.putExtra(Reservation.class.getName(), reservation);
        setResult(RESULT_OK, intent);
        finish();
    }
}
