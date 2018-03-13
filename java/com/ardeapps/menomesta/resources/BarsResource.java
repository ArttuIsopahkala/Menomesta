package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetBarHandler;
import com.ardeapps.menomesta.handlers.GetBarsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class BarsResource extends FirebaseService {
    private static BarsResource instance;
    private static DatabaseReference database;

    public static BarsResource getInstance() {
        if (instance == null) {
            instance = new BarsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(BARS);
        return instance;
    }

    public void editBar(Bar bar, final EditSuccessListener handler) {
        editData(database.child(bar.barId), bar, handler);
    }

    public void getBars(final GetBarsHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, Bar> bars = new HashMap<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Bar bar = object.getValue(Bar.class);
                    if (bar != null) {
                        bars.put(bar.barId, bar);
                    }
                }
                handler.onBarsLoaded(bars);
            }
        });
    }

    public void getBar(final String barId, final GetBarHandler handler) {
        getData(database.child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Bar bar = dataSnapshot.getValue(Bar.class);
                handler.onBarLoaded(bar);
            }
        });
    }
}
