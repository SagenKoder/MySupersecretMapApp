package app.sagen.roombooking.task;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import app.sagen.roombooking.data.Reservation;
import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.util.Utils;

public class CreateReservationTask extends AsyncTask<Reservation, Void, Reservation> {

    public interface CreateReservationCallback {
        void reservationCreated(Reservation reservation);
    }

    private static final String API_URI = "http://student.cs.hioa.no/~s326194/createReservation.php?roomId=%ROOM%&startTime=%TIME%&duration=%DURATION%";

    private static final String TAG = "CreateReservationTask";

    private CreateReservationCallback createReservationCallback;

    public CreateReservationTask(CreateReservationCallback createReservationCallback) {
        this.createReservationCallback = createReservationCallback;
    }

    @Override
    protected Reservation doInBackground(Reservation... reservations) {
        Reservation reservation = reservations[0];

        try {
            URL url = new URL(API_URI
                    .replace("%ROOM%", String.valueOf(reservation.getRoom().getId()))
                    .replace("%TIME%", Utils.formatStringForUrl(Utils.formatJsonDate(reservation.getFrom())))
                    .replace("%DURATION%", String.valueOf(reservation.getDurationInSeconds())));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to contact api! Got HTTP status " + responseCode + " from server!\nApi: " + url.toString());
            }

            String data = Utils.readStringFromStream(conn.getInputStream());

            Log.e(TAG, "doInBackground: data = " + data);

            try {
                reservation.setId(Integer.parseInt(data));
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not parse id from data: " + data);
            }

            conn.disconnect();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Error while creating reservation!", e);
        }

        return reservation;
    }

    @Override
    protected void onPostExecute(Reservation reservation) {
        createReservationCallback.reservationCreated(reservation);
    }
}
