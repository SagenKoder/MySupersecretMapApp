package app.sagen.mysupersecretmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.util.DatePickerFragment;
import app.sagen.mysupersecretmapapp.util.TimePickerFragment;
import app.sagen.mysupersecretmapapp.util.Utils;

public class CreateReservationActivity extends AppCompatActivity {

    private static final String TAG = "CreateReservationActivi";
    
    private Building building;

    private Button selectDateButton;
    private Button selectTimeFromButton;
    private Button selectTimeToButton;

    DatePickerFragment.DatePickerCallback datePickerCallback = new DatePickerFragment.DatePickerCallback() {
        @Override
        public void onDatePicked(DatePicker view, int year, int month, int day) {
            // date picked
            Log.e(TAG, "onDatePicked: y=" + year + " m=" + month + " d=" + day);
        }
    };

    TimePickerFragment.TimePickerCallback timePickerCallback_from = new TimePickerFragment.TimePickerCallback() {
        @Override
        public void onTimePicked(TimePicker view, int hourOfDay, int minute) {
            // time from picked
            Log.e(TAG, "onTimePicked: from: hour=" + hourOfDay + " minute=" + minute);
        }
    };

    TimePickerFragment.TimePickerCallback timePickerCallback_to = new TimePickerFragment.TimePickerCallback() {
        @Override
        public void onTimePicked(TimePicker view, int hourOfDay, int minute) {
            // time to picked
            Log.e(TAG, "onTimePicked: to: hour=" + hourOfDay + " minute=" + minute);
        }
    };

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

        selectDateButton = findViewById(R.id.create_reservation_show_datepicker);
        selectTimeFromButton = findViewById(R.id.create_reservation_show_timefrompicker);
        selectTimeToButton = findViewById(R.id.create_reservation_show_timetopicker);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
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
            @Override public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DialogFragment timePickerFragment = new TimePickerFragment(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        timePickerCallback_from
                );
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        selectTimeToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DialogFragment timePickerFragment = new TimePickerFragment(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        timePickerCallback_from
                );
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

}
