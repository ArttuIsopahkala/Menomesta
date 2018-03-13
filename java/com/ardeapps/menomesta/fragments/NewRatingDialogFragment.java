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
import android.widget.RatingBar;
import android.widget.TextView;

import com.ardeapps.menomesta.R;

/**
 * Created by Arttu on 29.11.2015.
 */
public class NewRatingDialogFragment extends DialogFragment {

    RatingDialogListener mListener = null;
    RatingBar rating;
    TextView title;
    Button btn_submit;
    Button cancel_button;
    String title_text;

    public static NewRatingDialogFragment newInstance(String title) {
        NewRatingDialogFragment f = new NewRatingDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);

        return f;
    }

    public void setListener(RatingDialogListener l) {
        mListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_text = getArguments().getString("title", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_rating_dialog, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        rating = (RatingBar) v.findViewById(R.id.rating);
        title = (TextView) v.findViewById(R.id.title);
        btn_submit = (Button) v.findViewById(R.id.btn_submit);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);

        title.setText(title_text);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRatingSubmit(rating.getRating());
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

    public interface RatingDialogListener {
        void onDialogClose();

        void onRatingSubmit(float rating);
    }
}
