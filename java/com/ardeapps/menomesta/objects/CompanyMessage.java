package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

/**
 * Created by Arttu on 25.9.2016.
 */
public class CompanyMessage {
    public String commentId;
    public String userId;
    public long time;
    public String message;
    @Exclude
    public User user;

    public CompanyMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(CompanyMessage.class)
    }
}
