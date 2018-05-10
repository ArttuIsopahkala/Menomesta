package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class RatingsLogResource extends FirebaseDatabaseService {
    private static RatingsLogResource instance;
    private static DatabaseReference database;

    public static RatingsLogResource getInstance() {
        if (instance == null) {
            instance = new RatingsLogResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(RATINGS_LOG);
        return instance;
    }

    public void addRatingLog(String barId, Rating rating) {
        rating.ratingId = database.child(barId).push().getKey();
        addData(database.child(barId).child(rating.ratingId), rating);
    }

    /**
     * Vain admin käyttöön
     * Hakee kaikki ratingit. Baarin nimi - ratingit
     */
    /*public void getRatingsForStatistics(final GetBarRatingsHandler handler) {
        final Map<String, ArrayList<Rating>> allTimeRatingsMap = new HashMap<>();
        final Map<String, ArrayList<Rating>> thisWeekRatingsMap = new HashMap<>();
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    for (DataSnapshot object : bar.getChildren()) {
                        String barId = bar.getKey();
                        Rating rating = object.getValue(Rating.class);
                        if (rating != null) {
                            ArrayList<Rating> existingAllTimeRatings = allTimeRatingsMap.get(barId);
                            ArrayList<Rating> allTimeRatings = existingAllTimeRatings != null ? existingAllTimeRatings : new ArrayList<Rating>();
                            allTimeRatings.add(rating);
                            allTimeRatingsMap.put(barId, allTimeRatings);


                            ArrayList<Rating> existingThisWeekRatings = thisWeekRatingsMap.get(barId);
                            ArrayList<Rating> thisWeekTimeRatings = existingThisWeekRatings != null ? existingThisWeekRatings : new ArrayList<Rating>();
                            if (rating.time > DateUtil.getStartOfWeekInMillis() && rating.time < DateUtil.getEndOfWeekInMillis()) {
                                thisWeekTimeRatings.add(rating);
                                thisWeekRatingsMap.put(barId, existingThisWeekRatings);
                            }
                        }
                    }
                    handler.onBarRatingsLoaded(allTimeRatingsMap, thisWeekRatingsMap);
                }
            }
        });
    }*/
}
