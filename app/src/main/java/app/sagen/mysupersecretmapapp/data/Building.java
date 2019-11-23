package app.sagen.mysupersecretmapapp.data;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Building {

    private int id;
    private String name;
    private float geolat;
    private float geolng;

    List<Room> rooms;

    public Building() {
        id = -1;
    }

    public Building(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        geolat = (float) jsonObject.getDouble("geolat");
        geolng = (float) jsonObject.getDouble("geolng");

        this.rooms = new ArrayList<>();

        JSONArray rooms = jsonObject.getJSONArray("rooms");
        for(int i = 0; i < rooms.length(); i++) {
            this.rooms.add(new Room(this, rooms.getJSONObject(i)));
        }
    }

    public Building(String name, float geolat, float geolng) {
        this(-1, name, geolat, geolng);
    }

    public Building(int id, String name, float geolat, float geolng) {
        this.id = id;
        this.name = name;
        this.geolat = geolat;
        this.geolng = geolng;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getGeolat() {
        return geolat;
    }

    public void setGeolat(float geolat) {
        this.geolat = geolat;
    }

    public float getGeolng() {
        return geolng;
    }

    public void setGeolng(float geolng) {
        this.geolng = geolng;
    }

    public void setLatLng(LatLng latLng) {
        this.geolat = (float)latLng.latitude;
        this.geolng = (float)latLng.longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(geolat, geolng);
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", geolat=" + geolat +
                ", geolng=" + geolng +
                ", rooms=" + rooms +
                '}';
    }
}
