package com.example.kalendertr;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> dates;
    private List<Event> events;
    private String[] monthNames = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
    private int selectedDay = -1;

    public CalendarAdapter(Context context) {
        this.context = context;
        dates = new ArrayList<>();
        events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < startDayOfWeek; i++) {
            dates.add(0);
        }

        for (int i = 1; i <= daysInMonth; i++) {
            dates.add(i);
        }
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(8, 8, 8, 8);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView = (TextView) convertView;
        }

        int dayOfMonth = dates.get(position);
        if (dayOfMonth != 0) {
            textView.setText(String.valueOf(dayOfMonth));

            // Setze Hintergrundfarbe für ausgewählten Tag
            if (dayOfMonth == selectedDay) {
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.gruen));
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            textView.setText("");
            textView.setBackgroundColor(Color.TRANSPARENT);
        }

        return textView;
    }

    public void updateCalendar(int year, int month) {
        dates.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < startDayOfWeek; i++) {
            dates.add(0);
        }

        for (int i = 1; i <= daysInMonth; i++) {
            dates.add(i);
        }

        notifyDataSetChanged();
    }

    public String getMonthYearString(int year, int month) {
        return monthNames[month] + " " + year;
    }

    // Methode, um das ausgewählte Datum zu setzen
    public void setSelectedDay(int day) {
        selectedDay = day;
        notifyDataSetChanged();
    }

    // Methode, um das aktuell ausgewählte Datum abzurufen
    public int getSelectedDay() {
        return selectedDay;
    }
}