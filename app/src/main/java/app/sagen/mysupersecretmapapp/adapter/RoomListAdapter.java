package app.sagen.mysupersecretmapapp.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import app.sagen.mysupersecretmapapp.data.Room;

public class RoomListAdapter extends BaseAdapter {

    private Activity context;
    private List<Room> rooms;

    public RoomListAdapter(Activity context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Room getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rooms.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            // todo: inflate view
        }

        return null;
    }
}
