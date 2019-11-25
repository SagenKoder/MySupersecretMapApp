package app.sagen.mysupersecretmapapp.task;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import app.sagen.mysupersecretmapapp.data.Building;

public class CreateBuildingTask extends AsyncTask<Building, Void, Void> {

    public interface CreateBuildingCallback {
        void buildingCreated();
    }

    private static final String API_URI = "http://student.cs.hioa.no/~s326194/createBuilding.php?name=%NAME%&geolat=%GEOLAT%&geolng=%GEOLNG%";

    private static final String TAG = "CreateBuildingTask";

    private CreateBuildingCallback createBuildingCallback;

    public CreateBuildingTask(CreateBuildingCallback createBuildingCallback) {
        this.createBuildingCallback = createBuildingCallback;
    }

    @Override
    protected Void doInBackground(Building... buildings) {

        for (Building building : buildings) {
            try {
                URL url = new URL(API_URI
                        .replace("%NAME%", building.getName().replaceAll("\\s", "%20"))
                        .replace("%GEOLAT%", String.format(Locale.US, "%.6f", building.getGeolat()))
                        .replace("%GEOLNG%", String.format(Locale.US, "%.6f", building.getGeolng())));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("Failed to contact api! Got HTTP status " + responseCode + " from server!\nApi: " + url.toString());
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error while loading buildings!", e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        createBuildingCallback.buildingCreated();
    }
}
