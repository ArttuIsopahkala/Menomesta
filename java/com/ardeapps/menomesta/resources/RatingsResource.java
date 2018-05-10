package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetRatingsHandler;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class RatingsResource extends FirebaseDatabaseService {
    private static RatingsResource instance;
    private static DatabaseReference database;

    public static RatingsResource getInstance() {
        if (instance == null) {
            instance = new RatingsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(RATINGS);
        return instance;
    }

    /**
     * Tositaiseksi käytössä vain tämä, koska rating voidaan lisätä vain kerran päivässä eikä sitä voi muokata.
     */
    public void addRating(String barId, Rating rating, final AddSuccessListener handler) {
        rating.ratingId = database.child(barId).push().getKey();
        addData(database.child(barId).child(rating.ratingId), rating, handler);
    }

    public void editRating(String barId, Rating rating, final EditSuccessListener handler) {
        editData(database.child(barId).child(rating.ratingId), rating, handler);
    }

    /**
     * Hakee ratingit vain eilisen jälkeen.
     */
    public void getRatings(final GetRatingsHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, ArrayList<Rating>> ratingsMap = new HashMap<>();
                Map<String, String> userRatings = new HashMap<>();

                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    String barId = bar.getKey();
                    ArrayList<Rating> ratings = new ArrayList<>();
                    // Lista ratingeja
                    for (DataSnapshot object : bar.getChildren()) {
                        Rating rating = object.getValue(Rating.class);
                        if (rating != null) {
                            if (DateUtil.isToday(rating.time)) {
                                ratings.add(rating);
                                if (rating.userId.equals(AppRes.getUser().userId) && userRatings.get(barId) == null) {
                                    userRatings.put(barId, rating.ratingId);
                                }
                            } else {
                                // Poistetaan vanha vote
                                editData(database.child(barId).child(rating.ratingId), null);
                            }
                        }
                    }
                    ratingsMap.put(barId, ratings);
                }

                handler.onRatingsLoaded(ratingsMap, userRatings);
            }
        });
    }
}
