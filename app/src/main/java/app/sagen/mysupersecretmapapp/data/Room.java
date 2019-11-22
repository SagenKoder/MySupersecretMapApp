package app.sagen.mysupersecretmapapp.data;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {

    private int id;
    private String name;
    private String description;
    private float geolat;
    private float geolng;

    public Room() {
        id = -1;
    }

    public Room(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        description = jsonObject.getString("description");
        geolat = (float) jsonObject.getDouble("geolat");
        geolng = (float) jsonObject.getDouble("geolng");
    }

    public Room(String name, String description, float geolat, float geolng) {
        this(-1, name, description, geolat, geolng);
    }

    public Room(int id, String name, String description, float geolat, float geolng) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
