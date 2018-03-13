package com.ardeapps.menomesta.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.resources.EventsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventFragment extends Fragment {

    Listener mListener = null;
    EditText nameText;
    EditText descriptionText;
    EditText priceText;
    AutoCompleteTextView addressText;
    TextView dateText;
    TextView timeText;
    TextView titleText;
    Button submitButton;
    Map<String, Bar> bars = new HashMap<>();
    Calendar myCalendar;
    Event event;
    ArrayAdapter<String> adapter;
    AppRes appRes;

    public void setListener(Listener l) {
        mListener = l;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        bars = appRes.getBars();
    }

    // EditText täytyy päivittää täällä
    @Override
    public void onResume() {
        super.onResume();
        if (event == null) {
            nameText.setText("");
            addressText.setText("");
            priceText.setText("");
            descriptionText.setText("");
        } else {
            nameText.setText(event.name);
            addressText.setText(event.address);
            priceText.setText(event.price == 0 ? "" : String.valueOf(event.price));
            descriptionText.setText(event.description);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        nameText = (EditText) v.findViewById(R.id.nameText);
        addressText = (AutoCompleteTextView) v.findViewById(R.id.addressText);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        titleText = (TextView) v.findViewById(R.id.title);
        dateText = (TextView) v.findViewById(R.id.dateText);
        timeText = (TextView) v.findViewById(R.id.timeText);
        descriptionText = (EditText) v.findViewById(R.id.descriptionText);
        priceText = (EditText) v.findViewById(R.id.priceText);

        descriptionText.setHorizontallyScrolling(false);
        descriptionText.setMaxLines(10);

        ArrayList<String> barNames = new ArrayList<>();
        for (Bar bar : bars.values()) {
            barNames.add(bar.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<>(barNames));
        addressText.setThreshold(1);
        addressText.setAdapter(adapter);
        myCalendar = Calendar.getInstance();
        if(event == null) {
            titleText.setText(getString(R.string.events_add));
            setDefaultDateTime();
            Helper.showKeyBoard();
            nameText.requestFocus();
        } else {
            titleText.setText(getString(R.string.events_edit));
            myCalendar.setTimeInMillis(event.startTime);
            dateText.setText(StringUtils.getDateText(myCalendar.getTime().getTime(), false, false));
            timeText.setText(StringUtils.getTimeText(myCalendar.getTimeInMillis()));
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (!DateUtil.isOnThisWeek(myCalendar.getTime().getTime()))
                    dateText.setText(StringUtils.getDateText(myCalendar.getTime().getTime(), true, false));
                else
                    dateText.setText(StringUtils.getDateText(myCalendar.getTime().getTime(), false, false));
            }

        };

        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                        timeText.setText(StringUtils.getTimeText(myCalendar.getTimeInMillis()));
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String address = addressText.getText().toString();
                long startTime = myCalendar.getTimeInMillis();
                String description = descriptionText.getText().toString();
                String priceString = priceText.getText().toString();
                int price = 0;
                if(!priceString.isEmpty()) {
                    try {
                        price = Integer.parseInt(priceText.getText().toString());
                    } catch (NumberFormatException ex) {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.events_add_error_title), getString(R.string.prices_error_price));
                        dialog.show(getFragmentManager(), "Virheellinen arvo");
                        return;
                    }
                }

                if(startTime < System.currentTimeMillis()) {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.events_add_error_title), getString(R.string.events_add_error_desc_time));
                    dialog.show(getFragmentManager(), "Aika menneisyydessä");
                    return;
                }

                if(StringUtils.isEmptyString(name) || StringUtils.isEmptyString(address)) {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.events_add_error_title), getString(R.string.events_add_error_desc_empty));
                    dialog.show(getFragmentManager(), "Tapahtumaa ei lisätty");
                } else {
                    final Event eventToSave;
                    if(event != null) {
                        eventToSave = event.clone();
                    } else {
                        eventToSave = new Event();
                    }

                    eventToSave.userId = AppRes.getUser().userId;
                    eventToSave.name = name;
                    eventToSave.startTime = startTime;
                    eventToSave.address = address;
                    eventToSave.price = price;
                    eventToSave.description = description;

                    if (event != null) {
                        EventsResource.getInstance().editEvent(eventToSave, new EditSuccessListener() {
                            @Override
                            public void onEditSuccess() {
                                appRes.setEvent(eventToSave.eventId, eventToSave);

                                resetFields();

                                mListener.onEventUpdated(eventToSave);
                                getActivity().onBackPressed();
                            }
                        });
                    } else {
                        EventsResource.getInstance().addEvent(eventToSave, new AddSuccessListener() {
                            @Override
                            public void onAddSuccess(String id) {
                                eventToSave.eventId = id;
                                appRes.setEvent(eventToSave.eventId, eventToSave);

                                resetFields();
                                mListener.onEventUpdated(eventToSave);

                                UsersResource.getInstance().updateUserKarma(KarmaPoints.EVENT_ADDED, true);
                                getActivity().onBackPressed();
                            }
                        });
                    }

                }
            }
        });

        return v;
    }

    private void resetFields() {
        nameText.setText("");
        setDefaultDateTime();
        addressText.setText("");
        priceText.setText("");
        descriptionText.setText("");
    }

    private void setDefaultDateTime() {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        myCalendar.set(Calendar.HOUR_OF_DAY, hour + 1);
        myCalendar.set(Calendar.MINUTE, 0);
        dateText.setText(StringUtils.getDateText(myCalendar.getTime().getTime(), false, false));
        timeText.setText(StringUtils.getTimeText(myCalendar.getTimeInMillis()));
    }

    public interface Listener {
        void onEventUpdated(Event event);
    }
}
