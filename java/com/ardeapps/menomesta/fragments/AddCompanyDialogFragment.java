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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

/**
 * Created by Arttu on 29.11.2015.
 */
public class AddCompanyDialogFragment extends DialogFragment {

    AddCompanyDialogCloseListener mListener = null;
    EditText companyText;
    TextView info_text;
    TextView title;
    Button ready_button;
    Button cancel_button;
    String oldText;

    public void setListener(AddCompanyDialogCloseListener l) {
        mListener = l;
    }

    public void setCompanyText(String text) {
        oldText = text;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_company_text, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        companyText = (EditText) v.findViewById(R.id.companyText);
        title = (TextView) v.findViewById(R.id.title);
        info_text = (TextView) v.findViewById(R.id.info_text);

        ready_button = (Button) v.findViewById(R.id.btn_ready);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);

        companyText.setHorizontallyScrolling(false);
        companyText.setMaxLines(10);

        companyText.setText(oldText);

        companyText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ready_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = companyText.getText().toString();
                if (!StringUtils.isEmptyString(text)) {
                    Helper.hideKeyBoard(companyText);

                    if(StringUtils.isEmptyString(oldText))
                        mListener.onCompanyDialogTextAdded(text, true);
                    else
                        mListener.onCompanyDialogTextAdded(text, false);
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(companyText);
                mListener.onCompanyDialogCancel();
            }
        });

        return v;
    }

    public interface AddCompanyDialogCloseListener {
        void onCompanyDialogTextAdded(String text, boolean create);

        void onCompanyDialogCancel();
    }
}
