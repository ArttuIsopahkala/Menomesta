package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetBarDetailsHandler;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class BarDetailsResource extends FirebaseService {
    private static BarDetailsResource instance;
    private static DatabaseReference database;

    public static BarDetailsResource getInstance() {
        if (instance == null) {
            instance = new BarDetailsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(BAR_DETAILS);
        return instance;
    }

    public void editBarDetails(final String barId, final BarDetails barDetails, final EditSuccessListener handler) {
        editData(database.child(barId), barDetails, handler);
    }

    public void getBarDetails(final String barId, final GetBarDetailsHandler handler) {
        getData(database.child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                BarDetails barDetails = dataSnapshot.getValue(BarDetails.class);
                handler.onBarDetailsLoaded(barDetails);
            }
        });
    }
}
