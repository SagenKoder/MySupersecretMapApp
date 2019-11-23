package app.sagen.mysupersecretmapapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import app.sagen.mysupersecretmapapp.util.Util;

public class Reservation {

    private int id;
    private Room room;
    private Date from;
    private int durationInSeconds;
    private Date to;

    public Reservation(Room room) {
        this.room = room;
        this.id = -1;
    }

    public Reservation(Room room, JSONObject jsonObject) throws JSONException {

        this.room = room;

        id = jsonObject.getInt("id");
        from = Util.parseJsonDate(jsonObject.getString("datetime_from"));
        to = Util.parseJsonDate(jsonObject.getString("datetime_to"));
        durationInSeconds = jsonObject.getInt("durationInSeconds");

    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", from=" + from +
                ", durationInSeconds=" + durationInSeconds +
                ", to=" + to +
                '}';
    }
}
