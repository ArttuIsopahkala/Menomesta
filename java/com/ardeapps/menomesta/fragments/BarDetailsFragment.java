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
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.BarCommentsResource;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RatingsLogResource;
import com.ardeapps.menomesta.resources.RatingsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.CommentHolder;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BarDetailsFragment extends Fragment {

    LinearLayout commentList;
    LinearLayout sexLayout;
    LinearLayout infoContainer;
    LinearLayout sexContainer;
    ArrayList<Comment> barComments;
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
    TextView malePercentText;
    TextView femalePercentText;
    TextView commentsText;
    Button writeCommentButton;
    Button addRating;
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

    AppRes appRes;

    public void setBarComments(ArrayList<Comment> barComments) {
        this.barComments = barComments;
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

        commentList = (LinearLayout) v.findViewById(R.id.commentList);
        sexLayout = (LinearLayout) v.findViewById(R.id.sexLayout);
        infoContainer = (LinearLayout) v.findViewById(R.id.infoContainer);
        sexContainer = (LinearLayout) v.findViewById(R.id.sexContainer);
        ratingContent = (LinearLayout) v.findViewById(R.id.ratingContent);
        edit_bar = (Button) v.findViewById(R.id.edit_bar);
        ratingText = (TextView) v.findViewById(R.id.ratingText);
        title = (TextView) v.findViewById(R.id.title);
        writeCommentButton = (Button) v.findViewById(R.id.addComment);
        positionAllTime = (TextView) v.findViewById(R.id.positionAllTime);
        averageAgeText = (TextView) v.findViewById(R.id.averageAge);
        malePercentText = (TextView) v.findViewById(R.id.malePercent);
        femalePercentText = (TextView) v.findViewById(R.id.femalePercent);
        commentsText = (TextView) v.findViewById(R.id.commentsText);
        addRating = (Button) v.findViewById(R.id.addRating);
        drinkListView = (LinearLayout) v.findViewById(R.id.drinkListView);
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

        title.setText(bar.name);

        updateBar();
        updateBarDetails();
        updateDrinks();
        updateCommentList();
        updateStats();

        writeCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.hasUserSentCommentInMinute(barComments)) {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.error_comment_title), getString(R.string.error_comment_desc));
                    dialog.show(getFragmentManager(), "kommentti lähetetty liian nopeasti");
                } else {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToWriteCommentFragment(new WriteCommentFragment.Listener() {
                        @Override
                        public void onSendClicked(final Comment comment) {
                            BarCommentsResource.getInstance().addBarComment(bar.barId, comment, new AddSuccessListener() {
                                @Override
                                public void onAddSuccess(String id) {
                                    comment.commentId = id;
                                    Collections.reverse(barComments);
                                    barComments = Comment.setComment(barComments, comment.commentId, comment);
                                    Collections.reverse(barComments);
                                    updateCommentList();
                                }
                            });

                            UsersResource.getInstance().updateUserKarma(KarmaPoints.COMMENTED_BAR, true);
                        }
                    });
                }
            }
        });

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NewRatingDialogFragment ratingDialog = NewRatingDialogFragment.newInstance(getString(R.string.bar_details_rate) + " " + bar.name);
                ratingDialog.setListener(new NewRatingDialogFragment.RatingDialogListener() {
                    @Override
                    public void onDialogClose() {
                        ratingDialog.dismiss();
                    }

                    @Override
                    public void onRatingSubmit(final float value) {
                        ratingDialog.dismiss();

                        final Rating rating = new Rating();
                        rating.barId = bar.barId;
                        rating.userId = AppRes.getUser().userId;
                        rating.rating = value;
                        rating.time = System.currentTimeMillis();

                        if (appRes.getUserRatings().keySet().contains(bar.barId)) {
                            InfoDialogFragment ratingFoundDialog = InfoDialogFragment.newInstance(getString(R.string.error_rating_title), getString(R.string.error_rating_desc));
                            ratingFoundDialog.show(getFragmentManager(), "Arvostelu samana päivänä");
                            return;
                        }

                        RatingsResource.getInstance().addRating(bar.barId, rating, new AddSuccessListener() {
                            @Override
                            public void onAddSuccess(String id) {
                                rating.ratingId = id;
                                appRes.setRating(bar.barId, rating.ratingId, rating);

                                RatingStatsResource.getInstance().editRatingStats(value, bar.barId, new GetRatingStatsHandler() {
                                    @Override
                                    public void onRatingStatsLoaded(Map<String, RatingStat> ratingStats) {
                                        appRes.setAllTimeRatingStats(ratingStats);
                                        FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                                        refreshData();
                                        updateStats();

                                        RatingsLogResource.getInstance().addRatingLog(bar.barId, rating);
                                        UsersResource.getInstance().updateUserKarma(KarmaPoints.STARS_ADDED, true);
                                    }
                                });
                            }
                        });
                    }
                });
                ratingDialog.show(getFragmentManager(), "new rating");
            }
        });

        edit_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditBarFragment(bar);
            }
        });

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToMapFragment(bar);
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
                    View cv = inflater.inflate(R.layout.drink_static_list_item, drinkListView, false);
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
            sexContainer.setVisibility(View.VISIBLE);

            long malePercent = voteStat.maleCount * 100 / voteStat.voteCount;
            long femalePercent = 100 - malePercent;
            malePercentText.setText(getString(R.string.percent, String.valueOf(malePercent)));
            femalePercentText.setText(getString(R.string.percent, String.valueOf(femalePercent)));

            sexLayout.removeAllViews();
            View maleBar = new View(getActivity());
            View femaleBar = new View(getActivity());
            femaleBar.setBackgroundColor(ContextCompat.getColor(context, R.color.color_female));
            maleBar.setBackgroundColor(ContextCompat.getColor(context, R.color.color_male));
            femaleBar.setLayoutParams(new LinearLayout.LayoutParams(0, 50, femalePercent / 100f));
            maleBar.setLayoutParams(new LinearLayout.LayoutParams(0, 50, malePercent / 100f));

            sexLayout.addView(femaleBar);
            sexLayout.addView(maleBar);
        } else {
            positionAllTime.setVisibility(View.GONE);
            averageAgeText.setVisibility(View.GONE);
            sexContainer.setVisibility(View.GONE);
        }

        infoContainer.setVisibility(voteStat != null || ratingStat != null ? View.VISIBLE : View.GONE);
    }

    public void updateCommentList() {
        if (barComments.size() == 0) {
            commentsText.setVisibility(View.GONE);
        } else {
            commentList.removeAllViewsInLayout();
            commentsText.setVisibility(View.VISIBLE);
            final CommentHolder holder = new CommentHolder();
            LayoutInflater commentInflater = LayoutInflater.from(context);
            for (final Comment comment : barComments) {
                View cv = commentInflater.inflate(R.layout.comment_list_item, commentList, false);
                // set item content in view
                holder.commentContainer = (LinearLayout) cv.findViewById(R.id.commentContainer);
                holder.replyContainer = (LinearLayout) cv.findViewById(R.id.replyContainer);
                holder.likesText = (TextView) cv.findViewById(R.id.likesText);
                holder.like_icon = (IconView) cv.findViewById(R.id.like_icon);
                holder.report_icon = (LinearLayout) cv.findViewById(R.id.report_icon);
                holder.comment = (TextView) cv.findViewById(R.id.comment);
                holder.time = (TextView) cv.findViewById(R.id.time);

                holder.commentContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.color_secondary));
                holder.replyContainer.setVisibility(View.INVISIBLE);

                if (comment.usersVoted != null)
                    holder.likesText.setText(String.valueOf(comment.usersVoted.size()));
                else
                    holder.likesText.setText("0");

                holder.comment.setText(comment.message);

                holder.time.setText(StringUtils.getDateText(comment.time));

                if (comment.usersVoted != null && comment.usersVoted.contains(AppRes.getUser().userId)) {
                    holder.like_icon.setAlpha(0.2f);
                    holder.like_icon.setOnClickListener(null);
                } else {
                    holder.like_icon.setAlpha(1f);
                    holder.like_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Comment commentToSave = comment.clone();
                            commentToSave.usersVoted.add(AppRes.getUser().userId);
                            BarCommentsResource.getInstance().editBarComment(bar.barId, commentToSave, new EditSuccessListener() {
                                @Override
                                public void onEditSuccess() {
                                    barComments = Comment.setComment(barComments, commentToSave.commentId, commentToSave);
                                    updateCommentList();

                                    UsersResource.getInstance().giveUserKarma(commentToSave.userId, KarmaPoints.COMMENT_LIKED, true);
                                }
                            });
                        }
                    });
                }

                holder.report_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (comment.usersReported != null && comment.usersReported.contains(AppRes.getUser().userId)) {
                            InfoDialogFragment reportDialog = InfoDialogFragment.newInstance(getString(R.string.not_participated_title), getString(R.string.confirmation_report_error_desc_comment));
                            reportDialog.show(getFragmentManager(), "olet jo raportoinut tästä");
                        } else {
                            ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(getString(R.string.confirmation_report_comment));
                            confirm_dialog.show(getFragmentManager(), "flägää dialogi");
                            confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                                @Override
                                public void onDialogYesButtonClick() {
                                    final Comment commentToSave = comment.clone();
                                    commentToSave.usersReported.add(AppRes.getUser().userId);
                                    if (commentToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_BAR_COMMENT) {
                                        BarCommentsResource.getInstance().removeBarComment(bar.barId, commentToSave.commentId, new EditSuccessListener() {
                                            @Override
                                            public void onEditSuccess() {
                                                barComments = Comment.setComment(barComments, commentToSave.commentId, null);
                                                updateCommentList();
                                            }
                                        });
                                    } else {
                                        BarCommentsResource.getInstance().editBarComment(bar.barId, commentToSave, new EditSuccessListener() {
                                            @Override
                                            public void onEditSuccess() {
                                                barComments = Comment.setComment(barComments, commentToSave.commentId, commentToSave);
                                                updateCommentList();
                                            }
                                        });
                                    }
                                    UsersResource.getInstance().giveUserKarma(commentToSave.userId, KarmaPoints.COMMENT_REPORTED, false);
                                }
                            });
                        }
                    }
                });

                commentList.addView(cv);
            }
        }
    }

    public class PriceHolder {
        TextView name;
        TextView size;
        TextView price;
    }
}
