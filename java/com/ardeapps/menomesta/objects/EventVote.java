package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 18.9.2016.
 */
public class EventVote {
    public String voteId;
    public String eventId;
    public String barId;
    public String userId;
    public long time;
    public long eventEndTime;
    @Exclude
    public User user;

    public EventVote() {
        // Default constructor required for calls to DataSnapshot.getValue(EventVote.class)
    }
}
