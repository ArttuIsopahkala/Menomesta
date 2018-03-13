package com.ardeapps.menomesta.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.EventListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetEventsHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.EventsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesLogResource;
import com.ardeapps.menomesta.resources.VotesResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class EventsFragment extends Fragment implements EventListAdapter.EventListAdapterListener {

    TextView no_events;
    ListView eventList;
    SwipeRefreshLayout swipeRefresh;
    EventListAdapter adapter;
    ArrayList<Event> events;
    Map<String, Bar> bars;
    Map<String, ArrayList<Vote>> votes;
    Map<String, String> userVotes;
    AppRes appRes;
    TextView addEvent;

    public void update() {
        if (events.size() == 0) {
            no_events.setVisibility(View.VISIBLE);
        } else {
            no_events.setVisibility(View.GONE);

            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event obj1, Event obj2) {
                    if (obj1.startTime < obj2.startTime) {
                        return -1;
                    } else if (obj1.startTime > obj2.startTime) {
                        return 1;
                    } else {
                        return obj1.name.compareTo(obj2.name);
                    }
                }
            });
        }

        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        events = appRes.getEvents();
        bars = appRes.getBars();
        votes = appRes.getVotes();
        userVotes = appRes.getUserVotes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EventListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        eventList = (ListView) v.findViewById(R.id.eventList);
        no_events = (TextView) v.findViewById(R.id.no_events);
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        addEvent = (TextView) v.findViewById(R.id.addEvent);

        eventList.setAdapter(adapter);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToAddEventFragment(null);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                EventsResource.getInstance().getEvents(new GetEventsHandler() {
                    @Override
                    public void onEventsLoaded(ArrayList<Event> events) {
                        appRes.setEvents(events);
                        refreshData();
                        update();
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onRemoveEventClick(final String eventId) {
        EventsResource.getInstance().removeEvent(eventId, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setEvent(eventId, null);

                FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                refreshData();
                update();
            }
        });
    }

    @Override
    public void onReportEventClick(final Event event) {
        final Event eventToSave = event.clone();
        eventToSave.usersReported.add(AppRes.getUser().userId);
        if (eventToSave.usersReported.size() >= ReportCounts.REPORTS_TO_DELETE_EVENT) {
            EventsResource.getInstance().removeEvent(eventToSave.eventId, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setEvent(eventToSave.eventId, null);
                    refreshData();
                    update();
                }
            });
        } else {
            EventsResource.getInstance().editEvent(eventToSave, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    appRes.setEvent(eventToSave.eventId, eventToSave);
                    refreshData();
                    update();

                    UsersResource.getInstance().giveUserKarma(eventToSave.userId, KarmaPoints.EVENT_REPORTED, false);
                }
            });
        }
    }

    @Override
    public void onAddEventVoteClick(final Event event) {
        final Event eventToSave = event.clone();
        eventToSave.usersJoined.add(AppRes.getUser().userId);
        EventsResource.getInstance().editEvent(eventToSave, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setEvent(eventToSave.eventId, eventToSave);

                final Bar bar = Helper.getBarFromAddress(bars.values(), eventToSave.address);
                if (bar != null) {
                    // Käyttäjä on jo osallistunut baariin, älä lisää toista votea
                    if ((DateUtil.isToday(eventToSave.startTime) && StringUtils.isEmptyString(appRes.getUserVotes().get(bar.barId)))
                            || DateUtil.isAfterToday(eventToSave.startTime)) {
                        final Vote vote = new Vote();
                        vote.barId = bar.barId;
                        vote.time = eventToSave.startTime;
                        vote.userId = AppRes.getUser().userId;
                        vote.user = AppRes.getUser();

                        VotesResource.getInstance().addVote(bar.barId, vote, new AddSuccessListener() {
                            @Override
                            public void onAddSuccess(String id) {
                                vote.voteId = id;
                                // Lisätään vote lokaalisti vain, jos tapahtuma on tänään
                                if (DateUtil.isToday(eventToSave.startTime)) {
                                    appRes.setVote(bar.barId, vote.voteId, vote);
                                }
                                VoteStatsResource.getInstance().editVoteStats(bar.barId, new GetVoteStatsHandler() {
                                    @Override
                                    public void onVoteStatsLoaded(Map<String, VoteStat> allTimeVoteStats) {
                                        appRes.setAllTimeVoteStats(allTimeVoteStats);
                                        refreshData();
                                        update();
                                        FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();

                                        VotesLogResource.getInstance().addVoteLog(bar.barId, vote);
                                    }
                                });
                            }
                        });
                    } else {
                        refreshData();
                        update();
                    }
                } else {
                    refreshData();
                    update();
                    FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                }
                UsersResource.getInstance().updateUserKarma(KarmaPoints.VOTED, true);
            }
        });
    }

    @Override
    public void onRemoveEventVoteClick(final Event event) {
        final Event eventToSave = event.clone();
        eventToSave.usersJoined.remove(AppRes.getUser().userId);
        EventsResource.getInstance().editEvent(eventToSave, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setEvent(eventToSave.eventId, eventToSave);

                // Onko käyttäjällä vote?
                final Bar bar = Helper.getBarFromAddress(bars.values(), eventToSave.address);
                if (bar != null) {
                    // Poistetaan vote, jos tapahtuma on tänään eli vote on tänään
                    if (DateUtil.isToday(eventToSave.startTime) && !StringUtils.isEmptyString(appRes.getUserVotes().get(bar.barId))) {
                        String voteIdToRemove = userVotes.get(bar.barId);
                        for (final Vote vote : votes.get(bar.barId)) {
                            if (vote.voteId.equals(voteIdToRemove)) {
                                VotesResource.getInstance().removeVote(bar.barId, vote.voteId, new EditSuccessListener() {
                                    @Override
                                    public void onEditSuccess() {
                                        appRes.setVote(bar.barId, vote.voteId, null);

                                        refreshData();
                                        update();
                                        FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                                    }
                                });
                                break;
                            }
                        }
                    } else {
                        refreshData();
                        update();
                    }
                } else {
                    refreshData();
                    update();
                    FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                }
                UsersResource.getInstance().updateUserKarma(KarmaPoints.VOTED, false);
            }
        });
    }

    @Override
    public void onEventLocationClick(final String address) {
        Bar bar = Helper.getBarFromAddress(bars.values(), address);
        if (bar != null) {
            LatLng position = new LatLng(bar.latitude, bar.longitude);
            FragmentListeners.getInstance().getFragmentChangeListener().goToShowLocationFragment(position, address);
        } else {
            LatLng position = null;
            if (Helper.getLocationFromAddress(address) != null) {
                position = Helper.getLocationFromAddress(address + " " + AppRes.getCity());
            }
            if (position != null) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToShowLocationFragment(position, address);
            } else {
                InfoDialogFragment noFoundDialog = InfoDialogFragment.newInstance(address, getString(R.string.map_no_location_found_event));
                noFoundDialog.show(getFragmentManager(), "Sijaintia ei löydy");
            }
        }
    }
}
