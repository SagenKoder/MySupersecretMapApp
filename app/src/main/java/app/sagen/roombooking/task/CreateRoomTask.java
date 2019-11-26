package app.sagen.roombooking.task;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.util.Utils;

public class CreateRoomTask extends AsyncTask<Room, Void, Room> {

    public interface CreateRoomCallback {
        void buildingCreated(Room room);
    }

    private static final String API_URI = "http://student.cs.hioa.no/~s326194/createRoom.php?name=%NAME%&desc=%DESC%&buildingId=%BUILDINGID%";

    private static final String TAG = "CreateRoomTask";

    private CreateRoomCallback createRoomCallback;

    public CreateRoomTask(CreateRoomCallback createRoomCallback) {
        this.createRoomCallback = createRoomCallback;
    }

    @Override
    protected Room doInBackground(Room... rooms) {
        Room room = rooms[0];

        try {
            URL url = new URL(API_URI
                    .replace("%NAME%", Utils.formatStringForUrl(room.getName()))
                    .replace("%DESC%", Utils.formatStringForUrl(room.getDescription()))
                    .replace("%BUILDINGID%", String.valueOf(room.getBuilding().getId())));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to contact api! Got HTTP status " + responseCode + " from server!\nApi: " + url.toString());
            }

            String data = Utils.readStringFromStream(conn.getInputStream());

            Log.e(TAG, "doInBackground: data = " + data);

            try {
                room.setId(Integer.parseInt(data));
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not parse id from data: " + data);
            }

            conn.disconnect();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Error while creating room!", e);
        }

        return room;
    }

    @Override
    protected void onPostExecute(Room room) {
        createRoomCallback.buildingCreated(room);
    }
}
