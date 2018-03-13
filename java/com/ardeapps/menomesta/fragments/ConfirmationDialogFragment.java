package com.ardeapps.menomesta.fragments;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.ardeapps.menomesta.R;

/**
 * Created by Arttu on 29.11.2015.
 */
public class ConfirmationDialogFragment extends DialogFragment {

    ConfirmationDialogCloseListener mListener = null;
    TextView desc_text;
    Button no_button;
    Button yes_button;
    String desc;

    public static ConfirmationDialogFragment newInstance(String desc) {
        ConfirmationDialogFragment f = new ConfirmationDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("desc", desc);
        f.setArguments(args);

        return f;
    }

    public void setListener(ConfirmationDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        desc = getArguments().getString("desc", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirmation_dialog, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        desc_text = (TextView) v.findViewById(R.id.desc_text);
        desc_text.setText(desc);

        no_button = (Button) v.findViewById(R.id.btn_no);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        yes_button = (Button) v.findViewById(R.id.btn_yes);
        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onDialogYesButtonClick();
            }
        });

        return v;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public interface ConfirmationDialogCloseListener {
        void onDialogYesButtonClick();
    }
}
