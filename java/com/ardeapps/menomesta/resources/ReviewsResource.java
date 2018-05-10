package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetReviewsHandler;
import com.ardeapps.menomesta.handlers.GetUsersMapHandler;
import com.ardeapps.menomesta.objects.Review;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class ReviewsResource extends FirebaseDatabaseService {
    private static ReviewsResource instance;
    private static DatabaseReference database;

    public static ReviewsResource getInstance() {
        if (instance == null) {
            instance = new ReviewsResource();
        }
        database = getDatabase().child(CITIES).child(AppRes.getCity()).child(REVIEWS);
        return instance;
    }

    public void addReview(String barId, Review review, final AddSuccessListener handler) {
        review.reviewId = database.child(barId).push().getKey();
        addData(database.child(barId).child(review.reviewId), review, handler);
    }

    public void editReview(String barId, Review review, final EditSuccessListener handler) {
        editData(database.child(barId).child(review.reviewId), review, handler);
    }

    public void removeReview(String barId, String reviewId, final EditSuccessListener handler) {
        editData(database.child(barId).child(reviewId), null, handler);
    }

    public void getReviews(String barId, final GetReviewsHandler handler) {
        getData(database.child(barId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final ArrayList<Review> reviews = new ArrayList<>();
                final ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Review review = object.getValue(Review.class);
                    if(review != null) {
                        if (!userIds.contains(review.userId)) {
                            userIds.add(review.userId);
                        }
                        reviews.add(review);
                    }
                }
                if (userIds.size() > 0) {
                    UsersResource.getInstance().getUsers(userIds, new GetUsersMapHandler() {
                        @Override
                        public void onUsersMapLoaded(Map<String, User> usersMap) {
                            for (Review review : reviews) {
                                review.user = usersMap.get(review.userId);
                            }
                            handler.onReviewsLoaded(reviews);
                        }
                    });
                } else {
                    handler.onReviewsLoaded(reviews);
                }
            }
        });
    }
}
