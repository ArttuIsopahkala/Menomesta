package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Rating;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetRatingsHandler {
    void onRatingsLoaded(Map<String, ArrayList<Rating>> ratings, Map<String, String> userRatings);
}
