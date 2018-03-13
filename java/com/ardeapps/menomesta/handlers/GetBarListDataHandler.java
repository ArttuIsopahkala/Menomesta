package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetBarListDataHandler {
    void onBarListDataLoaded(
            Map<String, Bar> bars,
            Map<String, ArrayList<Vote>> votes,
            Map<String, String> userVotes,
            Map<String, ArrayList<Rating>> ratings,
            Map<String, String> userRatings,
            Map<String, VoteStat> allTimeVoteStats,
            Map<String, RatingStat> allTimeRatingStats,
            ArrayList<Drink> drinks
    );
}
