package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.GetVotesHandler;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class VotesLogResource extends FirebaseService {
    private static VotesLogResource instance;
    private static DatabaseReference database;

    public static VotesLogResource getInstance() {
        if (instance == null) {
            instance = new VotesLogResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(VOTES_LOG);
        return instance;
    }

    public void addVoteLog(String barId, Vote vote) {
        if (!StringUtils.isEmptyString(vote.voteId))
            addData(database.child(barId).child(vote.voteId), vote);
    }

    /**
     * Vain admin käyttöön
     * Hakee kaikki votet. Baarin nimi - votet
     */
    public void getVotesForStatistics(final GetVotesHandler handler) {
        final Map<String, ArrayList<Vote>> allTimeVotesMap = new HashMap<>();
        final Map<String, ArrayList<Vote>> thisWeekVotesMap = new HashMap<>();
        final ArrayList<String> userIds = new ArrayList<>();
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    for (DataSnapshot object : bar.getChildren()) {
                        String barId = bar.getKey();
                        Vote vote = object.getValue(Vote.class);
                        if (vote != null) {
                            ArrayList<Vote> existingAllTimeVotes = allTimeVotesMap.get(barId);
                            ArrayList<Vote> allTimeVotes = existingAllTimeVotes != null ? existingAllTimeVotes : new ArrayList<Vote>();
                            allTimeVotes.add(vote);
                            allTimeVotesMap.put(barId, allTimeVotes);


                            ArrayList<Vote> existingThisWeekVotes = thisWeekVotesMap.get(barId);
                            ArrayList<Vote> thisWeekTimeVotes = existingThisWeekVotes != null ? existingThisWeekVotes : new ArrayList<Vote>();
                            if (vote.time > DateUtil.getStartOfWeekInMillis() && vote.time < DateUtil.getEndOfWeekInMillis()) {
                                thisWeekTimeVotes.add(vote);
                                thisWeekVotesMap.put(barId, existingThisWeekVotes);
                            }
                            if (!userIds.contains(vote.userId)) {
                                userIds.add(vote.userId);
                            }
                        }
                    }
                    //handler.onBarVotesLoaded(allTimeVotesMap, thisWeekVotesMap, userIds);
                }
            }
        });
    }
}
