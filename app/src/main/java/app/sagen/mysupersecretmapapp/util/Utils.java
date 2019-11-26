package app.sagen.mysupersecretmapapp.util;

import android.text.format.DateUtils;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.sagen.mysupersecretmapapp.data.Building;
import app.sagen.mysupersecretmapapp.data.Reservation;
import app.sagen.mysupersecretmapapp.data.Room;

public class Utils {

    public static final int CREATE_ROOM_REQUEST_CODE = 20;
    public static final int CREATE_RESERVATION_REQUEST_CODE = 30;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static Date parseJsonDate(String jsonDate) {
        try {
            return sdf.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readStringFromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    public static String readStringFrom(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to fetch json from api! Got HTTP status " + responseCode + " from server!\nApi: " + url.toString());
        }

        String data = readStringFromStream(conn.getInputStream());
        conn.disconnect();

        return data;
    }

    public static JSONObject readJsonObjectFrom(URL url) throws IOException, JSONException {
        return new JSONObject(readStringFrom(url));
    }

    public static JSONArray readJsonArrayFrom(URL url) throws IOException, JSONException {
        return new JSONArray(readStringFrom(url));
    }

    public static List<Reservation> getAllReservationsToday(Room room) {
        List<Reservation> reservations = new ArrayList<>();
        for(Reservation reservation : room.getReservations()) {
            if(DateUtils.isToday(reservation.getFrom().getTime())) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public static void fixParcelableReferences(List<Building> buildings) {
        for(Building building : buildings) {
            fixParcelableReferences(building);
        }
    }

    public static void fixParcelableReferences(Building building) {
        for(Room room : building.getRooms()) {
            room.setBuilding(building);
            fixParcelableReferences(room);
        }
    }

    public static void fixParcelableReferences(Room room) {
        for(Reservation reservation : room.getReservations()) {
            reservation.setRoom(room);
        }
    }

    public static String formatStringForUrl(String string) {
        return string.replaceAll("\\s", "%20");
    }

}
