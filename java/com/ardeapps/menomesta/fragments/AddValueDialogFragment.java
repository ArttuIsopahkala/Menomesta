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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

/**
 * Created by Arttu on 29.11.2015.
 */
public class AddValueDialogFragment extends DialogFragment {

    AddValueDialogCloseListener mListener = null;
    EditText valueText;
    TextView title;
    CheckBox jacketCheckBox;
    Button ready_button;
    Button cancel_button;
    EditType type;
    BarDetails barDetails;

    public void setListener(AddValueDialogCloseListener l) {
        mListener = l;
    }

    public void setBarDetails(BarDetails barDetails) {
        this.barDetails = barDetails;
    }

    public void setEditType(EditType type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_value_dialog, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        valueText = (EditText) v.findViewById(R.id.valueText);
        title = (TextView) v.findViewById(R.id.title);
        jacketCheckBox = (CheckBox) v.findViewById(R.id.jacketCheckBox);
        ready_button = (Button) v.findViewById(R.id.btn_ready);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);

        valueText.setText("");
        jacketCheckBox.setVisibility(View.GONE);
        switch (type) {
            case AGE_FRIDAY:
                title.setText(getString(R.string.bar_details_age_friday));
                if(barDetails != null && barDetails.ageLimitUpdated != 0) {
                    valueText.setText(barDetails.ageLimit+"");
                }
                break;
            case AGE_SATURDAY:
                title.setText(getString(R.string.bar_details_age_saturday));
                if(barDetails != null && barDetails.ageLimitSaturdayUpdated != 0) {
                    valueText.setText(barDetails.ageLimitSaturday+"");
                }
                break;
            case ENTRANCE:
                jacketCheckBox.setChecked(false);
                jacketCheckBox.setVisibility(View.VISIBLE);
                if(barDetails != null && barDetails.entranceUpdated != 0) {
                    valueText.setText(barDetails.entrancePrice+"");
                    jacketCheckBox.setChecked(barDetails.jacketIncluded);
                }
                break;
        }

        valueText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ready_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;
                BarDetails detailsToSave;
                if (barDetails != null) {
                    detailsToSave = barDetails.clone();
                } else {
                    detailsToSave = new BarDetails();
                }

                if (!StringUtils.isEmptyString(valueText.getText().toString())) {
                    Helper.hideKeyBoard(valueText);
                    try {
                        value = Integer.parseInt(valueText.getText().toString());
                    } catch (NumberFormatException ex) {
                        showValueError(getString(R.string.bar_details_error_desc));
                        return;
                    }
                    switch (type) {
                        case AGE_FRIDAY:
                            if (value > 30 || value < 18) {
                                showValueError(getString(R.string.bar_details_error_desc));
                            } else {
                                detailsToSave.ageLimit = value;
                                detailsToSave.ageLimitUpdated = System.currentTimeMillis();
                                mListener.onValueUpdated(detailsToSave);
                            }
                            break;
                        case AGE_SATURDAY:
                            if (value > 30 || value < 18) {
                                showValueError(getString(R.string.bar_details_error_desc));
                            } else {
                                detailsToSave.ageLimitSaturday = value;
                                detailsToSave.ageLimitSaturdayUpdated = System.currentTimeMillis();
                                mListener.onValueUpdated(detailsToSave);
                            }
                            break;
                        case ENTRANCE:
                            if (value > 30) {
                                showValueError(getString(R.string.bar_details_error_desc));
                            } else {
                                detailsToSave.jacketIncluded = jacketCheckBox.isChecked();
                                detailsToSave.entrancePrice = value;
                                detailsToSave.entranceUpdated = System.currentTimeMillis();
                                mListener.onValueUpdated(detailsToSave);
                            }
                            break;
                    }
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(valueText);
                mListener.onValueDialogCancel();
            }
        });

        return v;
    }

    private void showValueError(String errorText) {
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.bar_details_error_title), errorText);
        dialog.show(getFragmentManager(), "Virheellinen arvo");
    }

    public enum EditType {
        AGE_FRIDAY,
        AGE_SATURDAY,
        ENTRANCE
    }

    public interface AddValueDialogCloseListener {
        void onValueUpdated(BarDetails barDetails);

        void onValueDialogCancel();
    }
}
