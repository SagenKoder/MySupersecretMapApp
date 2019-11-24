package app.sagen.mysupersecretmapapp.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static Date parseJsonDate(String jsonDate) {
        try {
            return sdf.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    private static String readStringFrom(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if(responseCode != 200) {
            throw new RuntimeException("Failed to fetch json from api! Got HTTP status " + responseCode + " from server!\nApi: " + url.toString());
        }

        return readFromStream(conn.getInputStream());
    }

    public static JSONObject readJsonObjectFrom(URL url) throws IOException, JSONException {
        return new JSONObject(readStringFrom(url));
    }

    public static JSONArray readJsonArrayFrom(URL url) throws IOException, JSONException {
        return new JSONArray(readStringFrom(url));
    }

}
