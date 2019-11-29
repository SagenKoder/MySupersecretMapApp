package app.sagen.roombooking.util;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public interface TimePickerCallback {
        void onTimePicked(TimePicker view, int hourOfDay, int minute);
    }

    private TimePickerCallback timePickerCallback;

    private int initialHour;
    private int initialMinute;

    public TimePickerFragment(int initialHour, int initialMinute, TimePickerCallback timePickerCallback) {
        this.timePickerCallback = timePickerCallback;
        this.initialHour = initialHour;
        this.initialMinute = initialMinute;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, initialHour, initialMinute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timePickerCallback.onTimePicked(view, hourOfDay, minute);
    }
}
