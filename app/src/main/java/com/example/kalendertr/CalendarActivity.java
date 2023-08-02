package com.example.kalendertr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;


public class CalendarActivity extends Activity {
    private GridView calendarGrid;
    private CalendarAdapter calendarAdapter;
    private Button previousMonthButton, nextMonthButton, addEventButton;
    private TextView monthTextView;
    private int currentYear, currentMonth;
    private int currentDay;
    private ListView eventListView;

    private int selectedDay = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarGrid = findViewById(R.id.calendarGrid);
        previousMonthButton = findViewById(R.id.previous_month_button);
        nextMonthButton = findViewById(R.id.next_month_button);
        monthTextView = findViewById(R.id.monthTextView);
        addEventButton = findViewById(R.id.add_event_button);
        eventListView = findViewById(R.id.event_list_view);

        calendarAdapter = new CalendarAdapter(this);
        calendarGrid.setAdapter(calendarAdapter);

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateCalendar(currentYear, currentMonth);

        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMonth == 0) {
                    currentMonth = 11;
                    currentYear--;
                } else {
                    currentMonth--;
                }
                updateCalendar(currentYear, currentMonth);
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMonth == 11) {
                    currentMonth = 0;
                    currentYear++;
                } else {
                    currentMonth++;
                }
                updateCalendar(currentYear, currentMonth);
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDay != -1) {
                    showAddEventDialog(selectedDay);
                } else {
                    Toast.makeText(CalendarActivity.this, "Bitte wählen Sie zuerst ein Datum aus.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int dayOfMonth = (int) adapterView.getItemAtPosition(position);
                if (dayOfMonth != 0) {
                    selectedDay = dayOfMonth;
                    calendarAdapter.setSelectedDay(selectedDay);
                    onDateSelected(selectedDay);
                }
            }
        });
    }

    private void updateCalendar(int year, int month) {
        calendarAdapter.updateCalendar(year, month);
        String monthYearString = calendarAdapter.getMonthYearString(year, month);
        monthTextView.setText(monthYearString);
    }

    private void showAddEventDialog(final int dayOfMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event hinzufügen");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        final EditText inputTitle = dialogView.findViewById(R.id.input_title);
        final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.updateDate(currentYear, currentMonth, dayOfMonth);

        // ListView initialisieren
        ListView eventListView = dialogView.findViewById(R.id.event_list_view);

        // Liste der bereits gespeicherten Events für den ausgewählten Tag abrufen und der ListView zuweisen
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, dayOfMonth);
        Date selectedDate = calendar.getTime();

        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");

        eventsRef.whereEqualTo("eventDate", selectedDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Event> events = queryDocumentSnapshots.toObjects(Event.class);

                        // Verknüpfen des ListView mit dem Layout list_item_event.xml über den EventListAdapter
                        EventListAdapter eventListAdapter = new EventListAdapter(CalendarActivity.this, events);
                        eventListView.setAdapter(eventListAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Fehlerbehandlung, falls das Abrufen der Events fehlschlägt
                    }
                });

        builder.setView(dialogView);

        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String eventTitle = inputTitle.getText().toString().trim();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date eventDate = calendar.getTime();

                if (!eventTitle.isEmpty()) {
                    Event event = new Event(eventTitle, eventDate);

                    CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");

                    eventsRef.add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(CalendarActivity.this, "Event hinzugefügt", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CalendarActivity.this, "Fehler beim Hinzufügen des Events", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void onDateSelected(int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, dayOfMonth);
        Date selectedDate = calendar.getTime();

        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");

        eventsRef.whereEqualTo("eventDate", selectedDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Event> events = queryDocumentSnapshots.toObjects(Event.class);

                        // Zeige die ListView und aktualisiere ihre Daten
                        eventListView.setVisibility(View.VISIBLE);
                        EventListAdapter eventListAdapter = new EventListAdapter(CalendarActivity.this, events);
                        eventListView.setAdapter(eventListAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Behandeln Sie den Fehler beim Abrufen der Ereignisse
                    }
                });
    }
}