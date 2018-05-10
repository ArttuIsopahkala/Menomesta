package com.ardeapps.menomesta.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.ardeapps.menomesta.objects.Event;
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
import java.util.Map;

public class FbBarDetailsFragment extends Fragment {

    FacebookBarDetails details;
    BarDetails barDetails;
    Bar bar;
    VoteStat voteStat;

    ArrayList<Review> reviews;
    ArrayList<Drink> drinks;
    Location location;
    Context context;

    LinearLayout eventsList;
    LinearLayout drinksList;
    LinearLayout reviewList;
    LinearLayout ageContainer;
    LinearLayout foodContent;
    LinearLayout openContent;
    LinearLayout showAllEventsContent;
    LinearLayout priceContent;
    LinearLayout openTimeTodayContent;
    GenderBars genderBars;

    TextView title;
    TextView barType;
    TextView distanceText;
    TextView positionAllTime;
    TextView eventsText;
    TextView reviewsText;
    TextView ageTitleText;
    TextView emptyMessage;
    TextView aboutText;
    TextView openText;
    TextView openTimeTodayText;
    TextView averageAgeText;
    TextView malePercentText;
    TextView femalePercentText;

    ProgressBar priceBar;
    RatingBar ratingBar;

    Button addReview;
    Button edit_bar;

    IconView map_icon;
    IconView openTimesIcon;
    String distance = "0m";

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

    public void setVoteStat(VoteStat voteStat) {
        this.voteStat = voteStat;
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
        View v = inflater.inflate(R.layout.fragment_fb_bar_details, container, false);

        reviewList = (LinearLayout) v.findViewById(R.id.reviewList);
        drinksList = (LinearLayout) v.findViewById(R.id.drinksList);
        ageContainer = (LinearLayout) v.findViewById(R.id.ageContainer);
        foodContent = (LinearLayout) v.findViewById(R.id.foodContent);
        eventsList = (LinearLayout) v.findViewById(R.id.eventsList);
        openContent = (LinearLayout) v.findViewById(R.id.openContent);
        showAllEventsContent = (LinearLayout) v.findViewById(R.id.showAllEventsContent);
        priceContent = (LinearLayout) v.findViewById(R.id.priceContent);
        openTimeTodayContent = (LinearLayout) v.findViewById(R.id.openTimeTodayContent);

        title = (TextView) v.findViewById(R.id.title);
        positionAllTime = (TextView) v.findViewById(R.id.positionAllTime);
        reviewsText = (TextView) v.findViewById(R.id.reviewsText);
        barType = (TextView) v.findViewById(R.id.barType);
        distanceText = (TextView) v.findViewById(R.id.distanceText);
        ageTitleText = (TextView) v.findViewById(R.id.ageTitleText);
        emptyMessage = (TextView) v.findViewById(R.id.emptyMessage);
        eventsText = (TextView) v.findViewById(R.id.eventsText);
        aboutText = (TextView) v.findViewById(R.id.aboutText);
        openText = (TextView) v.findViewById(R.id.openText);
        openTimeTodayText = (TextView) v.findViewById(R.id.openTimeTodayText);
        averageAgeText = (TextView) v.findViewById(R.id.averageAge);
        malePercentText = (TextView) v.findViewById(R.id.malePercentText);
        femalePercentText = (TextView) v.findViewById(R.id.femalePercentText);

        genderBars = (GenderBars) v.findViewById(R.id.genderBars);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        priceBar = (ProgressBar) v.findViewById(R.id.priceBar);
        edit_bar = (Button) v.findViewById(R.id.editBar);
        addReview = (Button) v.findViewById(R.id.addReview);
        map_icon = (IconView) v.findViewById(R.id.map_icon);
        openTimesIcon = (IconView) v.findViewById(R.id.openTimesIcon);

        Drawable progressDrawable = priceBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(ContextCompat.getColor(context, R.color.color_money), PorterDuff.Mode.SRC_IN);
        priceBar.setProgressDrawable(progressDrawable);
        priceBar.setScaleY(2f);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, R.color.color_text_hint), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(context, R.color.color_star), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.color_star), PorterDuff.Mode.SRC_ATOP);
        }

        updateEvents();
        updateDrinks();
        updateReviews();


        final AppRes appRes = (AppRes) AppRes.getContext();
        final FacebookBarDetails details = FbRes.getBarDetail(bar.barId);

        // Otsikko
        title.setText(details.name);
        // Esittely
        if(!StringUtils.isEmptyString(details.about)) {
            aboutText.setVisibility(View.VISIBLE);
            aboutText.setText(details.about);
        } else {
            aboutText.setVisibility(View.GONE);
        }
        // Etäisyys
        if(appRes.getLocation() != null) {
            if (details.barLocation != null) {
                distanceText.setText(StringUtils.getDistanceToText(details.barLocation));
            } else {
                distanceText.setText(StringUtils.getDistanceToText(bar.latitude, bar.longitude));
            }
        } else {
            distanceText.setText(getString(R.string.bar_details_map));
        }
        // Baari vai yökerho
        barType.setText(getString(bar.nightClub ? R.string.nightclub : R.string.bar_pub));
        // Hintataso
        if(!StringUtils.isEmptyString(details.priceRange) && !details.priceRange.equals("Unspecified")) {
            priceContent.setVisibility(View.VISIBLE);
            final float progress = (float)(details.priceRange.length() - 1) / 3 * 100;
            priceBar.post(new Runnable() {
                @Override
                public void run() {
                    priceBar.setProgress(progress == 0 ? 5 : (int)progress);
                }
            });
        } else {
            priceContent.setVisibility(View.GONE);
        }
        // Aukioloaika
        if(!details.hours.isEmpty()) {
            openContent.setVisibility(View.VISIBLE);
            if (Helper.isOpenToday(details.hours)) {
                openTimeTodayContent.setVisibility(View.VISIBLE);
                openTimeTodayText.setText(Helper.getTodayOpenText(getString(R.string.bar_details_open), details.hours, true));
                if (Helper.isOpenNow(details.hours)) {
                    openText.setText(R.string.bar_details_open_now);
                    openText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_bar_open));
                } else {
                    openText.setText(R.string.bar_details_closed_now);
                    openText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_bar_closed));
                }
            } else {
                openTimeTodayContent.setVisibility(View.GONE);
                openText.setText(R.string.bar_details_closed_today);
                openText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_bar_closed));
            }
            openTimesIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder hoursText = new StringBuilder();
                    for(String hour : Helper.getFacebookHoursList(details.hours)) {
                        hoursText.append(hour).append("\n");
                    }
                    InfoDialogFragment infoDialog = InfoDialogFragment.newInstance(getString(R.string.bar_details_opening_hours), hoursText.toString());
                    infoDialog.show(getFragmentManager(), "aukioloajat");
                }
            });
        } else {
            openContent.setVisibility(View.GONE);
        }

        // Ruokapaikka
        foodContent.setVisibility(bar.isFoodPlace ? View.VISIBLE : View.GONE);
        // Tähdet
        ratingBar.post(new Runnable() {
            @Override
            public void run() {
                ratingBar.setRating(details.overallStarRating);
            }
        });

        // Sijoitus
        positionAllTime.setText(getString(R.string.bar_details_position, Helper.getPositionVoted(FbRes.getBarDetails(), bar.barId)));

        if (voteStat != null && voteStat.voteCount > 0) {
            genderBars.setVisibility(View.VISIBLE);
            genderBars.setView(voteStat, getActivity());
        } else {
            genderBars.setVisibility(View.GONE);
        }

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
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditBarFragment(bar, true);
            }
        });

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng;
                if(details.barLocation != null) {
                    latLng = new LatLng(details.barLocation.latitude, details.barLocation.longitude);
                } else {
                    latLng = new LatLng(bar.latitude, bar.longitude);
                }
                FragmentListeners.getInstance().getFragmentChangeListener().goToMapFragment(details.name);
            }
        });

        return v;
    }

    private void updateEvents() {
        ArrayList<Event> events = FbRes.getEvents(bar.barId);
        if(events.size() == 0) {
            eventsText.setVisibility(View.GONE);
        } else {
            eventsText.setVisibility(View.VISIBLE);
            ArrayList<Event> singleEvent = new ArrayList<>();
            Collections.reverse(events);
            singleEvent.add(events.get(0));
            buildEventsList(singleEvent);
        }
        showAllEventsContent.setVisibility(events.size() > 1 ? View.VISIBLE : View.GONE);
        showAllEventsContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Event> events = FbRes.getEvents(bar.barId);
                buildEventsList(events);
                showAllEventsContent.setVisibility(View.GONE);
            }
        });
    }

    private void buildEventsList(ArrayList<Event> events) {
        eventsList.removeAllViewsInLayout();
        LayoutInflater inf = LayoutInflater.from(context);
        final StaticEventHolder holder = new StaticEventHolder();
        for(final Event event : events) {
            View cv = inf.inflate(R.layout.list_item_static_event, eventsList, false);
            holder.event_name = (TextView) cv.findViewById(R.id.event_name);
            holder.timeText = (TextView) cv.findViewById(R.id.timeText);
            holder.attendeesText = (TextView) cv.findViewById(R.id.attendeesText);
            holder.descriptionText = (TextView) cv.findViewById(R.id.descriptionText);
            holder.timeContent = (LinearLayout) cv.findViewById(R.id.timeContent);
            holder.attendeesContent = (LinearLayout) cv.findViewById(R.id.attendeesContent);

            holder.event_name.setText(event.name);
            holder.timeText.setText(StringUtils.getDateTimeText(event.startTime));

            if (event.description.isEmpty()) {
                holder.descriptionText.setVisibility(View.GONE);
            } else {
                holder.descriptionText.setVisibility(View.VISIBLE);
                int maxLength = 200;
                if(event.description.length() > maxLength) {
                    holder.descriptionText.setText(event.description.substring(0, maxLength) + "...");
                    holder.descriptionText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InfoDialogFragment dialog = InfoDialogFragment.newInstance(event.name, event.description);
                            dialog.show(getFragmentManager(), "description");
                        }
                    });
                } else {
                    holder.descriptionText.setText(event.description);
                }
            }

            int attendees = event.attendingCount;
            if (attendees > 0) {
                if(attendees == 1) {
                    holder.attendeesText.setText(attendees + " " + context.getString(R.string.vote));
                } else holder.attendeesText.setText(attendees + " " + context.getString(R.string.votes));
            } else {
                holder.attendeesText.setText(context.getString(R.string.no_votes));
            }

            eventsList.addView(cv);
        }
    }

    public class StaticEventHolder {
        TextView event_name;
        TextView timeText;
        TextView attendeesText;
        TextView descriptionText;
        LinearLayout timeContent;
        LinearLayout attendeesContent;
    }

    public void updateDrinks() {
        if (drinks.size() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            Collections.sort(drinks, Drink.customDrinkComparator);
            drinksList.removeAllViewsInLayout();
            final PriceHolder holder = new PriceHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Drink drink : drinks) {
                if (drink.size != 0 && drink.price != 0) {
                    View cv = inflater.inflate(R.layout.list_item_drink_static, drinksList, false);
                    holder.name = (TextView) cv.findViewById(R.id.name);
                    holder.size = (TextView) cv.findViewById(R.id.size);
                    holder.price = (TextView) cv.findViewById(R.id.price);

                    holder.name.setText(drink.name);
                    holder.size.setText(getString(R.string.size, String.valueOf(drink.size)));
                    holder.price.setText(getString(R.string.price, String.valueOf(drink.price)));
                    drinksList.addView(cv);
                }
            }
        }
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
