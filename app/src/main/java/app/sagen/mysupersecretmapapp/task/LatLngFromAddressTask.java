package app.sagen.mysupersecretmapapp.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import app.sagen.mysupersecretmapapp.data.LatLngResult;
import app.sagen.mysupersecretmapapp.util.Util;

public class LatLngFromAddressTask extends AsyncTask<String, Void, LatLngResult> {

    public interface LatLngFromAddressCallback {
        void fetchedLatLngFromAddress(LatLngResult latLngResult);
        void fetchLatLngFromAddressFailed();
    }

    private static final String TAG = "LatLngFromAddressTask";

    private static final String GEO_LOCATION_API = "https://maps.googleapis.com/maps/api/geocode/json?address=%ADDRESS%&key=%KEY%";

    private String apiKey;
    private LatLngFromAddressCallback latLngFromAddressCallback;

    public LatLngFromAddressTask(String apiKey, LatLngFromAddressCallback latLngFromAddressCallback) {
        this.apiKey = apiKey;
        this.latLngFromAddressCallback = latLngFromAddressCallback;
    }

    @Override
    protected LatLngResult doInBackground(String... strings) {

        for(String string : strings) {

            try {

                URL url = new URL(GEO_LOCATION_API
                        .replace("%KEY%", apiKey)
                        .replace("%ADDRESS%", string.replaceAll("\\s", "%20")));

                Log.e(TAG, "doInBackground: URL: " + url);

                JSONObject jsonString = Util.readJsonObjectFrom(url);

                Log.e(TAG, "doInBackground: JsonString: " + jsonString);

                JSONObject jsonObject = jsonString;

                if(!jsonObject.getString("status").equals("OK")) {
                    throw new RuntimeException("Got a non-ok status from maps API!\nStatus: " + jsonObject.getString("status"));
                }

                JSONArray results = ((JSONArray) jsonObject.get("results"));

                JSONObject firstResult = results.getJSONObject(0);

                JSONObject geometry = firstResult.getJSONObject("geometry");


                JSONObject location = geometry.getJSONObject("location");
                JSONObject viewport = geometry.getJSONObject("viewport");

                JSONObject northeastJson = viewport.getJSONObject("northeast");
                JSONObject southwestJson = viewport.getJSONObject("southwest");

               LatLng result = new LatLng(
                       (float) location.getDouble("lat"),
                        (float) location.getDouble("lng")
                );

               LatLng northeast = new LatLng(
                       (float) northeastJson.getDouble("lat"),
                       (float) northeastJson.getDouble("lng")
               );

                LatLng southwest = new LatLng(
                        (float) southwestJson.getDouble("lat"),
                        (float) southwestJson.getDouble("lng")
                );


                return new LatLngResult(result, northeast, southwest, string);

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not get geolocation from address " + string, e);
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(LatLngResult result) {
        if(result == null) {
            latLngFromAddressCallback.fetchLatLngFromAddressFailed();
        } else {
            latLngFromAddressCallback.fetchedLatLngFromAddress(result);
        }
    }
}
