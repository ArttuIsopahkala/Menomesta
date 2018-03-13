package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class RatingStatsResource extends FirebaseService {
    private static RatingStatsResource instance;
    private static DatabaseReference database;

    public static RatingStatsResource getInstance() {
        if (instance == null) {
            instance = new RatingStatsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(RATING_STATS);
        return instance;
    }

    public void editRatingStats(final double rating, final String barId, final GetRatingStatsHandler handler) {
        getData(database.child(ALL_TIME).child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final RatingStat ratingStat = dataSnapshot.exists() ? dataSnapshot.getValue(RatingStat.class) : new RatingStat();
                if (ratingStat != null) {
                    editData(database.child(ALL_TIME).child(barId), editStat(barId, rating, ratingStat));
                    getAllTimeRatingStats(new GetRatingStatsHandler() {
                        @Override
                        public void onRatingStatsLoaded(Map<String, RatingStat> ratingStats) {
                            handler.onRatingStatsLoaded(ratingStats);
                        }
                    });
                }
            }
        });
        getDataAnonymously(database.child(THIS_WEEK).child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                // Jos VoteStat updateTime on tällä viikolla, käytetään samaa objektia. Muuten luodaan uusi.
                RatingStat ratingStat = new RatingStat();
                if (dataSnapshot.exists()) {
                    RatingStat ratingClone = dataSnapshot.getValue(RatingStat.class);
                    if (ratingClone != null && DateUtil.isOnThisWeek(ratingClone.updateTime)) {
                        ratingStat = ratingClone;
                    }
                }
                editData(database.child(THIS_WEEK).child(barId), editStat(barId, rating, ratingStat));
            }
        });
    }

    private RatingStat editStat(String barId, double rating, RatingStat ratingStat) {
        ratingStat.updateTime = System.currentTimeMillis();
        ratingStat.ratingCount++;
        ratingStat.ratingSum += rating;
        ratingStat.barId = barId;
        return ratingStat;
    }

    /**
     * Haetaan tämän viikon rating statsit. Jos updateTime on ennen tätä viikkoa, poistetaan statsi.
     */
    public void getThisWeekRatingStats(final GetRatingStatsHandler handler) {
        getData(database.child(THIS_WEEK), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, RatingStat> ratingStats = new HashMap<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    RatingStat ratingStat = bar.getValue(RatingStat.class);
                    if (ratingStat != null) {
                        if (DateUtil.isOnThisWeek(ratingStat.updateTime)) {
                            ratingStats.put(bar.getKey(), ratingStat);
                        } else {
                            editData(database.child(THIS_WEEK).child(bar.getKey()), null);
                        }
                    }
                }
                handler.onRatingStatsLoaded(ratingStats);
            }
        });
    }

    /**
     * Haetaan kaikkien aikojen rating statsit. Ei poisteta mitään.
     */
    public void getAllTimeRatingStats(final GetRatingStatsHandler handler) {
        getData(database.child(ALL_TIME), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, RatingStat> ratingStats = new HashMap<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    RatingStat ratingStat = bar.getValue(RatingStat.class);
                    if (ratingStat != null) {
                        ratingStats.put(bar.getKey(), ratingStat);
                    }
                }
                handler.onRatingStatsLoaded(ratingStats);
            }
        });
    }
}
