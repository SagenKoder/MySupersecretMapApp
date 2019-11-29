package app.sagen.roombooking.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import app.sagen.roombooking.R;
import app.sagen.roombooking.data.Reservation;

public class ReservationListAdapter extends BaseAdapter {

    private Activity context;
    private List<Reservation> reservations;

    private DateFormat dateFormat;
    private DateFormat timeFormat;

    public ReservationListAdapter(Activity context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;

        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public void addItem(Reservation reservation) {
        this.reservations.add(reservation);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reservations.size();
    }

    @Override
    public Reservation getItem(int position) {
        return reservations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reservations.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Reservation reservation = getItem(position);

        if (view == null) { // opprett ny om ikke gjennvunnet
            view = context.getLayoutInflater().inflate(R.layout.listitem_reservation, parent, false);
        }

        TextView dateField = view.findViewById(R.id.listitem_reservation_date);
        TextView timeFromField = view.findViewById(R.id.listitem_reservation_time_from);
        TextView timeToField = view.findViewById(R.id.listitem_reservation_time_to);

        dateField.setText(dateFormat.format(reservation.getFrom()));
        timeFromField.setText(timeFormat.format(reservation.getFrom()));
        timeToField.setText(timeFormat.format(reservation.getTo()));

        return view;
    }
}
