package com.ardeapps.menomesta.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Review;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Arttu on 29.11.2015.
 */
public class AddReviewDialogFragment extends DialogFragment {

    public interface RatingDialogListener {
        void onDialogClose();
        void onReviewSubmit(Review review);
    }

    public void setListener(RatingDialogListener l) {
        mListener = l;
    }

    RatingDialogListener mListener = null;

    RatingBar ratingBar;
    TextView title;
    Button btn_submit;
    Button cancel_button;
    String title_text;
    EditText reviewText;
    Bar bar;

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_review, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        title = (TextView) v.findViewById(R.id.title);
        btn_submit = (Button) v.findViewById(R.id.btn_submit);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);
        reviewText = (EditText) v.findViewById(R.id.reviewText);

        title.setText(getString(R.string.bar_details_rate) + " " + (FbRes.getBarDetail(bar.barId) != null ? FbRes.getBarDetail(bar.barId).name : bar.name));

        reviewText.setHorizontallyScrolling(false);
        reviewText.setMaxLines(10);
        reviewText.setText("");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = reviewText.getText().toString().trim();
                float rating = ratingBar.getRating();
                if(!StringUtils.isEmptyString(message) || rating > 0) {
                    Review review = new Review();
                    review.barId = bar.barId;
                    review.userId = AppRes.getUser().userId;
                    review.rating = ratingBar.getRating();
                    review.message = reviewText.getText().toString().trim();
                    review.time = System.currentTimeMillis();
                    review.usersVoted = new ArrayList<>();
                    review.usersReported = new ArrayList<>();

                    Helper.hideKeyBoard(reviewText);
                    mListener.onReviewSubmit(review);
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogClose();
            }
        });

        return v;
    }

}
