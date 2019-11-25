package app.sagen.mysupersecretmapapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import app.sagen.mysupersecretmapapp.util.Util;

public class Reservation implements Parcelable {

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

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

    protected Reservation(Parcel in) {
        id = in.readInt();
        room = in.readParcelable(Room.class.getClassLoader());
        durationInSeconds = in.readInt();
        from = new Date(in.readLong());
        to = new Date(in.readLong());
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(room, flags);
        dest.writeInt(durationInSeconds);
        dest.writeLong(from.getTime());
        dest.writeLong(to.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
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
