package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.Vote;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetEventVotesHandler {
    void onEventVotesLoaded(Map<String, ArrayList<EventVote>> eventVotes, Map<String, String> userEventVotes);
}
