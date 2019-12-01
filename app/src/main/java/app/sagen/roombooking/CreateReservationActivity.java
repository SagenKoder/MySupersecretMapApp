package app.sagen.roombooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.sagen.roombooking.data.Building;
import app.sagen.roombooking.data.Reservation;
import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.util.DatePickerFragment;
import app.sagen.roombooking.util.TimePickerFragment;
import app.sagen.roombooking.util.Utils;

public class CreateReservationActivity extends AppCompatActivity {

    private static final String TAG = "CreateReservationActivi";

    private DateFormat dateFormat;
    private DateFormat timeFormat;

    private EditText selectDateText;
    private EditText selectTimeFromText;
    private EditText selectTimeToText;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private int selectedFromHour;
    private int selectedFromMinute;

    private int selectedToHour;
    private int selectedToMinute;

    DatePickerFragment.DatePickerCallback datePickerCallback = new DatePickerFragment.DatePickerCallback() {
        @Override
        public void onDatePicked(DatePicker view, int year, int month, int day) {
            // date picked
            Log.d(TAG, "onDatePicked: y=" + year + " m=" + month + " d=" + day);
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;
            updateTextFields();
        }
    };

    TimePickerFragment.TimePickerCallback timePickerCallback_from = new TimePickerFragment.TimePickerCallback() {
        @Override
        public void onTimePicked(TimePicker view, int hourOfDay, int minute) {
            // time from picked
            Log.d(TAG, "onTimePicked: from: hour=" + hourOfDay + " minute=" + minute);
            selectedFromHour = hourOfDay;
            selectedFromMinute = minute;
            updateTextFields();
        }
    };

    TimePickerFragment.TimePickerCallback timePickerCallback_to = new TimePickerFragment.TimePickerCallback() {
        @Override
        public void onTimePicked(TimePicker view, int hourOfDay, int minute) {
            // time to picked
            Log.d(TAG, "onTimePicked: to: hour=" + hourOfDay + " minute=" + minute);
            selectedToHour = hourOfDay;
            selectedToMinute = minute;
            updateTextFields();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Building building = getIntent().getParcelableExtra(Building.class.getName());
        if (building == null) {
            throw new RuntimeException("Could not get building from intent!");
        }
        Utils.fixParcelableReferences(building);

        setTitle("Reserver rom i " + building.getName());

        dateFormat = android.text.format.DateFormat.getDateFormat(this);
        timeFormat = android.text.format.DateFormat.getTimeFormat(this);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1); // 1 time frem i tid

        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedFromHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedFromMinute = calendar.get(Calendar.MINUTE);

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.add(Calendar.MINUTE, 30);

        selectedToHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedToMinute = calendar.get(Calendar.MINUTE);

        Button selectDateButton = findViewById(R.id.create_reservation_show_datepicker);
        Button selectTimeFromButton = findViewById(R.id.create_reservation_show_timefrompicker);
        Button selectTimeToButton = findViewById(R.id.create_reservation_show_timetopicker);

        selectDateText = findViewById(R.id.create_reservation_selected_date);
        selectTimeFromText = findViewById(R.id.create_reservation_selected_time_from);
        selectTimeToText = findViewById(R.id.create_reservation_selected_time_to);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DialogFragment datePickerFragment = new DatePickerFragment(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        datePickerCallback
                );
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        selectTimeFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DialogFragment timePickerFragment = new TimePickerFragment(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        timePickerCallback_from
                );
                timePickerFragment.show(getSupportFragmentManager(), "timePickerFrom");
            }
        });

        selectTimeToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DialogFragment timePickerFragment = new TimePickerFragment(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        timePickerCallback_to
                );
                timePickerFragment.show(getSupportFragmentManager(), "timePickerTo");
            }
        });

        ExtendedFloatingActionButton fab = findViewById(R.id.fab_create_reservation);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()) {
                    // todo: find all available rooms

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    calendar.set(Calendar.HOUR_OF_DAY, selectedFromHour);
                    calendar.set(Calendar.MINUTE, selectedFromMinute);
                    calendar.set(Calendar.SECOND, 0);

                    Date from = calendar.getTime();

                    calendar.set(Calendar.HOUR_OF_DAY, selectedToHour);
                    calendar.set(Calendar.MINUTE, selectedToMinute);

                    Date to = calendar.getTime();

                    ArrayList<Room> rooms = Utils.allAvailableRooms(building, from, to);

                    if(rooms.size() == 0) {

                        AlertDialog alertDialog = new AlertDialog.Builder(CreateReservationActivity.this)
                                .setMessage("Ingen ledige rom i denne perioden!")
                                .setTitle("Ingen ledige rom")
                                .setNeutralButton("Ok", null)
                                .setCancelable(true)
                                .create();
                        alertDialog.show();

                        return;

                    }

                    Intent intent = new Intent(CreateReservationActivity.this, CreateRoomReservationActivity.class);
                    intent.putExtra("from", from.getTime());
                    intent.putExtra("to", to.getTime());
                    intent.putParcelableArrayListExtra("rooms", rooms);
                    startActivityForResult(intent, Utils.CREATE_ROOM_RESERVATION_REQUEST_CODE);

                }
            }
        });

        updateTextFields();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Utils.CREATE_ROOM_RESERVATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Reservation reservation = data.getParcelableExtra(Reservation.class.getName());
                if(reservation != null) {
                    setResult(Utils.CREATE_ROOM_RESERVATION_REQUEST_CODE, data);
                    finish(); // return after reservation
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextFields() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedFromHour);
        calendar.set(Calendar.MINUTE, selectedFromMinute);
        calendar.set(Calendar.SECOND, 0);

        selectDateText.setText(dateFormat.format(calendar.getTime()));
        selectTimeFromText.setText(timeFormat.format(calendar.getTime()));

        calendar.set(Calendar.HOUR_OF_DAY, selectedToHour);
        calendar.set(Calendar.MINUTE, selectedToMinute);

        selectTimeToText.setText(timeFormat.format(calendar.getTime()));
    }

    private boolean validateFields() {
        int comp = Utils.compareTime(selectedFromHour, selectedFromMinute, selectedToHour, selectedToMinute);
        if(comp > 0) {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Starttid må være før sluttid!")
                    .setTitle("Feil input!")
                    .setNeutralButton("Ok", null)
                    .setCancelable(true)
                    .create();
            alertDialog.show();

            return false;

        }

        return true;
    }

}
