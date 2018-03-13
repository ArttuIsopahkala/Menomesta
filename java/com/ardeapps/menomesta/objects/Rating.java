package com.ardeapps.menomesta.objects;

/**
 * Created by Arttu on 18.9.2016.
 */
public class Rating {
    public String ratingId;
    public String barId;
    public String userId;
    public float rating;
    public long time;

    public Rating() {
        // Default constructor required for calls to DataSnapshot.getValue(Vote.class)
    }
}
