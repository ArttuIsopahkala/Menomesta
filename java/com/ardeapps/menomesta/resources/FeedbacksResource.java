package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class FeedbacksResource extends FirebaseDatabaseService {
    private static FeedbacksResource instance;
    private static DatabaseReference database;

    public static FeedbacksResource getInstance() {
        if (instance == null) {
            instance = new FeedbacksResource();
        }
        database = getDatabase().child(FEED_BACKS);
        return instance;
    }

    public void addFeedback(String feedback, final AddSuccessListener handler) {
        String feedbackId = database.child(AppRes.getUser().userId).push().getKey();
        addData(database.child(AppRes.getUser().userId).child(feedbackId), feedback, handler);
    }
}
