package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arttu on 25.9.2016.
 */
public class Review {

    public String reviewId;
    public String barId;
    public String userId;
    public float rating;
    public String message;
    public long time;
    public List<String> usersVoted;
    public List<String> usersReported;
    @Exclude
    public User user;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    @Exclude
    public static ArrayList<Review> setReview(ArrayList<Review> reviews, String reviewId, Review review) {
        if (reviews == null)
            reviews = new ArrayList<>();

        boolean exists = false;
        for (int index = 0; index < reviews.size(); index++) {
            if (reviews.get(index).reviewId.equals(reviewId)) {
                exists = true;
                if (review == null) {
                    reviews.remove(index);
                } else {
                    reviews.set(index, review);
                }
                break;
            }
        }
        if (!exists && review != null) {
            reviews.add(review);
        }
        return reviews;
    }

    @Exclude
    public Review clone() {
        Review clone = new Review();
        clone.reviewId = this.reviewId;
        clone.barId = this.barId;
        clone.userId = this.userId;
        clone.rating = this.rating;
        clone.message = this.message;
        clone.time = this.time;
        if (this.usersVoted == null) {
            clone.usersVoted = new ArrayList<>();
        } else {
            clone.usersVoted = this.usersVoted;
        }
        if (this.usersReported == null) {
            clone.usersReported = new ArrayList<>();
        } else {
            clone.usersReported = this.usersReported;
        }
        clone.user = this.user;
        return clone;
    }
}
