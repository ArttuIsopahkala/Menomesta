package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetUsersMapHandler;
import com.ardeapps.menomesta.handlers.GetVotesHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.Vote;
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

public class VotesResource extends FirebaseDatabaseService {
    private static VotesResource instance;
    private static DatabaseReference database;

    public static VotesResource getInstance() {
        if (instance == null) {
            instance = new VotesResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(VOTES);
        return instance;
    }

    public void addVote(String barId, Vote vote, final AddSuccessListener handler) {
        vote.voteId = database.child(barId).push().getKey();
        addData(database.child(barId).child(vote.voteId), vote, handler);
    }

    public void removeVote(String barId, String voteId, final EditSuccessListener handler) {
        editData(database.child(barId).child(voteId), null, handler);
    }

    /**
     * Käytetään kun haetaan baarilistaan votet.
     */
    public void getVotes(final GetVotesHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, String> userVotes = new HashMap<>();
                final Map<String, ArrayList<Vote>> votesMap = new HashMap<>();
                final ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot bar : dataSnapshot.getChildren()) {
                    String barId = bar.getKey();
                    ArrayList<Vote> votes = new ArrayList<>();
                    // Lista voteja
                    for (DataSnapshot object : bar.getChildren()) {
                        final Vote vote = object.getValue(Vote.class);
                        if (vote != null) {
                            if (DateUtil.isToday(vote.time)) {
                                if (vote.userId.equals(AppRes.getUser().userId) && userVotes.get(barId) == null) {
                                    userVotes.put(barId, vote.voteId);
                                }
                                if (!userIds.contains(vote.userId)) {
                                    userIds.add(vote.userId);
                                }
                                votes.add(vote);
                            } else {
                                // Poistetaan vanha vote
                                editData(database.child(barId).child(vote.voteId), null);
                            }
                        }
                    }
                    votesMap.put(barId, votes);
                }
                if (userIds.size() > 0) {
                    UsersResource.getInstance().getUsers(userIds, new GetUsersMapHandler() {
                        @Override
                        public void onUsersMapLoaded(Map<String, User> usersMap) {
                            for (ArrayList<Vote> votes : votesMap.values()) {
                                for (Vote vote : votes) {
                                    vote.user = usersMap.get(vote.userId);
                                }
                            }
                            handler.onVotesLoaded(votesMap, userVotes);
                        }
                    });
                } else {
                    handler.onVotesLoaded(votesMap, userVotes);
                }
            }
        });
    }
}
