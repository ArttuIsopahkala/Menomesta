package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Review;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetReviewsHandler {
    void onReviewsLoaded(ArrayList<Review> reviews);
}
