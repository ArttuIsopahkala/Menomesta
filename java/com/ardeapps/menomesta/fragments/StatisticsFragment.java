package com.ardeapps.menomesta.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    ArrayList<VoteStat> allTimeVoteStats;
    ArrayList<VoteStat> thisWeekVoteStats;
    ArrayList<RatingStat> allTimeRatingStats;
    ArrayList<RatingStat> thisWeekRatingStats;
    Map<String, Bar> bars;
    ArrayList<Drink> drinks;
    ArrayList<Drink> beers = new ArrayList<>();
    ArrayList<Drink> longs = new ArrayList<>();
    Context context;
    LinearLayout allTimeStarsListView;
    LinearLayout allTimeVotesListView;
    LinearLayout thisWeekStarsListView;
    LinearLayout thisWeekVotesListView;
    LinearLayout beerListView;
    LinearLayout longListView;
    AppRes appRes;

    public void setStats(Map<String, VoteStat> allTimeVoteStats, Map<String, VoteStat> thisWeekVoteStats,
                         Map<String, RatingStat> allTimeRatingStats, Map<String, RatingStat> thisWeekRatingStats) {
        this.allTimeVoteStats = new ArrayList<>(allTimeVoteStats.values());
        this.thisWeekVoteStats = new ArrayList<>(thisWeekVoteStats.values());
        this.allTimeRatingStats = new ArrayList<>(allTimeRatingStats.values());
        this.thisWeekRatingStats = new ArrayList<>(thisWeekRatingStats.values());
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        drinks = appRes.getDrinks();
        bars = appRes.getBars();
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
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);

        allTimeStarsListView = (LinearLayout) v.findViewById(R.id.allTimeStarsListView);
        allTimeVotesListView = (LinearLayout) v.findViewById(R.id.allTimeVotesListView);
        thisWeekStarsListView = (LinearLayout) v.findViewById(R.id.thisWeekStarsListView);
        thisWeekVotesListView = (LinearLayout) v.findViewById(R.id.thisWeekVotesListView);
        beerListView = (LinearLayout) v.findViewById(R.id.beerListView);
        longListView = (LinearLayout) v.findViewById(R.id.longListView);
        Spinner filterSpinner = (Spinner) v.findViewById(R.id.filterSpinner);
        TextView titleText = (TextView) v.findViewById(R.id.title);

        titleText.setText(getString(R.string.statistics_title));

        ArrayList<String> filters = new ArrayList<>();
        filters.add(getString(R.string.stats_filter_all));
        filters.add(getString(R.string.stats_filter_bars));
        filters.add(getString(R.string.stats_filter_nightclubs));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filters);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArrayAdapter.notifyDataSetChanged();
        filterSpinner.setAdapter(spinnerArrayAdapter);

        beers.clear();
        longs.clear();
        for (Drink drink : drinks) {
            if (drink.name.equals(getString(R.string.prices_beer))) {
                beers.add(drink);
            }
            if (drink.name.equals(getString(R.string.prices_long))) {
                longs.add(drink);
            }
        }

        Collections.sort(beers, Drink.customDrinkComparator);
        Collections.sort(longs, Drink.customDrinkComparator);
        Collections.sort(allTimeVoteStats, VoteStat.customVoteComparator);
        Collections.sort(allTimeRatingStats, RatingStat.customRatingComparator);
        Collections.sort(thisWeekVoteStats, VoteStat.customVoteComparator);
        Collections.sort(thisWeekRatingStats, RatingStat.customRatingComparator);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setListViews(BarType.ALL);
                        break;
                    case 1:
                        setListViews(BarType.BARS);
                        break;
                    case 2:
                        setListViews(BarType.NIGHTCLUBS);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fragment avattu")
                .putContentType(this.getClass().getSimpleName()));

        return v;
    }

    private void setDrinkListView(LinearLayout list, ArrayList<Drink> drinks, BarType barType) {
        list.removeAllViewsInLayout();
        final Holder holder = new Holder();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < drinks.size(); i++) {
            if (i < 5) {
                Drink drink = drinks.get(i);
                View cv = inflater.inflate(R.layout.list_item_stat, list, false);
                holder.barName = (TextView) cv.findViewById(R.id.barName);
                holder.value = (TextView) cv.findViewById(R.id.value);
                holder.starContainer = (LinearLayout) cv.findViewById(R.id.starContainer);
                holder.star_icon = (IconView) cv.findViewById(R.id.star_icon);
                holder.starContainer.setVisibility(View.VISIBLE);
                holder.star_icon.setVisibility(View.GONE);
                holder.barName.setText(bars.get(drink.barId).name);
                holder.value.setText(getString(R.string.price, String.valueOf(drink.price)));
                if (barType == BarType.ALL) {
                    list.addView(cv);
                } else if (barType == BarType.BARS) {
                    if (!bars.get(drink.barId).nightClub) {
                        list.addView(cv);
                    }
                } else if (barType == BarType.NIGHTCLUBS) {
                    if (bars.get(drink.barId).nightClub) {
                        list.addView(cv);
                    }
                }
            }
        }
    }

    private void setVoteListView(LinearLayout list, ArrayList<VoteStat> stats, BarType barType) {
        list.removeAllViewsInLayout();
        final Holder holder = new Holder();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < stats.size(); i++) {
            if (i < 5) {
                VoteStat stat = stats.get(i);
                View cv = inflater.inflate(R.layout.list_item_stat, list, false);
                holder.barName = (TextView) cv.findViewById(R.id.barName);
                holder.value = (TextView) cv.findViewById(R.id.value);
                holder.starContainer = (LinearLayout) cv.findViewById(R.id.starContainer);
                holder.star_icon = (IconView) cv.findViewById(R.id.star_icon);
                holder.starContainer.setVisibility(View.GONE);
                int position = i + 1;
                holder.barName.setText(position + ". " + bars.get(stat.barId).name);
                if (barType == BarType.ALL) {
                    list.addView(cv);
                } else if (barType == BarType.BARS) {
                    if (!bars.get(stat.barId).nightClub) {
                        list.addView(cv);
                    }
                } else if (barType == BarType.NIGHTCLUBS) {
                    if (bars.get(stat.barId).nightClub) {
                        list.addView(cv);
                    }
                }
            }
        }
    }

    private void setRatingListView(LinearLayout list, ArrayList<RatingStat> stats, BarType barType) {
        list.removeAllViewsInLayout();
        final Holder holder = new Holder();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < stats.size(); i++) {
            if (i < 5) {
                RatingStat stat = stats.get(i);
                View cv = inflater.inflate(R.layout.list_item_stat, list, false);
                holder.barName = (TextView) cv.findViewById(R.id.barName);
                holder.value = (TextView) cv.findViewById(R.id.value);
                holder.starContainer = (LinearLayout) cv.findViewById(R.id.starContainer);
                holder.star_icon = (IconView) cv.findViewById(R.id.star_icon);
                holder.starContainer.setVisibility(View.VISIBLE);
                holder.star_icon.setVisibility(View.VISIBLE);
                holder.barName.setText(bars.get(stat.barId).name);
                holder.value.setText(StringUtils.getRatingText(stat.getRating()));
                if (barType == BarType.ALL) {
                    list.addView(cv);
                } else if (barType == BarType.BARS) {
                    if (!bars.get(stat.barId).nightClub) {
                        list.addView(cv);
                    }
                } else if (barType == BarType.NIGHTCLUBS) {
                    if (bars.get(stat.barId).nightClub) {
                        list.addView(cv);
                    }
                }
            }
        }
    }

    private void setListViews(BarType type) {
        setDrinkListView(beerListView, beers, type);
        setDrinkListView(longListView, longs, type);
        setVoteListView(allTimeVotesListView, allTimeVoteStats, type);
        setRatingListView(allTimeStarsListView, allTimeRatingStats, type);
        setVoteListView(thisWeekVotesListView, thisWeekVoteStats, type);
        setRatingListView(thisWeekStarsListView, thisWeekRatingStats, type);
    }

    private enum BarType {
        ALL,
        BARS,
        NIGHTCLUBS
    }

    public class Holder {
        TextView barName;
        TextView value;
        LinearLayout starContainer;
        IconView star_icon;
    }
}
