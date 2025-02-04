package app.sagen.roombooking.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.sagen.roombooking.R;
import app.sagen.roombooking.data.Room;
import app.sagen.roombooking.util.Utils;

public class RoomListAdapter extends BaseAdapter {

    private Activity context;
    private List<Room> rooms;

    private int layout;

    public RoomListAdapter(Activity context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
        layout = R.layout.listitem_room;
    }

    public RoomListAdapter(Activity context, List<Room> rooms, int layout) {
        this.context = context;
        this.rooms = rooms;
        this.layout = layout;
    }

    public void addItem(Room room) {
        this.rooms.add(room);
        this.notifyDataSetChanged();
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
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) { // opprett ny om ikke gjennvunnet
            view = context.getLayoutInflater().inflate(R.layout.listitem_room, parent, false);
        }

        TextView roomName = view.findViewById(R.id.listitem_room_name);
        TextView roomDesc = view.findViewById(R.id.listitem_room_description);
        TextView roomBuilding = view.findViewById(R.id.listitem_room_building);
        TextView roomReservations = view.findViewById(R.id.listitem_room_reservations_today);

        Room room = getItem(position);

        if(roomName != null) roomName.setText(room.getName());
        if(roomDesc != null) roomDesc.setText(room.getDescription());
        if(roomBuilding != null && room.getBuilding() != null) roomBuilding.setText(room.getBuilding().getName());
        if(roomReservations != null) roomReservations.setText(String.valueOf(Utils.getAllReservationsToday(room).size()));

        return view;
    }
}
