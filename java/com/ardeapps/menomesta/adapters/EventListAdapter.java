package com.ardeapps.menomesta.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.fragments.ConfirmationDialogFragment;
import com.ardeapps.menomesta.fragments.InfoDialogFragment;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;

import java.util.ArrayList;

public class EventListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    EventListAdapterListener mListener = null;
    ArrayList<Event> events;
    Context context;
    boolean adapterFirstTime;
    AppRes appRes;

    public EventListAdapter(Context context) {
        this.context = context;

        refreshData();

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(EventListAdapterListener l) {
        mListener = l;
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        events = appRes.getEvents();
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

    @Override
    public View getView(final int position, View cv, ViewGroup parent) {
        final Holder holder = new Holder();
        if (cv == null) {
            cv = inflater.inflate(R.layout.event_list_item, null);
            adapterFirstTime = true;
        }
        holder.event_name = (TextView) cv.findViewById(R.id.event_name);
        holder.joinButton = (ImageView) cv.findViewById(R.id.joinButton);
        holder.participateText = (TextView) cv.findViewById(R.id.participate);
        holder.timeText = (TextView) cv.findViewById(R.id.timeText);
        holder.event_place = (TextView) cv.findViewById(R.id.event_place);
        holder.priceText = (TextView) cv.findViewById(R.id.priceText);
        holder.descriptionText = (TextView) cv.findViewById(R.id.descriptionText);
        holder.edit_event = (IconView) cv.findViewById(R.id.edit_event);
        holder.report_event = (IconView) cv.findViewById(R.id.report_event);
        holder.remove_event = (IconView) cv.findViewById(R.id.remove_event);
        holder.open_map_text = (TextView) cv.findViewById(R.id.open_map_text);
        holder.timeContent = (LinearLayout) cv.findViewById(R.id.timeContent);
        holder.placeContent = (LinearLayout) cv.findViewById(R.id.placeContent);
        holder.priceContent = (LinearLayout) cv.findViewById(R.id.priceContent);

        final Event event = events.get(position);

        holder.event_name.setText(event.name);
        holder.timeText.setText(StringUtils.getDateTimeText(event.startTime, false, false));

        holder.event_place.setText(event.address);

        if (event.price == 0)
            holder.priceText.setText(context.getString(R.string.events_free));
        else
            holder.priceText.setText(context.getString(R.string.price, String.valueOf(event.price)));

        if (event.description.isEmpty()) {
            holder.descriptionText.setVisibility(View.GONE);
        } else {
            holder.descriptionText.setVisibility(View.VISIBLE);
            holder.descriptionText.setText(event.description);
        }

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
            holder.priceContent.startAnimation(in3);
            holder.descriptionText.startAnimation(in4);
        }

        final boolean userHasVoted = event.usersJoined != null && event.usersJoined.contains(AppRes.getUser().userId);

        // Osallistumisnappi
        if (userHasVoted) {
            holder.joinButton.setImageResource(R.drawable.minus_icon);
        } else {
            holder.joinButton.setImageResource(R.drawable.plus_icon);
        }

        // Osallistujat
        if (event.usersJoined != null && event.usersJoined.size() != 0) {
            int voteCount = event.usersJoined.size();
            if(voteCount == 1) {
                holder.participateText.setText(voteCount + " " + context.getString(R.string.vote));
            } else holder.participateText.setText(voteCount + " " + context.getString(R.string.votes));
        } else {
            holder.participateText.setText(context.getString(R.string.vote_here));
        }

        holder.open_map_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEventLocationClick(event.address);
            }
        });

        if (event.userId.equals(AppRes.getUser().userId)) {
            holder.report_event.setVisibility(View.GONE);
            holder.remove_event.setVisibility(View.VISIBLE);
            holder.edit_event.setVisibility(View.VISIBLE);
            holder.remove_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = (FragmentActivity) (context);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(context.getString(R.string.events_confirm_remove));
                    confirm_dialog.show(fm, "flägää dialogi");
                    confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            mListener.onRemoveEventClick(event.eventId);
                        }
                    });
                }
            });

            holder.edit_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToAddEventFragment(event);
                }
            });
        } else {
            holder.report_event.setVisibility(View.VISIBLE);
            holder.edit_event.setVisibility(View.GONE);
            holder.remove_event.setVisibility(View.GONE);
            holder.report_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = (FragmentActivity) (context);
                    FragmentManager fm = activity.getSupportFragmentManager();

                    if (event.usersReported != null && event.usersReported.contains(AppRes.getUser().userId)) {
                        InfoDialogFragment reportDialog = InfoDialogFragment.newInstance(context.getString(R.string.not_participated_title), context.getString(R.string.confirmation_report_error_desc_event));
                        reportDialog.show(fm, "olet jo raportoinut tästä");
                    } else {
                        ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(context.getString(R.string.confirmation_report_event));
                        confirm_dialog.show(fm, "flägää dialogi");
                        confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                            @Override
                            public void onDialogYesButtonClick() {
                                // Lisää raportti
                                mListener.onReportEventClick(event);
                            }
                        });
                    }
                }
            });
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

        adapterFirstTime = false;
        return cv;
    }

    public interface EventListAdapterListener {
        void onRemoveEventClick(String eventId);

        void onReportEventClick(Event event);

        void onAddEventVoteClick(Event event);

        void onRemoveEventVoteClick(Event event);

        void onEventLocationClick(String address);
    }

    public class Holder {
        TextView event_name;
        TextView participateText;
        ImageView joinButton;
        TextView timeText;
        TextView event_place;
        TextView priceText;
        TextView descriptionText;
        TextView open_map_text;
        IconView edit_event;
        IconView report_event;
        IconView remove_event;
        LinearLayout timeContent;
        LinearLayout placeContent;
        LinearLayout priceContent;
    }
}
