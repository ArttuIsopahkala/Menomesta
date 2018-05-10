package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Arttu on 15.9.2016. Tätä ei tallenneta tietokantaan.
 */
public class Event {
    public String barId;
    public String eventId;
    public String name;
    public String description;
    public long startTime;
    public long endTime;
    public int attendingCount;

    @Exclude
    public Event clone() {
        Event clone = new Event();
        clone.barId = this.barId;
        clone.eventId = this.eventId;
        clone.name = this.name;
        clone.description = this.description;
        clone.startTime = this.startTime;
        clone.endTime = this.endTime;
        clone.attendingCount = this.attendingCount;
        return clone;
    }

    @Exclude
    public static ArrayList<Event> convertToArrayList(Map<String, ArrayList<Event>> eventsMap) {
        ArrayList<Event> events = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Event>> entry : eventsMap.entrySet()) {
            events.addAll(entry.getValue());
        }
        return events;
    }

    @Exclude
    public static Comparator<Event> customEventComparator = new Comparator<Event>() {
        @Override
        public int compare(final Event entry1, final Event entry2) {
            return entry1.startTime < entry2.startTime ? -1 : 1;
        }
    };
}
