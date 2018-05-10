package com.ardeapps.menomesta.fragments;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.objects.Review;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RatingsLogResource;
import com.ardeapps.menomesta.resources.ReviewsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.GenderBars;
import com.ardeapps.menomesta.views.IconView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BarDetailsFragment extends Fragment {

    LinearLayout reviewList;
    LinearLayout infoContainer;
    ArrayList<Review> reviews;
    Map<String, VoteStat> allTimeVoteStats = new HashMap<>();
    Map<String, RatingStat> allTimeRatingStats = new HashMap<>();
    ArrayList<Drink> drinks;
    Bar bar;
    BarDetails barDetails;
    Location location;

    Context context;
    TextView title;
    TextView positionAllTime;
    TextView averageAgeText;
    TextView reviewsText;
    Button addReview;
    TextView ageTitleText;
    TextView entranceTitleText;
    Button edit_bar;
    LinearLayout ratingContent;
    TextView ratingText;
    LinearLayout drinkListView;
    IconView map_icon;
    TextView barType;
    TextView distanceText;
    TextView ageFridayText;
    TextView ageSaturdayText;
    TextView entranceText;
    TextView entranceDateText;
    String distance = "0m";
    TextView emptyMessage;
    LinearLayout ageContainer;
    LinearLayout foodContent;

    GenderBars genderBars;

    AppRes appRes;

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setBarDetails(BarDetails barDetails) {
        this.barDetails = barDetails;
    }

    public void setDrinks(ArrayList<Drink> drinks) {
        this.drinks = drinks;
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        allTimeVoteStats = appRes.getAllTimeVoteStats();
        allTimeRatingStats = appRes.getAllTimeRatingStats();
        location = appRes.getLocation();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bar_details, container, false);

        reviewList = (LinearLayout) v.findViewById(R.id.reviewList);
        infoContainer = (LinearLayout) v.findViewById(R.id.infoContainer);
        ratingContent = (LinearLayout) v.findViewById(R.id.ratingContent);
        edit_bar = (Button) v.findViewById(R.id.editBar);
        ratingText = (TextView) v.findViewById(R.id.ratingText);
        title = (TextView) v.findViewById(R.id.title);
        positionAllTime = (TextView) v.findViewById(R.id.positionAllTime);
        averageAgeText = (TextView) v.findViewById(R.id.averageAge);
        reviewsText = (TextView) v.findViewById(R.id.reviewsText);
        addReview = (Button) v.findViewById(R.id.addReview);
        drinkListView = (LinearLayout) v.findViewById(R.id.drinksList);
        map_icon = (IconView) v.findViewById(R.id.map_icon);
        barType = (TextView) v.findViewById(R.id.barType);
        distanceText = (TextView) v.findViewById(R.id.distanceText);
        ageFridayText = (TextView) v.findViewById(R.id.ageFridayText);
        ageSaturdayText = (TextView) v.findViewById(R.id.ageSaturdayText);
        entranceText = (TextView) v.findViewById(R.id.entranceText);
        entranceDateText = (TextView) v.findViewById(R.id.entranceDateText);
        entranceTitleText = (TextView) v.findViewById(R.id.entranceTitleText);
        ageTitleText = (TextView) v.findViewById(R.id.ageTitleText);
        emptyMessage = (TextView) v.findViewById(R.id.emptyMessage);
        ageContainer = (LinearLayout) v.findViewById(R.id.ageContainer);
        foodContent = (LinearLayout) v.findViewById(R.id.foodContent);
        genderBars = (GenderBars) v.findViewById(R.id.genderBars);


        title.setText(bar.name);

        updateBar();
        updateBarDetails();
        updateDrinks();
        updateReviews();
        updateStats();


        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddReviewDialogFragment reviewDialog = new AddReviewDialogFragment();
                reviewDialog.setBar(bar);
                reviewDialog.setListener(new AddReviewDialogFragment.RatingDialogListener() {
                    @Override
                    public void onDialogClose() {
                        reviewDialog.dismiss();
                    }

                    @Override
                    public void onReviewSubmit(final Review review) {
                        reviewDialog.dismiss();

                        if (Helper.hasUserAddedReviewToday(reviews)) {
                            InfoDialogFragment ratingFoundDialog = InfoDialogFragment.newInstance(getString(R.string.error_rating_title), getString(R.string.error_rating_desc));
                            ratingFoundDialog.show(getFragmentManager(), "Arvostelu samana päivänä");
                            return;
                        }

                        ReviewsResource.getInstance().addReview(review.barId, review, new AddSuccessListener() {
                            @Override
                            public void onAddSuccess(String id) {
                                review.reviewId = id;
                                review.user = AppRes.getUser();
                                Collections.reverse(reviews);
                                reviews = Review.setReview(reviews, review.reviewId, review);
                                Collections.reverse(reviews);
                                updateReviews();

                                if(review.rating > 0) {
                                    RatingStatsResource.getInstance().editRatingStats(review.rating, review.barId, new GetRatingStatsHandler() {
                                        @Override
                                        public void onRatingStatsLoaded(Map<String, RatingStat> ratingStats) {
                                            appRes.setAllTimeRatingStats(ratingStats);
                                            FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                                            refreshData();
                                            updateStats();

                                            // Lisätään rating lokiin
                                            final Rating rating = new Rating();
                                            rating.barId = bar.barId;
                                            rating.userId = AppRes.getUser().userId;
                                            rating.rating = review.rating;
                                            rating.time = System.currentTimeMillis();
                                            RatingsLogResource.getInstance().addRatingLog(bar.barId, rating);
                                        }
                                    });
                                }
                                UsersResource.getInstance().updateUserKarma(KarmaPoints.REVIEWED_BAR, true);
                            }
                        });
                    }
                });
                reviewDialog.show(getFragmentManager(), "Lisää arvostelu");
            }
        });

        edit_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditBarFragment(bar, false);
            }
        });

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToMapFragment(bar.name);
            }
        });

        return v;
    }

    public void updateBar() {
        if (location != null) {
            distanceText.setText(StringUtils.getDistanceToText(bar.latitude, bar.longitude));
        } else {
            distanceText.setText(getString(R.string.bar_details_map));
        }

        if (bar.nightClub) {
            barType.setText(getString(R.string.nightclub));
        } else {
            barType.setText(getString(R.string.bar_pub));
        }

        if (bar.isFoodPlace)
            foodContent.setVisibility(View.VISIBLE);
        else
            foodContent.setVisibility(View.GONE);
    }

    public void updateBarDetails() {
        ageTitleText.setText(getString(R.string.bar_details_age_no_added));
        ageContainer.setVisibility(View.GONE);
        if (barDetails != null) {
            if (barDetails.ageLimitUpdated != 0) {
                ageTitleText.setText(getString(R.string.bar_details_age));
                ageContainer.setVisibility(View.VISIBLE);
                ageFridayText.setText(getString(R.string.age, String.valueOf(barDetails.ageLimit)));
            } else {
                ageFridayText.setText(getString(R.string.bar_details_no_added));
            }
            if (barDetails.ageLimitSaturdayUpdated != 0) {
                ageTitleText.setText(getString(R.string.bar_details_age));
                ageContainer.setVisibility(View.VISIBLE);
                ageSaturdayText.setText(getString(R.string.age, String.valueOf(barDetails.ageLimitSaturday)));
            } else {
                ageSaturdayText.setText(getString(R.string.bar_details_no_added));
            }

            if (barDetails.entranceUpdated != 0) {
                entranceDateText.setVisibility(View.VISIBLE);
                if (barDetails.jacketIncluded) {
                    entranceText.setText(getString(R.string.price, String.valueOf(barDetails.entrancePrice)));
                } else {
                    String priceText = getString(R.string.price, String.valueOf(barDetails.entrancePrice)) + " + " + getString(R.string.bar_details_jacket);
                    entranceText.setText(priceText);
                }
                String updateText = "(" + StringUtils.getDateText(barDetails.entranceUpdated) + ")";
                entranceDateText.setText(updateText);
            } else {
                entranceDateText.setVisibility(View.GONE);
                entranceText.setText(getString(R.string.bar_details_no_added));
            }
        }
    }

    public void updateDrinks() {
        if (drinks.size() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            Collections.sort(drinks, Drink.customDrinkComparator);
            drinkListView.removeAllViewsInLayout();
            final PriceHolder holder = new PriceHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Drink drink : drinks) {
                if (drink.size != 0 && drink.price != 0) {
                    View cv = inflater.inflate(R.layout.list_item_drink_static, drinkListView, false);
                    holder.name = (TextView) cv.findViewById(R.id.name);
                    holder.size = (TextView) cv.findViewById(R.id.size);
                    holder.price = (TextView) cv.findViewById(R.id.price);

                    holder.name.setText(drink.name);
                    holder.size.setText(getString(R.string.size, String.valueOf(drink.size)));
                    holder.price.setText(getString(R.string.price, String.valueOf(drink.price)));
                    drinkListView.addView(cv);
                }
            }
        }
    }

    public void updateStats() {
        ArrayList<VoteStat> voteStats = new ArrayList<>(allTimeVoteStats.values());
        Collections.sort(voteStats, VoteStat.customVoteComparator);
        int position = 0;
        for (VoteStat voteStat : voteStats) {
            if (voteStat.barId.equals(bar.barId)) {
                position = voteStats.indexOf(voteStat) + 1;
            }
        }

        RatingStat ratingStat = allTimeRatingStats.get(bar.barId);
        if (ratingStat != null && ratingStat.ratingCount > 0) {
            ratingContent.setVisibility(View.VISIBLE);
            ratingText.setText(StringUtils.getRatingText(ratingStat.getRating()));
        } else {
            ratingContent.setVisibility(View.GONE);
        }

        VoteStat voteStat = allTimeVoteStats.get(bar.barId);
        if (voteStat != null && voteStat.voteCount > 0) {
            positionAllTime.setVisibility(View.VISIBLE);
            positionAllTime.setText(getString(R.string.bar_details_position, position));

            averageAgeText.setVisibility(View.VISIBLE);
            String ageText = getString(R.string.average_age) + " " + getString(R.string.age, String.valueOf(voteStat.getAverageAge()));
            averageAgeText.setText(ageText);
            genderBars.setVisibility(View.VISIBLE);
            genderBars.setView(voteStat, getActivity());
        } else {
            positionAllTime.setVisibility(View.GONE);
            averageAgeText.setVisibility(View.GONE);
            genderBars.setVisibility(View.GONE);
        }

        infoContainer.setVisibility(voteStat != null || ratingStat != null ? View.VISIBLE : View.GONE);
    }

    public void updateReviews() {
        reviewList.removeAllViewsInLayout();
        if (reviews.size() == 0) {
            reviewsText.setVisibility(View.GONE);
        } else {
            reviewsText.setVisibility(View.VISIBLE);
            final ReviewHolder holder = new ReviewHolder();
            LayoutInflater commentInflater = LayoutInflater.from(context);
            for (final Review review : reviews) {
                View cv = commentInflater.inflate(R.layout.list_item_review, reviewList, false);

                holder.likesText = (TextView) cv.findViewById(R.id.likesText);
                holder.likeIcon = (IconView) cv.findViewById(R.id.likeIcon);
                holder.reportIcon = (LinearLayout) cv.findViewById(R.id.reportIcon);
                holder.time = (TextView) cv.findViewById(R.id.time);
                holder.messageText = (TextView) cv.findViewById(R.id.messageText);
                holder.ageText = (TextView) cv.findViewById(R.id.ageText);
                holder.sexIcon = (IconView) cv.findViewById(R.id.sexIcon);
                holder.ratingText = (TextView) cv.findViewById(R.id.ratingText);
                holder.ratingContent = (LinearLayout) cv.findViewById(R.id.ratingContent);

                if(review.user != null) {
                    holder.ageText.setVisibility(View.VISIBLE);
                    holder.sexIcon.setVisibility(View.VISIBLE);
                    holder.ageText.setText(StringUtils.getAgeText(review.user.birthday));
                    if (review.user.isMale()) {
                        holder.sexIcon.setText(R.string.icon_male);
                        holder.sexIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_male));
                    } else {
                        holder.sexIcon.setText(R.string.icon_female);
                        holder.sexIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_female));
                    }
                } else {
                    holder.ageText.setVisibility(View.GONE);
                    holder.sexIcon.setVisibility(View.GONE);
                }

                holder.ratingContent.setVisibility(review.rating > 0 ? View.VISIBLE : View.GONE);
                holder.ratingText.setText(String.valueOf(Math.round(review.rating)));

                if (review.usersVoted != null)
                    holder.likesText.setText(String.valueOf(review.usersVoted.size()));
                else
                    holder.likesText.setText("0");

                holder.messageText.setText(review.message);

                holder.time.setText(StringUtils.getDateText(review.time));

                if (review.usersVoted != null && review.usersVoted.contains(AppRes.getUser().userId)) {
                    holder.likeIcon.setAlpha(0.2f);
                    holder.likeIcon.setOnClickListener(null);
                } else {
                    holder.likeIcon.setAlpha(1f);
                    holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Review reviewToSave = review.clone();
                            reviewToSave.usersVoted.add(AppRes.getUser().userId);
                            ReviewsResource.getInstance().editReview(review.barId, reviewToSave, new EditSuccessListener() {
                                @Override
                                public void onEditSuccess() {
                                    reviews = Review.setReview(reviews, reviewToSave.reviewId, reviewToSave);
                                    updateReviews();

                                    UsersResource.getInstance().giveUserKarma(reviewToSave.userId, KarmaPoints.COMMENT_LIKED, true);
                                }
                            });
                        }
                    });
                }

                if(StringUtils.isEmptyString(review.message)) {
                    holder.reportIcon.setVisibility(View.GONE);
                } else {
                    holder.reportIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (review.usersReported != null && review.usersReported.contains(AppRes.getUser().userId)) {
                                InfoDialogFragment reportDialog = InfoDialogFragment.newInstance(getString(R.string.not_participated_title), getString(R.string.confirmation_report_error_desc_comment));
                                reportDialog.show(getFragmentManager(), "olet jo raportoinut tästä");
                            } else {
                                ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(getString(R.string.confirmation_report_comment));
                                confirm_dialog.show(getFragmentManager(), "flägää dialogi");
                                confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                                    @Override
                                    public void onDialogYesButtonClick() {
                                        final Review reviewToSave = review.clone();
                                        reviewToSave.usersReported.add(AppRes.getUser().userId);
                                        if (reviewToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_REVIEW) {
                                            ReviewsResource.getInstance().removeReview(review.barId, reviewToSave.reviewId, new EditSuccessListener() {
                                                @Override
                                                public void onEditSuccess() {
                                                    reviews = Review.setReview(reviews, reviewToSave.reviewId, null);
                                                    updateReviews();
                                                }
                                            });
                                        } else {
                                            ReviewsResource.getInstance().editReview(review.barId, reviewToSave, new EditSuccessListener() {
                                                @Override
                                                public void onEditSuccess() {
                                                    reviews = Review.setReview(reviews, reviewToSave.reviewId, reviewToSave);
                                                    updateReviews();
                                                }
                                            });
                                        }
                                        UsersResource.getInstance().giveUserKarma(reviewToSave.userId, KarmaPoints.COMMENT_REPORTED, false);
                                    }
                                });
                            }
                        }
                    });
                }

                reviewList.addView(cv);
            }
        }
    }

    public class ReviewHolder {
        public TextView likesText;
        public TextView time;
        public IconView likeIcon;
        public LinearLayout reportIcon;
        public TextView messageText;
        public TextView ageText;
        public IconView sexIcon;
        public TextView ratingText;
        public LinearLayout ratingContent;
    }

    public class PriceHolder {
        TextView name;
        TextView size;
        TextView price;
    }
}
