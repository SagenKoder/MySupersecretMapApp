package app.sagen.roombooking.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerCallback {
        void onDatePicked(DatePicker view, int year, int month, int day);
    }

    private DatePickerCallback datePickerCallback;

    private int initialYear;
    private int initialMonth;
    private int initialDay;

    public DatePickerFragment(int initialYear, int initialMonth, int initialDay, DatePickerCallback datePickerCallback) {
        this.initialYear = initialYear;
        this.initialMonth = initialMonth;
        this.initialDay = initialDay;
        this.datePickerCallback = datePickerCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, initialYear, initialMonth, initialDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        datePickerCallback.onDatePicked(view, year, month, dayOfMonth);
    }

    /* todo:
    DialogFragment newFragment = new TimePickerFragment();
    newFragment.show(getSupportFragmentManager(), "timePicker");
     */
}
