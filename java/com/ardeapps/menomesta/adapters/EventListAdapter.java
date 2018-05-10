package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventListAdapter extends BaseAdapter {

    public interface EventListAdapterListener {
        void onAddEventVoteClick(Event event);
        void onRemoveEventVoteClick(Event event);
        void onDescriptionTextClick(Event event);
    }

    private static LayoutInflater inflater = null;
    private EventListAdapterListener mListener = null;
    private ArrayList<Event> events = new ArrayList<>();
    private Map<String, Bar> bars = new HashMap<>();
    private Map<String, String> userEventVotes = new HashMap<>();
    private Map<String, String> userVotes = new HashMap<>();
    private Context context;
    private boolean adapterFirstTime;
    private AppRes appRes;

    public EventListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(EventListAdapterListener l) {
        mListener = l;
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        events = FbRes.getEvents();
        bars = appRes.getBars();
        userEventVotes = appRes.getUserEventVotes();
        userVotes = appRes.getUserVotes();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class EventHolder {
        TextView event_name;
        TextView participateText;
        ImageView joinButton;
        TextView timeText;
        TextView event_place;
        TextView attendeesText;
        TextView descriptionText;
        TextView open_map_text;
        LinearLayout timeContent;
        LinearLayout placeContent;
        LinearLayout attendeesContent;
    }

    @Override
    public View getView(final int position, View cv, ViewGroup parent) {
        final EventHolder holder = new EventHolder();
        if (cv == null) {
            cv = inflater.inflate(R.layout.list_item_event, null);
            adapterFirstTime = true;
        }
        holder.event_name = (TextView) cv.findViewById(R.id.event_name);
        holder.joinButton = (ImageView) cv.findViewById(R.id.joinButton);
        holder.participateText = (TextView) cv.findViewById(R.id.participate);
        holder.timeText = (TextView) cv.findViewById(R.id.timeText);
        holder.event_place = (TextView) cv.findViewById(R.id.event_place);
        holder.attendeesText = (TextView) cv.findViewById(R.id.attendeesText);
        holder.descriptionText = (TextView) cv.findViewById(R.id.descriptionText);
        holder.open_map_text = (TextView) cv.findViewById(R.id.open_map_text);
        holder.timeContent = (LinearLayout) cv.findViewById(R.id.timeContent);
        holder.placeContent = (LinearLayout) cv.findViewById(R.id.placeContent);
        holder.attendeesContent = (LinearLayout) cv.findViewById(R.id.attendeesContent);

        final Event event = events.get(position);
        final Bar bar = bars.get(event.barId);

        holder.event_name.setText(event.name);
        holder.timeText.setText(StringUtils.getDateTimeText(event.startTime));
        holder.event_place.setText(FbRes.getBarDetail(bar.barId) != null ? FbRes.getBarDetail(bar.barId).name : bar.name);

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
                        mListener.onDescriptionTextClick(event);
                    }
                });
            } else {
                holder.descriptionText.setText(event.description);
            }
        }

        // Käyttäjä osallistunut tapahtumaan tai osallistunut baariin tänään
        final boolean userHasVoted = userEventVotes.containsKey(event.eventId) || userVotes.containsKey(event.barId);

        // Osallistumisnappi
        if (userHasVoted) {
            holder.joinButton.setImageResource(R.drawable.minus_icon);
        } else {
            holder.joinButton.setImageResource(R.drawable.plus_icon);
        }

        int attendees = event.attendingCount;
        if (attendees > 0) {
            if(attendees == 1) {
                holder.attendeesText.setText(attendees + " " + context.getString(R.string.vote));
            } else holder.attendeesText.setText(attendees + " " + context.getString(R.string.votes));
        } else {
            holder.attendeesText.setText(context.getString(R.string.no_votes));
        }

        // Osallistujat
        int voteCount = appRes.getVotes().get(event.barId) != null ? appRes.getVotes().get(event.barId).size() : 0;
        voteCount += event.attendingCount;
        if (voteCount > 0) {
            if(voteCount == 1) {
                holder.participateText.setText(voteCount + " " + context.getString(R.string.vote));
            } else holder.participateText.setText(voteCount + " " + context.getString(R.string.votes));
        } else {
            holder.participateText.setText(context.getString(R.string.vote_here));
        }

        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Osallistunut
                if (userHasVoted) {
                    mListener.onRemoveEventVoteClick(event);
                } else {
                    mListener.onAddEventVoteClick(event);
                }
            }
        });

        // Animaatiot infoteksteille
        if(adapterFirstTime) {
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
            holder.timeContent.startAnimation(in1);
            holder.placeContent.startAnimation(in2);
            holder.open_map_text.startAnimation(in2);
            holder.attendeesContent.startAnimation(in3);
            holder.descriptionText.startAnimation(in4);
        }

        holder.open_map_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToShowLocationFragment(bar);
            }
        });

        adapterFirstTime = false;
        return cv;
    }
}
