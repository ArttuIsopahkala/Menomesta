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
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.adapters.EventListAdapter;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.GetEventVotesHandler;
import com.ardeapps.menomesta.handlers.GetFacebookEventsHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.EventVotesResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesLogResource;
import com.ardeapps.menomesta.services.FacebookService;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventsFragment extends Fragment implements EventListAdapter.EventListAdapterListener {

    TextView no_events;
    ListView eventList;
    SwipeRefreshLayout swipeRefresh;
    EventListAdapter adapter;
    ArrayList<Event> events;
    Map<String, ArrayList<EventVote>> eventVotes = new HashMap<>();
    Map<String, String> userEventVotes = new HashMap<>();
    AppRes appRes;

    public void update() {
        no_events.setVisibility(events.size() == 0 ? View.VISIBLE : View.GONE);

        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        events = FbRes.getEvents();
        eventVotes = appRes.getEventVotes();
        userEventVotes = appRes.getUserEventVotes();
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

        eventList.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                ArrayList<String> facebookIds = new ArrayList<>();
                final Map<Bar, String> barFacebookIds = new HashMap<>();
                for (final Bar bar : appRes.getBars().values()) {
                    if(!StringUtils.isEmptyString(bar.facebookId)) {
                        barFacebookIds.put(bar, bar.facebookId);
                    }
                }
                EventVotesResource.getInstance().getEventVotes(new GetEventVotesHandler() {
                    @Override
                    public void onEventVotesLoaded(Map<String, ArrayList<EventVote>> eventVotes, Map<String, String> userEventVotes) {
                        appRes.setEventVotes(eventVotes);
                        appRes.setUserEventVotes(userEventVotes);

                        FacebookService.getInstance().getEvents(barFacebookIds, new GetFacebookEventsHandler() {
                            @Override
                            public void onFacebookEventsLoaded(Map<String, ArrayList<Event>> facebookEvents) {
                                FbRes.setEvents(facebookEvents);

                                refreshData();
                                update();
                            }
                        });
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onAddEventVoteClick(final Event event) {
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

                VoteStatsResource.getInstance().editVoteStat(event.barId, new GetVoteStatsHandler() {
                    @Override
                    public void onVoteStatsLoaded(Map<String, VoteStat> allTimeVoteStats) {
                        appRes.setAllTimeVoteStats(allTimeVoteStats);
                        refreshData();
                        update();
                        FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();

                        // Lisätään myös tapahtumavote lokiin
                        Vote vote = new Vote(eventVote);
                        VotesLogResource.getInstance().addVoteLog(event.barId, vote);
                    }
                });

                UsersResource.getInstance().updateUserKarma(KarmaPoints.VOTED, true);
            }
        });
    }

    @Override
    public void onRemoveEventVoteClick(final Event event) {
        final String voteIdToRemove = userEventVotes.get(event.eventId);
        EventVotesResource.getInstance().removeEventVote(event.eventId, voteIdToRemove, new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                appRes.setEventVote(event.eventId, voteIdToRemove, null);

                refreshData();
                update();
                FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();

                UsersResource.getInstance().updateUserKarma(KarmaPoints.VOTED, false);
            }
        });
    }

    @Override
    public void onDescriptionTextClick(Event event) {
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(event.name, event.description);
        dialog.show(getFragmentManager(), "description");
    }
}
