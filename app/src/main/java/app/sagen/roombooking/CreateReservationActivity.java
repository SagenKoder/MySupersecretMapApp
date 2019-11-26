package app.sagen.roombooking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import app.sagen.roombooking.data.Building;
import app.sagen.roombooking.util.DatePickerFragment;
import app.sagen.roombooking.util.TimePickerFragment;
import app.sagen.roombooking.util.Utils;

public class CreateReservationActivity extends AppCompatActivity {

    private static final String TAG = "CreateReservationActivi";
    
    private Building building;

    private DateFormat dateFormat;
    private DateFormat timeFormat;

    private Button selectDateButton;
    private Button selectTimeFromButton;
    private Button selectTimeToButton;

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
            Log.e(TAG, "onDatePicked: y=" + year + " m=" + month + " d=" + day);
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
            Log.e(TAG, "onTimePicked: from: hour=" + hourOfDay + " minute=" + minute);
            validateAndSetStartTime(hourOfDay, minute);
        }
    };

    TimePickerFragment.TimePickerCallback timePickerCallback_to = new TimePickerFragment.TimePickerCallback() {
        @Override
        public void onTimePicked(TimePicker view, int hourOfDay, int minute) {
            // time to picked
            Log.e(TAG, "onTimePicked: to: hour=" + hourOfDay + " minute=" + minute);
            validateAndSetEndTime(hourOfDay, minute);
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

        selectDateButton = findViewById(R.id.create_reservation_show_datepicker);
        selectTimeFromButton = findViewById(R.id.create_reservation_show_timefrompicker);
        selectTimeToButton = findViewById(R.id.create_reservation_show_timetopicker);

        selectDateText = findViewById(R.id.create_reservation_selected_date);
        selectTimeFromText = findViewById(R.id.create_reservation_selected_time_from);
        selectTimeToText = findViewById(R.id.create_reservation_selected_time_to);

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
                        timePickerCallback_to
                );
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        updateTextFields();
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

    private void validateAndSetStartTime(int hour, int minute) {
        if(Utils.compareTime(hour, minute, 23, 0) > 0) { // if after 23:00, set to 23:00
            hour = 23;
            minute = 0;
        }
        selectedFromHour = hour;
        selectedToMinute = minute;
        validateAndSetEndTime(selectedToHour, selectedToMinute); // update end time
    }

    private void validateAndSetEndTime(int hour, int minute) {
        if(Utils.compareTime(hour, minute, selectedFromHour, selectedToMinute) < 0) { // is before start time - set to an hour after start time
            selectedToHour = selectedFromHour;
            selectedToMinute = selectedFromMinute + 59;
        }
        updateTextFields();
    }

}
