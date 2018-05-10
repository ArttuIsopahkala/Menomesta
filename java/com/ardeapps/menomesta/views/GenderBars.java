package com.ardeapps.menomesta.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.VoteStat;

/**
 * Created by Arttu on 13.4.2018.
 */

public class GenderBars extends LinearLayout {
    TextView femalePercentText;
    TextView malePercentText;
    LinearLayout genderBarsContainer;
    Context ctx;

    public GenderBars(Context context) {
        super(context);
        createView(context);
    }

    public GenderBars(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        this.ctx = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.gender_bars, this);
        genderBarsContainer = (LinearLayout) findViewById(R.id.genderBarsContainer);
        femalePercentText = (TextView) findViewById(R.id.femalePercentText);
        malePercentText = (TextView) findViewById(R.id.malePercentText);
    }

    public void setView(VoteStat voteStat, Activity activity) {
        long malePercent = voteStat.maleCount * 100 / voteStat.voteCount;
        long femalePercent = 100 - malePercent;
        malePercentText.setText(ctx.getString(R.string.percent, String.valueOf(malePercent)));
        femalePercentText.setText(ctx.getString(R.string.percent, String.valueOf(femalePercent)));

        genderBarsContainer.removeAllViews();
        View maleBar = new View(activity);
        View femaleBar = new View(activity);
        femaleBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_female));
        maleBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_male));
        femaleBar.setLayoutParams(new LinearLayout.LayoutParams(0, 40, femalePercent / 100f));
        maleBar.setLayoutParams(new LinearLayout.LayoutParams(0, 40, malePercent / 100f));

        genderBarsContainer.addView(femaleBar);
        genderBarsContainer.addView(maleBar);
    }
}
