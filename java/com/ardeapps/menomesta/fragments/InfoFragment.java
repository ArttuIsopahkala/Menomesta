package com.ardeapps.menomesta.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.BuildConfig;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.FeedbacksResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.IconView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import static com.ardeapps.menomesta.PrefRes.FEEDBACK_TIME;
import static com.ardeapps.menomesta.PrefRes.LIKE_TIME;
import static com.ardeapps.menomesta.PrefRes.RATE_TIME;

public class InfoFragment extends Fragment {

    static final long oneDayInMilliseconds = 86400000;
    static final long oneMinuteInMilliseconds = 60000;

    private int fanCount = 0;

    public void setFanCount(int fanCount) {
        this.fanCount = fanCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        TextView versionTextView = (TextView) v.findViewById(R.id.version);
        TextView rateTextView = (TextView) v.findViewById(R.id.rate);
        TextView moreTextView = (TextView) v.findViewById(R.id.more);
        TextView facebookText = (TextView) v.findViewById(R.id.facebook);
        TextView fanCountText = (TextView) v.findViewById(R.id.fanCountText);
        TextView titleText = (TextView) v.findViewById(R.id.title);
        final EditText feedback_text = (EditText) v.findViewById(R.id.feedback_text);
        Button feedback_button = (Button) v.findViewById(R.id.feedback_button);
        IconView karma_button = (IconView) v.findViewById(R.id.karma_button);
        IconView agreement_button = (IconView) v.findViewById(R.id.agreement_button);
        // TODO Toimintoa ei ole vielä toteutettu
        RelativeLayout adsOffContainer = (RelativeLayout) v.findViewById(R.id.adsOffContainer);
        adsOffContainer.setVisibility(View.GONE);

        fanCountText.setText(getString(R.string.info_fan_count, String.valueOf(fanCount)));
        feedback_text.setHorizontallyScrolling(false);
        feedback_text.setMaxLines(5);
        titleText.setText(getString(R.string.info_title));
        versionTextView.setText(getString(R.string.info_version, BuildConfig.VERSION_NAME));
        rateTextView.setText(Html.fromHtml("<u>" + getString(R.string.info_link_rate) + "</u>"));
        rateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long rateTime = PrefRes.getLong(RATE_TIME);
                long currentTime = System.currentTimeMillis();
                if (currentTime > rateTime + oneDayInMilliseconds) {
                    UsersResource.getInstance().updateUserKarma(KarmaPoints.APP_RATED, true);
                    PrefRes.putLong(RATE_TIME, currentTime);
                }

                openUrl(getString(R.string.app_google_play_link));
            }
        });

        moreTextView.setText(Html.fromHtml("<u>" + getString(R.string.info_link_more) + "</u>"));
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.app_developer_link));
            }
        });

        facebookText.setText(Html.fromHtml("<u>" + getString(R.string.info_link_like) + "</u>"));
        facebookText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long likeTime = PrefRes.getLong(LIKE_TIME);
                long currentTime = System.currentTimeMillis();
                if (currentTime > likeTime + oneDayInMilliseconds) {
                    UsersResource.getInstance().updateUserKarma(KarmaPoints.FACEBOOK_LIKED, true);
                    PrefRes.putLong(LIKE_TIME, currentTime);
                }
                openUrl(getString(R.string.app_facebook_link));
            }
        });

        feedback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String feedback = feedback_text.getText().toString();
                if (!StringUtils.isEmptyString(feedback)) {
                    long feedbackTime = PrefRes.getLong(FEEDBACK_TIME);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > feedbackTime + oneMinuteInMilliseconds) {
                        if (feedback.equals(KarmaPoints.CODE_TO_PREMIUM)) {
                            final User user = AppRes.getUser().clone();
                            user.premium = true;
                            UsersResource.getInstance().editUser(user, new EditSuccessListener() {
                                @Override
                                public void onEditSuccess() {
                                    AppRes.setUser(user);
                                    Logger.toast(R.string.info_premium_now);
                                }
                            });
                        } else {
                            FeedbacksResource.getInstance().addFeedback(feedback, new AddSuccessListener() {
                                @Override
                                public void onAddSuccess(String id) {
                                    UsersResource.getInstance().updateUserKarma(KarmaPoints.FEEDBACK_GAVE, true);
                                    Logger.toast(R.string.info_feedback_sent);
                                }
                            });
                        }
                        PrefRes.putLong(FEEDBACK_TIME, currentTime);
                        feedback_text.setText("");
                    }
                }
            }
        });

        karma_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.profile_ads_title),
                        getString(R.string.profile_ads_desc, KarmaPoints.PERCENT_TO_BELONG_PREMIUM));
                dialog.show(getFragmentManager(), "karma");
            }
        });

        agreement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.info_agreement_title), getString(R.string.welcome_info_desc));
                dialog.show(getFragmentManager(), "agreement");
            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fragment avattu")
                .putContentType(this.getClass().getSimpleName()));

        return v;
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
