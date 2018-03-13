package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.RatingStat;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetRatingStatsHandler {
    void onRatingStatsLoaded(Map<String, RatingStat> ratingStats);
}
