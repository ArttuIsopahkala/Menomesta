package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.objects.CityRequest;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class CityRequestsResource extends FirebaseDatabaseService {
    private static CityRequestsResource instance;
    private static DatabaseReference database;

    public static CityRequestsResource getInstance() {
        if (instance == null) {
            instance = new CityRequestsResource();
        }
        database = getDatabase().child(CITY_REQUESTS);
        return instance;
    }

    public void addCityRequest(CityRequest cityRequest, final AddSuccessListener handler) {
        cityRequest.requestId = database.push().getKey();
        addData(database.child(AppRes.getUser().userId).child(cityRequest.requestId), cityRequest, handler);
    }

}
