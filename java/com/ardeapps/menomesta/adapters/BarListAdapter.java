package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.location.Location;
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
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
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
    private Map<String, RatingStat> allTimeRatingStats = new HashMap<>();
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

    public void refreshData() {
        AppRes appRes = (AppRes) AppRes.getContext();
        bars = new ArrayList<>(appRes.getBars().values());
        votes = appRes.getVotes();
        userVotes = appRes.getUserVotes();
        allTimeRatingStats = appRes.getAllTimeRatingStats();
        drinks = appRes.getDrinks();
        events = appRes.getEvents();
        location = appRes.getLocation();
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
            cv = inflater.inflate(R.layout.bar_list_item, null);
            adapterFirstTime = true;
        }

        holder.barContainer = (LinearLayout) cv.findViewById(R.id.barContainer);
        holder.bar_name = (TextView) cv.findViewById(R.id.bar_name);
        holder.joinButton = (ImageView) cv.findViewById(R.id.joinButton);
        holder.participateText = (TextView) cv.findViewById(R.id.participate);
        holder.distanceText = (TextView) cv.findViewById(R.id.distanceText);
        holder.ratingBar = (RatingBar) cv.findViewById(R.id.rating);
        holder.bar_type = (TextView) cv.findViewById(R.id.bar_type);
        holder.distanceContent = (LinearLayout) cv.findViewById(R.id.distanceContent);
        holder.foodContent = (LinearLayout) cv.findViewById(R.id.foodContent);
        holder.detailsContainer = (LinearLayout) cv.findViewById(R.id.detailsContainer);
        holder.beerContent = (LinearLayout) cv.findViewById(R.id.beerContent);
        holder.longContent = (LinearLayout) cv.findViewById(R.id.longContent);
        holder.popularContent = (LinearLayout) cv.findViewById(R.id.popularContent);
        holder.starsContent = (LinearLayout) cv.findViewById(R.id.starsContent);
        holder.eventContent = (LinearLayout) cv.findViewById(R.id.eventContent);
        holder.most_popular_text = (TextView) cv.findViewById(R.id.most_popular_text);
        holder.beerText = (TextView) cv.findViewById(R.id.beerText);
        holder.longText = (TextView) cv.findViewById(R.id.longText);
        holder.eventNameText = (TextView) cv.findViewById(R.id.eventNameText);
        holder.eventTimeText = (TextView) cv.findViewById(R.id.eventTimeText);
        holder.votesContent = (LinearLayout) cv.findViewById(R.id.votesContent);
        holder.femaleBar = cv.findViewById(R.id.femaleBar);
        holder.maleBar = cv.findViewById(R.id.maleBar);
        holder.femalePercent = (TextView) cv.findViewById(R.id.femalePercent);
        holder.malePercent = (TextView) cv.findViewById(R.id.malePercent);
        holder.joinContent = (RelativeLayout) cv.findViewById(R.id.joinContent);
        final Bar bar = bars.get(position);

        /** Nämä näkyy aina näytöllä **/
        holder.bar_name.setText(bar.name);

        if (bar.isFoodPlace)
            holder.foodContent.setVisibility(View.VISIBLE);
        else
            holder.foodContent.setVisibility(View.GONE);

        holder.bar_type.setText(bar.nightClub ? ctx.getString(R.string.nightclub) : ctx.getString(R.string.bar_pub));

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

        Event event = Helper.getEventByName(events, bar.name);
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

        // Animaatiot infoteksteille
        if (adapterFirstTime) {
            final Animation in1, in2, in3, in4;
            in1 = new AlphaAnimation(0.0f, 1.0f);
            in2 = new AlphaAnimation(0.0f, 1.0f);
            in3 = new AlphaAnimation(0.0f, 1.0f);
            in4 = new AlphaAnimation(0.0f, 1.0f);
            in1.setDuration(200);
            in1.setStartOffset(200);
            in2.setDuration(200);
            in2.setStartOffset(400);
            in3.setDuration(200);
            in3.setStartOffset(600);
            in4.setDuration(200);
            in4.setStartOffset(800);
            holder.detailsContainer.startAnimation(in1);
            holder.ratingBar.startAnimation(in2);

            if (beer_price > 0)
                holder.beerContent.startAnimation(in3);

            if (long_price > 0)
                holder.longContent.startAnimation(in3);

            if (event != null) {
                holder.eventContent.startAnimation(in4);
            }

            if (bar.barId.equals(most_popular_bar_id) || bar.barId.equals(most_popular_nightclub_id))
                holder.popularContent.startAnimation(in4);

            if (bar.barId.equals(most_rated_bar_id))
                holder.starsContent.startAnimation(in4);
        }

        RatingStat ratingStat = allTimeRatingStats.get(bar.barId);
        if (ratingStat != null) {
            holder.ratingBar.setAlpha(1f);
            holder.ratingBar.setRating((float) ratingStat.getRating());
        } else {
            holder.ratingBar.setAlpha(0.5f);
            holder.ratingBar.setRating(0);
        }

        if (location != null && location.getLatitude() > 0 && location.getLongitude() > 0) {
            holder.distanceText.setText(StringUtils.getDistanceToText(bar.latitude, bar.longitude));
            holder.distanceContent.setVisibility(View.VISIBLE);
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

        // Osallistumisnappi
        final boolean userHasVoted = userVotes.keySet().contains(bar.barId);

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

        holder.barContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToBarDetailsFragment(bar);
            }
        });

        adapterFirstTime = false;
        return cv;
    }

    public void scaleView(View v, float endScale) {
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
        View femaleBar;
        View maleBar;
        TextView femalePercent;
        TextView malePercent;
        RelativeLayout joinContent;
    }
}
