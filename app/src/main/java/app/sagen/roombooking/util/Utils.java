package app.sagen.roombooking.util;

import android.text.format.DateUtils;
import android.util.Log;

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

import app.sagen.roombooking.data.Building;
import app.sagen.roombooking.data.Reservation;
import app.sagen.roombooking.data.Room;

public class Utils {

    private static final String TAG = "Utils";
    
    public static final int CREATE_ROOM_REQUEST_CODE = 20;
    public static final int CREATE_RESERVATION_REQUEST_CODE = 30;
    public static final int CREATE_ROOM_RESERVATION_REQUEST_CODE = 40;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static Date parseJsonDate(String jsonDate) {
        try {
            return sdf.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatJsonDate(Date stringDate) {
        return sdf.format(stringDate);
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
        for (Reservation reservation : room.getReservations()) {
            if (DateUtils.isToday(reservation.getFrom().getTime())) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public static void fixParcelableReferences(Building building) {
        for (Room room : building.getRooms()) {
            room.setBuilding(building);
            fixParcelableReferences(room);
        }
    }

    public static void fixParcelableReferences(Room room) {
        for (Reservation reservation : room.getReservations()) {
            reservation.setRoom(room);
        }
    }

    public static String formatStringForUrl(String string) {
        return string.replaceAll("\\s", "%20");
    }

    public static int compareTime(int hour, int minute, int compHour, int compMinute) {
        int resHour = Integer.compare(hour, compHour);
        if (resHour != 0) return resHour;

        return Integer.compare(minute, compMinute);
    }

    public static ArrayList<Room> allAvailableRooms(Building building, Date from, Date to) {

        ArrayList<Room> rooms = new ArrayList<>();

        Log.e(TAG, "allAvailableRooms: Rooms=" + rooms + " From=" + from + " To=" + to);

        long from2 = from.getTime();
        long to2 = to.getTime();

        roomLoop:
        for(Room  room : building.getRooms()) {
            Log.e(TAG, "allAvailableRooms: ");
            fixParcelableReferences(room);
            Log.e(TAG, "allAvailableRooms:     Sjekker rom " + room.getName());
            for(Reservation reservation : room.getReservations()) {
                Log.e(TAG, "allAvailableRooms:         Sjekker reservasjon " + reservation.toString());
                long from1 = reservation.getFrom().getTime();
                long to1 = reservation.getTo().getTime();

                Log.e(TAG, "allAvailableRooms:                 StartA\t" + from1);
                Log.e(TAG, "allAvailableRooms:                 StartB\t" + from2);
                Log.e(TAG, "allAvailableRooms:                 EndA\t\t" + to1);
                Log.e(TAG, "allAvailableRooms:                 EndB\t\t" + to2);

                // source: https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
                if((from1 < to2) && (to1 > from2)) {
                    // room is reserved
                    Log.e(TAG, "allAvailableRooms:             Overlapping.....");
                    continue roomLoop;
                }
                Log.e(TAG, "allAvailableRooms:             Did not overlap.....");
            }
            Log.e(TAG, "allAvailableRooms:         Room did not overlap");
            rooms.add(room);
        }

        return rooms;
    }

}
