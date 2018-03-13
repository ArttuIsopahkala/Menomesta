package com.ardeapps.menomesta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.objects.BarRequest;
import com.ardeapps.menomesta.resources.BarRequestsResource;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;

public class BarRequestFragment extends Fragment {

    RadioButton pubRadioButton;
    RadioButton nightclubRadioButton;
    EditText nameText;
    EditText addressText;
    TextView titleText;
    Button submitButton;
    Spinner citySpinner;
    CheckBox food_checkBox;
    ArrayList<String> cityNames;
    AppRes appRes;

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        cityNames = appRes.getCityNames();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_bar, container, false);

        pubRadioButton = (RadioButton) v.findViewById(R.id.radioPub);
        nightclubRadioButton = (RadioButton) v.findViewById(R.id.radioNightclub);
        nameText = (EditText) v.findViewById(R.id.name);
        addressText = (EditText) v.findViewById(R.id.address);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        citySpinner = (Spinner) v.findViewById(R.id.citySpinner);
        titleText = (TextView) v.findViewById(R.id.title);
        food_checkBox = (CheckBox) v.findViewById(R.id.food_checkBox);

        titleText.setText(getString(R.string.add_bar_title));

        pubRadioButton.setChecked(true);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArrayAdapter.notifyDataSetChanged();
        citySpinner.setAdapter(spinnerArrayAdapter);
        for (int i = 0; i < cityNames.size(); i++) {
            if (AppRes.getCity().equals(cityNames.get(i))) {
                citySpinner.setSelection(i);
                break;
            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRequest();
            }
        });
        return v;
    }

    private void saveRequest() {
        String name = nameText.getText().toString();
        final String city;
        city = citySpinner.getSelectedItem().toString();
        String address = addressText.getText().toString();
        boolean nightClub = nightclubRadioButton.isChecked();
        boolean isFoodPlace = food_checkBox.isChecked();

        if (StringUtils.isEmptyString(name) || StringUtils.isEmptyString(city) || StringUtils.isEmptyString(address)) {
            InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.submit_error_title), getString(R.string.submit_error_desc));
            dialog.show(getFragmentManager(), "Lisää kaupunki");
        } else {
            final BarRequest barRequest = new BarRequest();
            barRequest.userId = AppRes.getUser().userId;
            barRequest.name = name;
            barRequest.city = city;
            barRequest.address = address;
            barRequest.nightClub = nightClub;
            barRequest.isFoodPlace = isFoodPlace;

            BarRequestsResource.getInstance().addBarRequest(barRequest, new AddSuccessListener() {
                @Override
                public void onAddSuccess(String id) {
                    barRequest.requestId = id;
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.submit_success), getString(R.string.submit_success_desc));
                    dialog.show(getFragmentManager(), "Lisää kaupunki");

                    nameText.setText("");
                    addressText.setText("");
                    cityNames.add(city);
                    citySpinner.setSelection(cityNames.size() - 1);
                    pubRadioButton.setChecked(true);
                    food_checkBox.setChecked(false);
                }
            });
        }
    }

}
