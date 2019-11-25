package app.sagen.mysupersecretmapapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Building implements Parcelable {

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

    private int id;
    private String name;
    private float geolat;
    private float geolng;

    private List<Room> rooms;

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
        for (int i = 0; i < rooms.length(); i++) {
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

    protected Building(Parcel in) {
        id = in.readInt();
        name = in.readString();
        geolat = in.readFloat();
        geolng = in.readFloat();
        rooms = in.createTypedArrayList(Room.CREATOR);
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
        this.geolat = (float) latLng.latitude;
        this.geolng = (float) latLng.longitude;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public LatLng getLatLng() {
        return new LatLng(geolat, geolng);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(geolat);
        dest.writeFloat(geolng);
        dest.writeTypedList(rooms);
    }

    @Override
    public int describeContents() {
        return 0;
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
