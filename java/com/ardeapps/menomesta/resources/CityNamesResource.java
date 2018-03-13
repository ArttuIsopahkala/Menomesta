package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.handlers.GetCityNamesHandler;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Arttu on 19.1.2018.
 */

public class CityNamesResource extends FirebaseService {
    private static CityNamesResource instance;
    private static DatabaseReference database;

    public static CityNamesResource getInstance() {
        if (instance == null) {
            instance = new CityNamesResource();
        }
        database = getDatabase().child(CITY_NAMES);
        return instance;
    }

    public void getCityNames(final GetCityNamesHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                ArrayList<String> cityNames = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    cityNames.add(object.getKey());
                }
                handler.onCityNamesLoaded(cityNames);
            }
        });
    }
}
