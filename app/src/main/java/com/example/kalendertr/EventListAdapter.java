package com.example.kalendertr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventListAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public EventListAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        }

        Event event = events.get(position);

        TextView titleTextView = convertView.findViewById(R.id.event_title_text_view);
        TextView dateTextView = convertView.findViewById(R.id.event_date_text_view);

        titleTextView.setText(event.getEventTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateTextView.setText(sdf.format(event.getEventDate()));

        return convertView;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }
}

