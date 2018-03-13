package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arttu on 15.9.2016.
 */
public class Event {
    public String eventId;
    public String userId;
    public String name;
    public String address;
    public String description;
    public long startTime;
    public int price;
    public List<String> usersReported;
    public List<String> usersJoined;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    @Exclude
    public Event clone() {
        Event clone = new Event();
        clone.eventId = this.eventId;
        clone.userId = this.userId;
        clone.name = this.name;
        clone.address = this.address;
        clone.description = this.description;
        clone.price = this.price;
        clone.startTime = this.startTime;
        if (this.usersReported == null) {
            clone.usersReported = new ArrayList<>();
        } else {
            clone.usersReported = this.usersReported;
        }
        if (this.usersJoined == null) {
            clone.usersJoined = new ArrayList<>();
        } else {
            clone.usersJoined = this.usersJoined;
        }
        return clone;
    }
}
