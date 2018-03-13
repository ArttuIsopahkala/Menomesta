package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.objects.BarRequest;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class BarRequestsResource extends FirebaseService {
    private static BarRequestsResource instance;
    private static DatabaseReference database;

    public static BarRequestsResource getInstance() {
        if (instance == null) {
            instance = new BarRequestsResource();
        }
        database = getDatabase().child(BAR_REQUESTS);
        return instance;
    }

    public void addBarRequest(BarRequest barRequest, final AddSuccessListener handler) {
        barRequest.requestId = database.child(AppRes.getUser().userId).push().getKey();
        addData(database.child(AppRes.getUser().userId).child(barRequest.requestId), barRequest, handler);
    }
}
