package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetEventsHandler;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class EventsResource extends FirebaseService {
    private static EventsResource instance;
    private static DatabaseReference database;

    public static EventsResource getInstance() {
        if (instance == null) {
            instance = new EventsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(EVENTS);
        return instance;
    }

    public void addEvent(Event event, final AddSuccessListener handler) {
        event.eventId = database.push().getKey();
        addData(database.child(event.eventId), event, handler);
    }

    public void editEvent(Event event, final EditSuccessListener handler) {
        editData(database.child(event.eventId), event, handler);
    }

    public void removeEvent(String eventId, final EditSuccessListener handler) {
        editData(database.child(eventId), null, handler);
    }

    /**
     * Hakee tapahtumat vain eilisen jälkeen. Vanhat tapahtumat eivät poistu.
     */
    public void getEvents(final GetEventsHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Event event = object.getValue(Event.class);
                    if (event != null) {
                        // Säilytä tapahtuma aamuun asti
                        if (DateUtil.isAfterYesterday(event.startTime))
                            events.add(event);
                    }
                }
                handler.onEventsLoaded(events);
            }
        });
    }
}
