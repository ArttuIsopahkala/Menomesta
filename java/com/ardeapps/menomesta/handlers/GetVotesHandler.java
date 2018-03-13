package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Vote;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetVotesHandler {
    void onVotesLoaded(Map<String, ArrayList<Vote>> votes, Map<String, String> userVotes);
}
