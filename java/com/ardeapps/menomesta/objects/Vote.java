package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 18.9.2016.
 */
public class Vote {
    public String voteId;
    public String barId;
    public String userId;
    public long time;
    @Exclude
    public User user;

    public Vote() {
        // Default constructor required for calls to DataSnapshot.getValue(Vote.class)
    }
}
