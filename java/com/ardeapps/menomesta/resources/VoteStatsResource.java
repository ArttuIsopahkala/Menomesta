package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class VoteStatsResource extends FirebaseDatabaseService {
    private static VoteStatsResource instance;
    private static DatabaseReference database;

    public static VoteStatsResource getInstance() {
        if (instance == null) {
            instance = new VoteStatsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(VOTE_STATS);
        return instance;
    }

    public void editVoteStat(final String barId, final GetVoteStatsHandler handler) {
        getData(database.child(ALL_TIME).child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final VoteStat voteStat = dataSnapshot.exists() ? dataSnapshot.getValue(VoteStat.class) : new VoteStat();
                if (voteStat != null) {
                    editData(database.child(ALL_TIME).child(barId), editStat(barId, voteStat));
                    getAllTimeVoteStats(new GetVoteStatsHandler() {
                        @Override
                        public void onVoteStatsLoaded(Map<String, VoteStat> voteStats) {
                            handler.onVoteStatsLoaded(voteStats);
                        }
                    });
                }
            }
        });
        getDataAnonymously(database.child(THIS_WEEK).child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                // Jos VoteStat updateTime on tällä viikolla, käytetään samaa objektia. Muuten luodaan uusi.
                VoteStat voteStat = new VoteStat();
                if (dataSnapshot.exists()) {
                    VoteStat voteClone = dataSnapshot.getValue(VoteStat.class);
                    if (voteClone != null && DateUtil.isOnThisWeek(voteClone.updateTime)) {
                        voteStat = voteClone;
                    }
                }
                editData(database.child(THIS_WEEK).child(barId), editStat(barId, voteStat));
            }
        });
    }

    private VoteStat editStat(String barId, VoteStat voteStat) {
        User user = AppRes.getUser();
        int age = DateUtil.getAgeInYears(user.birthday);
        voteStat.ageSum = voteStat.ageSum + age;
        voteStat.voteCount++;
        if (user.isMale()) {
            voteStat.maleCount++;
        } else {
            voteStat.femaleCount++;
        }
        voteStat.updateTime = System.currentTimeMillis();
        voteStat.barId = barId;
        return voteStat;
    }

    /**
     * Haetaan tämän viikon vote statsit. Jos updateTime on ennen tätä viikkoa, poistetaan statsi.
     */
    public void getThisWeekVoteStats(final GetVoteStatsHandler handler) {
        getData(database.child(THIS_WEEK), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, VoteStat> voteStats = new HashMap<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    VoteStat voteStat = bar.getValue(VoteStat.class);
                    if (voteStat != null) {
                        if (DateUtil.isOnThisWeek(voteStat.updateTime)) {
                            voteStats.put(bar.getKey(), voteStat);
                        } else {
                            editData(database.child(THIS_WEEK).child(bar.getKey()), null);
                        }
                    }
                }
                handler.onVoteStatsLoaded(voteStats);
            }
        });
    }

    /**
     * Haetaan kaikkien aikojen vote statsit. Ei poisteta mitään.
     */
    public void getAllTimeVoteStats(final GetVoteStatsHandler handler) {
        getData(database.child(ALL_TIME), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<String, VoteStat> voteStats = new HashMap<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    VoteStat voteStat = bar.getValue(VoteStat.class);
                    if (voteStat != null) {
                        voteStats.put(bar.getKey(), voteStat);
                    }
                }
                handler.onVoteStatsLoaded(voteStats);
            }
        });
    }
}
