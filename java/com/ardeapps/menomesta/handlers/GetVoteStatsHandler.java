package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.VoteStat;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetVoteStatsHandler {
    void onVoteStatsLoaded(Map<String, VoteStat> voteStats);
}
