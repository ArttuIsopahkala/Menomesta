package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BarListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public BarListAdapterListener mListener = null;
    private ArrayList<Bar> bars = new ArrayList<>();
    private Map<String, ArrayList<Vote>> votes = new HashMap<>();
    private Map<String, String> userVotes = new HashMap<>();
    private Map<String, String> userEventVotes = new HashMap<>();
    private Map<String, RatingStat> allTimeRatingStats = new HashMap<>();
    private Map<String, FacebookBarDetails> facebookBarDetails = new HashMap<>();
    private ArrayList<Drink> drinks = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private Context ctx;
    private boolean adapterFirstTime;
    private String most_popular_bar_id;
    private String most_rated_bar_id;
    private String most_popular_nightclub_id;
    private Location location;

    public BarListAdapter(Context ctx) { // Activity
        this.ctx = ctx;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(BarListAdapterListener l) {
        mListener = l;
    }

    public void setTopBars(String most_popular_bar_id, String most_rated_bar_id, String most_popular_nightclub_id) {
        this.most_popular_bar_id = most_popular_bar_id;
        this.most_rated_bar_id = most_rated_bar_id;
        this.most_popular_nightclub_id = most_popular_nightclub_id;
    }

    public void setBars(ArrayList<Bar> bars) {
        this.bars = bars;
    }

    public void refreshData() {
        AppRes appRes = (AppRes) AppRes.getContext();
        votes = appRes.getVotes();
        userVotes = appRes.getUserVotes();
        allTimeRatingStats = appRes.getAllTimeRatingStats();
        drinks = appRes.getDrinks();
        events = FbRes.getEvents();
        location = appRes.getLocation();
        userEventVotes = appRes.getUserEventVotes();
        facebookBarDetails = FbRes.getBarDetails();
        // NullPointer if(appRes.getBars().get(details.barId).nightClub) baaria ei löydy
        /*ArrayList<FacebookBarDetails> nightclubs = new ArrayList<>();
        ArrayList<FacebookBarDetails> pubs = new ArrayList<>();
        for(FacebookBarDetails details : facebookBarDetails.values()) {
            if(appRes.getBars().get(details.barId).nightClub) {
                nightclubs.add(details);
            } else {
                pubs.add(details);
            }
        }
        this.most_popular_bar_id = Collections.max(pubs, FacebookBarDetails.wereHereComparator).barId;
        this.most_popular_nightclub_id = Collections.max(nightclubs, FacebookBarDetails.wereHereComparator).barId;
        this.most_rated_bar_id = Collections.max(facebookBarDetails.values(), FacebookBarDetails.starsComparator).barId;*/
    }

    @Override
    public int getCount() {
        return bars.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View cv, ViewGroup parent) {
        final Holder holder = new Holder();
        if (cv == null) {
            cv = inflater.inflate(R.layout.list_item_bar, null);
            adapterFirstTime = true;
        }

        holder.barContainer = (LinearLayout) cv.findViewById(R.id.barContainer);
        holder.distanceContent = (LinearLayout) cv.findViewById(R.id.distanceContent);
        holder.foodContent = (LinearLayout) cv.findViewById(R.id.foodContent);
        holder.detailsContainer = (LinearLayout) cv.findViewById(R.id.detailsContainer);
        holder.beerContent = (LinearLayout) cv.findViewById(R.id.beerContent);
        holder.longContent = (LinearLayout) cv.findViewById(R.id.longContent);
        holder.popularContent = (LinearLayout) cv.findViewById(R.id.popularContent);
        holder.starsContent = (LinearLayout) cv.findViewById(R.id.starsContent);
        holder.eventContent = (LinearLayout) cv.findViewById(R.id.eventContent);
        holder.openContent = (LinearLayout) cv.findViewById(R.id.openContent);
        holder.votesContent = (LinearLayout) cv.findViewById(R.id.votesContent);
        holder.joinContent = (RelativeLayout) cv.findViewById(R.id.joinContent);

        holder.femaleBar = cv.findViewById(R.id.femaleBar);
        holder.maleBar = cv.findViewById(R.id.maleBar);
        holder.joinButton = (ImageView) cv.findViewById(R.id.joinButton);
        holder.ratingBar = (RatingBar) cv.findViewById(R.id.rating);

        holder.bar_type = (TextView) cv.findViewById(R.id.bar_type);
        holder.most_popular_text = (TextView) cv.findViewById(R.id.most_popular_text);
        holder.beerText = (TextView) cv.findViewById(R.id.beerText);
        holder.longText = (TextView) cv.findViewById(R.id.longText);
        holder.eventNameText = (TextView) cv.findViewById(R.id.eventNameText);
        holder.eventTimeText = (TextView) cv.findViewById(R.id.eventTimeText);
        holder.participateText = (TextView) cv.findViewById(R.id.participate);
        holder.distanceText = (TextView) cv.findViewById(R.id.distanceText);
        holder.bar_name = (TextView) cv.findViewById(R.id.bar_name);
        holder.femalePercent = (TextView) cv.findViewById(R.id.femalePercentText);
        holder.malePercent = (TextView) cv.findViewById(R.id.malePercentText);
        holder.openText = (TextView) cv.findViewById(R.id.openText);
        holder.openTimeText = (TextView) cv.findViewById(R.id.openTimeTodayText);
        holder.closedTodayText = (TextView) cv.findViewById(R.id.closedTodayText);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(ctx, R.color.color_text_hint), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(ctx, R.color.color_star), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(ctx, R.color.color_star), PorterDuff.Mode.SRC_ATOP);
        }

        final Bar bar = bars.get(position);
        final FacebookBarDetails details = facebookBarDetails.get(bar.barId);

        /** Nämä näkyy aina näytöllä **/
        holder.bar_name.setText(details != null ? details.name : bar.name);

        if (bar.isFoodPlace)
            holder.foodContent.setVisibility(View.VISIBLE);
        else
            holder.foodContent.setVisibility(View.GONE);

        holder.bar_type.setText(bar.nightClub ? ctx.getString(R.string.nightclub) : ctx.getString(R.string.bar_pub));

        // Aukioloaika
        if(details != null) {
            if (Helper.isOpenToday(details.hours)) {
                holder.openContent.setVisibility(View.VISIBLE);
                holder.closedTodayText.setVisibility(View.GONE);
                if (Helper.isOpenNow(details.hours)) {
                    holder.openTimeText.setText(Helper.getTodayOpenText(ctx.getString(R.string.bar_details_open), details.hours, true));
                    holder.openText.setText(R.string.bar_details_open_now);
                    holder.openText.setTextColor(ContextCompat.getColor(ctx, R.color.color_bar_open));
                } else {
                    holder.openTimeText.setText(Helper.getTodayOpenText(ctx.getString(R.string.bar_details_will_open), details.hours, false));
                    holder.openText.setText(R.string.bar_details_closed_now);
                    holder.openText.setTextColor(ContextCompat.getColor(ctx, R.color.color_bar_closed));
                }
            } else {
                holder.closedTodayText.setVisibility(details.hours.isEmpty() ? View.GONE : View.VISIBLE);
                holder.openContent.setVisibility(View.GONE);
            }
        } else {
            holder.closedTodayText.setVisibility(View.GONE);
            holder.openContent.setVisibility(View.GONE);
        }

        double beer_price = 0;
        double long_price = 0;
        for (Drink drink : drinks) {
            if (drink.barId.equals(bar.barId)) {
                if (drink.name.equals(ctx.getString(R.string.prices_beer))) {
                    beer_price = drink.price;
                }
                if (drink.name.equals(ctx.getString(R.string.prices_long))) {
                    long_price = drink.price;
                }
                if (beer_price > 0 && long_price > 0)
                    break;
            }
        }

        if (beer_price > 0) {
            String beerText = ctx.getString(R.string.prices_beer) + " " + ctx.getString(R.string.price, String.valueOf(beer_price));
            holder.beerText.setText(beerText);
            holder.beerContent.setVisibility(View.VISIBLE);
        } else
            holder.beerContent.setVisibility(View.GONE);

        if (long_price > 0) {
            String longText = ctx.getString(R.string.prices_long) + " " + ctx.getString(R.string.price, String.valueOf(long_price));
            holder.longText.setText(longText);
            holder.longContent.setVisibility(View.VISIBLE);
        } else
            holder.longContent.setVisibility(View.GONE);

        // Aseta suosituin baari / yökerho tekstit
        if (bar.barId.equals(most_popular_nightclub_id) || bar.barId.equals(most_popular_bar_id)) {
            if (bar.barId.equals(most_popular_bar_id))
                holder.most_popular_text.setText(ctx.getString(R.string.most_popular_pub));

            if (bar.barId.equals(most_popular_nightclub_id))
                holder.most_popular_text.setText(ctx.getString(R.string.most_popular_nightclub));
            holder.popularContent.setVisibility(View.VISIBLE);
        } else {
            holder.popularContent.setVisibility(View.GONE);
        }

        // Aseta parhaat arvostelut tekstit
        if (bar.barId.equals(most_rated_bar_id))
            holder.starsContent.setVisibility(View.VISIBLE);
        else
            holder.starsContent.setVisibility(View.GONE);

        Event event = Helper.getEventByBarId(events, bar.barId);
        if (event == null) {
            holder.eventContent.setVisibility(View.GONE);
        } else {
            holder.eventContent.setVisibility(View.VISIBLE);
            if (DateUtil.isOnThisWeek(event.startTime)) {
                holder.eventNameText.setVisibility(View.VISIBLE);
                holder.eventNameText.setText(event.name);
                holder.eventTimeText.setText(StringUtils.getDateTimeText(event.startTime));
            } else {
                holder.eventNameText.setVisibility(View.GONE);
                String timeText = ctx.getString(R.string.events_coming) + " " + StringUtils.getDateText(event.startTime, true, true);
                holder.eventTimeText.setText(timeText);
            }
        }

        RatingStat ratingStat = allTimeRatingStats.get(bar.barId);
        float stars = details != null ? details.overallStarRating : ratingStat != null ? (float)ratingStat.getRating() : 0f;
        holder.ratingBar.setAlpha(stars > 0 ? 1f : 0.5f);
        holder.ratingBar.setRating(stars);

        if (location != null) {
            holder.distanceContent.setVisibility(View.VISIBLE);
            if(details != null && details.barLocation != null) {
                holder.distanceText.setText(StringUtils.getDistanceToText(details.barLocation));
            } else {
                holder.distanceText.setText(StringUtils.getDistanceToText(bar.latitude, bar.longitude));
            }
        } else {
            holder.distanceContent.setVisibility(View.GONE);
        }

        int voteCount = votes.get(bar.barId) != null ? votes.get(bar.barId).size() : 0;

        // Osallistujat
        if (voteCount != 0) {
            if (voteCount == 1) {
                holder.participateText.setText(voteCount + " " + ctx.getString(R.string.vote));
            } else holder.participateText.setText(voteCount + " " + ctx.getString(R.string.votes));
        } else {
            holder.participateText.setText(ctx.getString(R.string.vote_here));
        }

        // Käyttäjä osallistunut baariin tänään tai tapahtumaan, joka on tänään
        final boolean userHasVoted = userVotes.containsKey(bar.barId) || (event != null && DateUtil.isToday(event.startTime) && userEventVotes.containsKey(event.eventId));

        if (userHasVoted) {
            holder.votesContent.setVisibility(View.VISIBLE);
            holder.joinContent.setVisibility(View.GONE);

            int maleCount = 0;
            for (Vote vote : votes.get(bar.barId)) {
                if (vote.user != null && vote.user.isMale()) {
                    maleCount++;
                }
            }

            long malePercent = maleCount * 100 / voteCount;
            long femalePercent = 100 - malePercent;
            holder.malePercent.setText(AppRes.getContext().getString(R.string.percent, String.valueOf(malePercent)));
            holder.femalePercent.setText(AppRes.getContext().getString(R.string.percent, String.valueOf(femalePercent)));
            scaleView(holder.femaleBar, femalePercent);
            scaleView(holder.maleBar, malePercent);
        } else {
            holder.votesContent.setVisibility(View.GONE);
            holder.joinContent.setVisibility(View.VISIBLE);
            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mListener.onAddVoteClick(bar);
                }
            });
        }

        // Animaatiot infoteksteille
        if (adapterFirstTime) {
            fadeIn(holder.detailsContainer, 200);
            fadeIn(holder.ratingBar, 400);

            if (beer_price > 0)
                fadeIn(holder.beerContent, 600);

            if (long_price > 0)
                fadeIn(holder.longContent, 600);

            if (event != null)
                fadeIn(holder.eventContent, 800);

            if (bar.barId.equals(most_popular_bar_id) || bar.barId.equals(most_popular_nightclub_id))
                fadeIn(holder.popularContent, 800);

            if (bar.barId.equals(most_rated_bar_id))
                fadeIn(holder.starsContent, 800);
        }

        holder.barContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToBarDetailsFragment(bar);
            }
        });

        adapterFirstTime = false;
        return cv;
    }

    private void fadeIn(View view, long offset) {
        final Animation fadeIn;
        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        fadeIn.setStartOffset(offset);
        view.startAnimation(fadeIn);
    }

    private void scaleView(View v, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f,
                0, endScale / 10,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f);
        anim.setFillAfter(true);
        anim.setDuration(400);
        v.startAnimation(anim);
    }

    public interface BarListAdapterListener {
        void onAddVoteClick(Bar bar);
    }

    public class Holder {
        TextView bar_name;
        TextView distanceText;
        TextView participateText;
        ImageView joinButton;
        LinearLayout barContainer;
        RatingBar ratingBar;
        TextView eventNameText;
        TextView eventTimeText;
        TextView bar_type;
        TextView most_popular_text;
        TextView beerText;
        TextView longText;
        LinearLayout distanceContent;
        LinearLayout foodContent;
        LinearLayout detailsContainer;
        LinearLayout beerContent;
        LinearLayout longContent;
        LinearLayout popularContent;
        LinearLayout starsContent;
        LinearLayout eventContent;
        LinearLayout votesContent;
        LinearLayout openContent;
        View femaleBar;
        View maleBar;
        TextView femalePercent;
        TextView malePercent;
        RelativeLayout joinContent;
        TextView openText;
        TextView openTimeText;
        TextView closedTodayText;
    }
}
