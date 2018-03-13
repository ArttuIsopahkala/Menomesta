package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 7.12.2016.
 */
public class Session {
    public String sessionId;
    public String userId;
    public long startTime;
    @Exclude
    public User recipient;

    public Session() {
        // Default constructor required for calls to DataSnapshot.getValue(Session.class)
    }
}
