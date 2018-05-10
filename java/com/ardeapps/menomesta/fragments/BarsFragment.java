package com.ardeapps.menomesta.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.BarListAdapter;
import com.ardeapps.menomesta.adapters.FilterSpinnerAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.GetBarListDataHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.EventVotesResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesLogResource;
import com.ardeapps.menomesta.resources.VotesResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BarsFragment extends Fragment implements BarListAdapter.BarListAdapterListener {

    ListView barList;
    TextView no_bars;
    Spinner filterSpinner;
    SwipeRefreshLayout swipeRefresh;
    BarListAdapter adapter;
    ArrayList<Bar> bars;
    Map<String, Bar> barsMap = new HashMap<>();
    Map<String, ArrayList<Vote>> votes;
    Map<String, String> userVotes;
    Map<String, VoteStat> allTimeVoteStats = new HashMap<>();
    Map<String, RatingStat> allTimeRatingStats = new HashMap<>();
    ArrayList<Event> events;
    LinearLayout menuButtons;
    LinearLayout statisticsButton;
    ArrayList<String> filters;
    FilterSpinnerAdapter spinnerAdapter;
    AppRes appRes;
    Location location;

    public void update() {
        String most_rated_bar_id = "";
        String most_popular_bar_id = "";
        String most_popular_nightclub_id = "";

        if (bars.size() == 0) {
            no_bars.setVisibility(View.VISIBLE);
            menuButtons.setVisibility(View.GONE);
        } else {
            no_bars.setVisibility(View.GONE);
            menuButtons.setVisibility(View.VISIBLE);

            int filterPosition = spinnerAdapter.getSelectedIndex();
            switch (filterPosition) {
                case 0:
                    Collections.sort(bars, new sortToday());
                    break;
                case 1:
                    Collections.sort(bars, new sortName());
                    break;
                case 2:
                    Collections.sort(bars, new sortName());
                    Collections.reverse(bars);
                    break;
                case 3:
                    if (location != null)
                        Collections.sort(bars, new sortDistance());
                    else
                        Logger.toast(R.string.filter_distance_not_found);
                    break;
                case 4:
                    Collections.sort(bars, new sortStars());
                    break;
                case 5:
                    Collections.sort(bars, new sortParticipate());
                    break;
                case 6:
                    Collections.sort(bars, new sortFemale());
                    break;
                case 7:
                    Collections.sort(bars, new sortMale());
                    break;
            }

            ArrayList<VoteStat> voteStats = new ArrayList<>(allTimeVoteStats.values());
            if (allTimeRatingStats.values().size() > 0) {
                Collections.sort(new ArrayList<>(allTimeRatingStats.values()), RatingStat.customRatingComparator);
                most_rated_bar_id = new ArrayList<>(allTimeRatingStats.values()).get(0).barId;
            }

            Collections.sort(voteStats, VoteStat.customVoteComparator);
            double most_votes_nightclub = 0;
            double most_votes_bar = 0;
            for (VoteStat voteStat : voteStats) {
                if (voteStat.voteCount > 0) {
                    for (Bar bar : bars) {
                        if (voteStat.barId.equals(bar.barId)) {
                            if (most_votes_nightclub == 0 || most_votes_bar == 0) {
                                if (bar.nightClub && voteStat.voteCount > most_votes_nightclub) {
                                    most_popular_nightclub_id = voteStat.barId;
                                    most_votes_nightclub = voteStat.voteCount;
                                }
                                if (!bar.nightClub && voteStat.voteCount > most_votes_bar) {
                                    most_popular_bar_id = voteStat.barId;
                                    most_votes_bar = voteStat.voteCount;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        adapter.refreshData();
        adapter.setBars(bars);
        adapter.setTopBars(most_popular_bar_id, most_rated_bar_id, most_popular_nightclub_id);
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        bars = new ArrayList<>(appRes.getBars().values());
        barsMap = appRes.getBars();
        votes = appRes.getVotes();
        userVotes = appRes.getUserVotes();
        allTimeVoteStats = appRes.getAllTimeVoteStats();
        allTimeRatingStats = appRes.getAllTimeRatingStats();
        events = FbRes.getEvents();
        location = appRes.getLocation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new BarListAdapter(getActivity());
        adapter.setListener(this);

        filters = new ArrayList<>();
        filters.add(getString(R.string.filter_today));
        filters.add(getString(R.string.filter_alphabetic));
        filters.add(getString(R.string.filter_alphabetic_reverse));
        filters.add(getString(R.string.filter_distance));
        filters.add(getString(R.string.filter_stars));
        filters.add(getString(R.string.filter_participate));
        filters.add(getString(R.string.filter_female));
        filters.add(getString(R.string.filter_male));
        spinnerAdapter = new FilterSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bars, container, false);

        barList = (ListView) v.findViewById(R.id.todayList);
        no_bars = (TextView) v.findViewById(R.id.no_bars);
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        menuButtons = (LinearLayout) v.findViewById(R.id.menuButtons);
        filterSpinner = (Spinner) v.findViewById(R.id.filterSpinner);
        statisticsButton = (LinearLayout) v.findViewById(R.id.statisticsButton);

        barList.setAdapter(adapter);

        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Answers.getInstance().logCustom(new CustomEvent("Baarilista järjestelty")
                            .putCustomAttribute("Valinta", position));
                }

                spinnerAdapter.setSelection(position);
                //sortBars(position);
                barList.post(new Runnable() {
                    @Override
                    public void run() {
                        barList.smoothScrollToPosition(0);
                    }
                });

                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToStatisticsFragment();
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                Helper.getBarListData(new GetBarListDataHandler() {
                    @Override
                    public void onBarListDataLoaded() {
                        refreshData();
                        update();
                    }
                });
            }
        });

        return v;
    }


    @Override
    public void onAddVoteClick(final Bar bar) {
        // Jos tapahtuma on tänään, lisätään tapahtumavote
        final Event event = Helper.getEventByBarId(events, bar.barId);
        if(event != null && DateUtil.isToday(event.startTime)) {
            final EventVote eventVote = new EventVote();
            eventVote.eventId = event.eventId;
            eventVote.barId = event.barId;
            eventVote.userId = AppRes.getUser().userId;
            eventVote.time = System.currentTimeMillis();
            eventVote.eventEndTime = event.endTime;
            eventVote.user = AppRes.getUser();

            EventVotesResource.getInstance().addEventVote(event.eventId, eventVote, new AddSuccessListener() {
                @Override
                public void onAddSuccess(String id) {
                    eventVote.voteId = id;
                    appRes.setEventVote(event.eventId, eventVote.voteId, eventVote);

                    Vote vote = new Vote(eventVote);
                    onVoteAdded(vote);
                }
            });
        } else {
            final String barId = bar.barId;
            final Vote vote = new Vote();
            vote.barId = barId;
            vote.time = System.currentTimeMillis();
            vote.userId = AppRes.getUser().userId;
            vote.user = AppRes.getUser();
            VotesResource.getInstance().addVote(barId, vote, new AddSuccessListener() {
                @Override
                public void onAddSuccess(String id) {
                    vote.voteId = id;
                    appRes.setVote(barId, vote.voteId, vote);

                    onVoteAdded(vote);
                }
            });
        }
    }

    private void onVoteAdded(final Vote vote) {
        VoteStatsResource.getInstance().editVoteStat(vote.barId, new GetVoteStatsHandler() {
            @Override
            public void onVoteStatsLoaded(Map<String, VoteStat> allTimeVoteStats) {
                appRes.setAllTimeVoteStats(allTimeVoteStats);
                refreshData();
                update();

                VotesLogResource.getInstance().addVoteLog(vote.barId, vote);
                UsersResource.getInstance().updateUserKarma(KarmaPoints.VOTED, true);
            }
        });
    }

    private int compareBars(Bar obj1, Bar obj2) {
        //2. ne missä eniten osallistujia
        int obj1Votes = 0;
        int obj2Votes = 0;
        boolean obj1Event = false;
        boolean obj2Event = false;

        for (Event event : events) {
            if (DateUtil.isToday(event.startTime)) {
                Bar bar = barsMap.get(event.barId);
                if (bar.barId.equals(obj1.barId)) {
                    obj1Event = true;
                    obj1Votes = event.attendingCount;
                }

                if (bar.barId.equals(obj2.barId)) {
                    obj2Event = true;
                    obj2Votes = event.attendingCount;
                }
            }

            if (obj1Event && obj2Event)
                break;
        }

        if (votes.get(obj1.barId) != null) {
            obj1Votes += votes.get(obj1.barId).size();
        }
        if (votes.get(obj2.barId) != null) {
            obj2Votes += votes.get(obj2.barId).size();
        }

        if (obj1Votes > obj2Votes) {
            return -1;
        }
        if (obj1Votes < obj2Votes) {
            return 1;
        }
        if (obj1Event && !obj2Event) {
            return -1;
        }
        if (!obj1Event && obj2Event) {
            return 1;
        }
        return compareNames(obj1, obj2);
    }

    private int compareNames(Bar obj1, Bar obj2) {
        return obj1.name.compareTo(obj2.name);
    }

    private class sortToday implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            //1. ne baarit mihin osallistuu
            if (userVotes.size() > 0) {
                boolean obj1vote = userVotes.keySet().contains(obj1.name);
                boolean obj2vote = userVotes.keySet().contains(obj2.name);

                if (obj1vote && obj2vote) {
                    return compareBars(obj1, obj2);
                }
                if (obj1vote) {
                    return -1;
                }
                if (obj2vote) {
                    return 1;
                }
                return compareBars(obj1, obj2);
            }
            return compareBars(obj1, obj2);
        }
    }

    private class sortName implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            return compareNames(obj1, obj2);
        }
    }

    private class sortDistance implements Comparator<Bar> {

        @Override
        public int compare(Bar obj1, Bar obj2) {
            float[] results1 = new float[1];
            float[] results2 = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    obj1.latitude, obj1.longitude, results1);
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    obj2.latitude, obj2.longitude, results2);
            float meters1 = results1[0];
            float meters2 = results2[0];
            if (meters1 == meters2) {
                return compareNames(obj1, obj2);
            }
            if (meters1 < meters2) {
                return -1;
            }
            if (meters1 > meters2) {
                return 1;
            }
            return compareNames(obj1, obj2);
        }
    }

    private class sortStars implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            double obj1rating = allTimeRatingStats.get(obj1.barId) != null ? allTimeRatingStats.get(obj1.barId).getRating() : 0;
            double obj2rating = allTimeRatingStats.get(obj2.barId) != null ? allTimeRatingStats.get(obj2.barId).getRating() : 0;

            if (obj1rating == obj2rating) {
                return compareNames(obj1, obj2);
            }
            if (obj1rating > obj2rating) {
                return -1;
            }
            if (obj1rating < obj2rating) {
                return 1;
            }
            return compareNames(obj1, obj2);
        }
    }

    private class sortParticipate implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            long obj1Votes = allTimeVoteStats.get(obj1.barId) != null ? allTimeVoteStats.get(obj1.barId).voteCount : 0;
            long obj2Votes = allTimeVoteStats.get(obj2.barId) != null ? allTimeVoteStats.get(obj2.barId).voteCount : 0;

            if (obj1Votes == obj2Votes) {
                return compareNames(obj1, obj2);
            }
            if (obj1Votes > obj2Votes) {
                return -1;
            }
            if (obj1Votes < obj2Votes) {
                return 1;
            }
            return compareNames(obj1, obj2);
        }
    }

    private class sortFemale implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            long obj1Females = allTimeVoteStats.get(obj1.barId) != null ? allTimeVoteStats.get(obj1.barId).femaleCount : 0;
            long obj2Females = allTimeVoteStats.get(obj2.barId) != null ? allTimeVoteStats.get(obj2.barId).femaleCount : 0;
            long voteCount1 = allTimeVoteStats.get(obj1.barId) != null ? allTimeVoteStats.get(obj1.barId).voteCount : 0;
            long voteCount2 = allTimeVoteStats.get(obj2.barId) != null ? allTimeVoteStats.get(obj2.barId).voteCount : 0;

            float female1Percent = 0;
            float female2Percent = 0;
            if (voteCount1 != 0)
                female1Percent = obj1Females * 100 / voteCount1;
            if (voteCount2 != 0)
                female2Percent = obj2Females * 100 / voteCount2;

            if (female1Percent == female2Percent) {
                return compareNames(obj1, obj2);
            }
            if (female1Percent > female2Percent) {
                return -1;
            }
            if (female1Percent < female2Percent) {
                return 1;
            }
            return compareNames(obj1, obj2);
        }
    }

    private class sortMale implements Comparator<Bar> {
        @Override
        public int compare(Bar obj1, Bar obj2) {
            long obj1males = allTimeVoteStats.get(obj1.barId) != null ? allTimeVoteStats.get(obj1.barId).maleCount : 0;
            long obj2males = allTimeVoteStats.get(obj2.barId) != null ? allTimeVoteStats.get(obj2.barId).maleCount : 0;
            long voteCount1 = allTimeVoteStats.get(obj1.barId) != null ? allTimeVoteStats.get(obj1.barId).voteCount : 0;
            long voteCount2 = allTimeVoteStats.get(obj2.barId) != null ? allTimeVoteStats.get(obj2.barId).voteCount : 0;

            float male1Percent = 0;
            float male2Percent = 0;
            if (voteCount1 != 0)
                male1Percent = obj1males * 100 / voteCount1;
            if (voteCount2 != 0)
                male2Percent = obj2males * 100 / voteCount2;

            if (male1Percent == male2Percent) {
                return compareNames(obj1, obj2);
            }
            if (male1Percent > male2Percent) {
                return -1;
            }
            if (male1Percent < male2Percent) {
                return 1;
            }
            return compareNames(obj1, obj2);
        }
    }
}
