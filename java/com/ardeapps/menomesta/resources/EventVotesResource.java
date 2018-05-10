package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetEventVotesHandler;
import com.ardeapps.menomesta.handlers.GetUsersMapHandler;
import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class EventVotesResource extends FirebaseDatabaseService {
    private static EventVotesResource instance;
    private static DatabaseReference database;

    public static EventVotesResource getInstance() {
        if (instance == null) {
            instance = new EventVotesResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(EVENT_VOTES);
        return instance;
    }

    public void addEventVote(String eventId, EventVote eventVote, final AddSuccessListener handler) {
        eventVote.voteId = database.child(eventId).push().getKey();
        addData(database.child(eventId).child(eventVote.voteId), eventVote, handler);
    }

    public void removeEventVote(String eventId, String voteId, final EditSuccessListener handler) {
        editData(database.child(eventId).child(voteId), null, handler);
    }

    /**
     * Käytetään kun haetaan tapahtumalistaan eventVotet.
     */
    public void getEventVotes(final GetEventVotesHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, String> userEventVotes = new HashMap<>();
                final Map<String, ArrayList<EventVote>> eventVotesMap = new HashMap<>();
                final ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot event : dataSnapshot.getChildren()) {
                    String eventId = event.getKey();
                    ArrayList<EventVote> eventVotes = new ArrayList<>();
                    // Lista voteja
                    for (DataSnapshot object : event.getChildren()) {
                        final EventVote eventVote = object.getValue(EventVote.class);
                        if (eventVote != null) {
                            if (eventVote.eventEndTime > System.currentTimeMillis()) {
                                if (eventVote.userId.equals(AppRes.getUser().userId) && userEventVotes.get(eventId) == null) {
                                    userEventVotes.put(eventId, eventVote.voteId);
                                }
                                if (!userIds.contains(eventVote.userId)) {
                                    userIds.add(eventVote.userId);
                                }
                                eventVotes.add(eventVote);
                            } else {
                                // Poistetaan vanha vote
                                editData(database.child(eventId).child(eventVote.voteId), null);
                            }
                        }
                    }
                    eventVotesMap.put(eventId, eventVotes);
                }
                if (userIds.size() > 0) {
                    UsersResource.getInstance().getUsers(userIds, new GetUsersMapHandler() {
                        @Override
                        public void onUsersMapLoaded(Map<String, User> usersMap) {
                            for (ArrayList<EventVote> eventVotes : eventVotesMap.values()) {
                                for (EventVote eventVote : eventVotes) {
                                    eventVote.user = usersMap.get(eventVote.userId);
                                }
                            }
                            handler.onEventVotesLoaded(eventVotesMap, userEventVotes);
                        }
                    });
                } else {
                    handler.onEventVotesLoaded(eventVotesMap, userEventVotes);
                }
            }
        });
    }
}
