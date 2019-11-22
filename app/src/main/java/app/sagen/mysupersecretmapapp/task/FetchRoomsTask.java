package app.sagen.mysupersecretmapapp.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.sagen.mysupersecretmapapp.data.Room;

public class FetchRoomsTask extends AsyncTask<Void, Void, List<Room>> {

    public interface FetchRoomTaskCallback {
        void fetchedRoomList(List<Room> room);
    }

    private static final String TAG = "FetchRoomsTask";

    private URL apiUri;
    private FetchRoomTaskCallback fetchRoomTaskCallback;

    public FetchRoomsTask(String apiUri, FetchRoomTaskCallback fetchRoomTaskCallback) {
        try {
            this.apiUri = new URL(apiUri);
            this.fetchRoomTaskCallback = fetchRoomTaskCallback;
        } catch (MalformedURLException e) {
            Log.e(TAG, "FetchRoomsTask: Could not parse api url!", e);
        }
    }

    @Override
    protected List<Room> doInBackground(Void... voids) {

        List<Room> rooms = new ArrayList<>();

        try {

            HttpURLConnection conn = (HttpURLConnection) apiUri.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if(responseCode != 200) {
                throw new RuntimeException("Failed to fetch rooms from api! Got HTTP status " + responseCode + " from server!");
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());

            conn.disconnect();

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                rooms.add(new Room(jsonObject));
            }

        } catch(Exception e) {
            Log.e(TAG, "doInBackground: Error while loading rooms!", e);
        }

        return rooms;
    }

    @Override
    protected void onPostExecute(List<Room> rooms) {
        fetchRoomTaskCallback.fetchedRoomList(rooms);
    }
}
