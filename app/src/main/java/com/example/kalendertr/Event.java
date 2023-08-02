package com.example.kalendertr;

import java.util.Calendar;
import java.util.Date;

public class Event {
    private String eventTitle;
    private Date eventDate;

    public Event() {
        this.eventDate = new Date();
    }

    public Event(String eventTitle, Date eventDate) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
