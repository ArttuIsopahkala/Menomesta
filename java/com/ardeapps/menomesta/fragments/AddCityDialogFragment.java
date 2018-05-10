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

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.objects.CityRequest;
import com.ardeapps.menomesta.resources.CityRequestsResource;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

/**
 * Created by Arttu on 29.11.2015.
 */
public class AddCityDialogFragment extends DialogFragment {

    EditText cityText;
    TextView info_text;
    TextView title;
    Button add_button;
    Button cancel_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_city, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        cityText = (EditText) v.findViewById(R.id.cityName);
        title = (TextView) v.findViewById(R.id.title);
        info_text = (TextView) v.findViewById(R.id.info_text);

        add_button = (Button) v.findViewById(R.id.btn_add);
        cancel_button = (Button) v.findViewById(R.id.btn_cancel);

        cityText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String city = cityText.getText().toString().trim();
                if (!StringUtils.isEmptyString(city)) {
                    Helper.hideKeyBoard(cityText);
                    final CityRequest cityRequest = new CityRequest();
                    cityRequest.userId = AppRes.getUser().userId;
                    cityRequest.city = city;
                    CityRequestsResource.getInstance().addCityRequest(cityRequest, new AddSuccessListener() {
                        @Override
                        public void onAddSuccess(String id) {
                            cityRequest.requestId = id;
                            info_text.setVisibility(View.VISIBLE);
                            cityText.setVisibility(View.GONE);
                            cancel_button.setVisibility(View.VISIBLE);
                            add_button.setVisibility(View.GONE);
                            cancel_button.setText(getString(R.string.ok));
                            title.setText(getString(R.string.submit_success));
                        }
                    });
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(cityText);
                dismiss();
            }
        });

        return v;
    }
}
