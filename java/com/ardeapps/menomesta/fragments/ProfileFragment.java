package com.ardeapps.menomesta.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.UsersLookingForCompanyResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.views.BirthdayPicker;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;
import java.util.Calendar;

import static com.ardeapps.menomesta.PrefRes.COMMENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.COMPANY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.EVENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.PRIVATE_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.REPLY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.VIBRATE_NOTIFICATIONS;
import static com.ardeapps.menomesta.objects.User.FEMALE;
import static com.ardeapps.menomesta.objects.User.MALE;

public class ProfileFragment extends Fragment {

    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;
    TextView addCityText;
    Spinner citySpinner;
    BirthdayPicker birthdayPicker;
    TextView titleText;
    Button saveButton;
    Switch private_switch;
    Switch replies_switch;
    Switch comments_switch;
    Switch events_switch;
    Switch company_switch;
    Switch vibrate_switch;
    TextView karmaText;
    LinearLayout cityContent;

    // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
    LinearLayout notificationPrivateContent;
    LinearLayout notificationCompanyContent;

    ArrayList<String> cityNames;
    Location location;
    AppRes appRes;

    public void refreshData() {
        appRes = (AppRes) AppRes.getContext();
        cityNames = appRes.getCityNames();
        location = appRes.getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        maleRadioButton = (RadioButton) v.findViewById(R.id.radioMan);
        femaleRadioButton = (RadioButton) v.findViewById(R.id.radioWoman);
        birthdayPicker = (BirthdayPicker) v.findViewById(R.id.birthday_picker);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        citySpinner = (Spinner) v.findViewById(R.id.citySpinner);
        addCityText = (TextView) v.findViewById(R.id.add_city);
        titleText = (TextView) v.findViewById(R.id.title);
        private_switch = (Switch) v.findViewById(R.id.private_switch);
        replies_switch = (Switch) v.findViewById(R.id.replies_switch);
        comments_switch = (Switch) v.findViewById(R.id.comments_switch);
        events_switch = (Switch) v.findViewById(R.id.events_switch);
        company_switch = (Switch) v.findViewById(R.id.company_switch);
        vibrate_switch = (Switch) v.findViewById(R.id.vibrate_switch);
        karmaText = (TextView) v.findViewById(R.id.karmaText);
        cityContent = (LinearLayout) v.findViewById(R.id.cityContent);
        // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
        notificationPrivateContent = (LinearLayout) v.findViewById(R.id.notificationPrivateContent);
        notificationCompanyContent = (LinearLayout) v.findViewById(R.id.notificationCompanyContent);
        notificationPrivateContent.setVisibility(View.GONE);
        notificationCompanyContent.setVisibility(View.GONE);

        titleText.setText(getString(R.string.profile_title));
        karmaText.setText("+" + AppRes.getUser().karma);

        addCityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCityDialogFragment dialog = new NewCityDialogFragment();
                dialog.show(getFragmentManager(), "Lisää kaupunki");
            }
        });

        // Tässä vaiheessa kaupunki on varmasti tallennettuna AppResiin
        if (location != null && cityNames.contains(Helper.getCityNameFromLocation(location))) {
            cityContent.setVisibility(View.GONE);
        } else {
            cityContent.setVisibility(View.VISIBLE);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityNames);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArrayAdapter.notifyDataSetChanged();
            citySpinner.setAdapter(spinnerArrayAdapter);

            citySpinner.setSelection(cityNames.indexOf(AppRes.getCity()));
        }

        // Asetetaan syntymäaika
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(AppRes.getUser().birthday);
        birthdayPicker.setDate(birthday);

        if (AppRes.getUser().isMale()) {
            maleRadioButton.setChecked(true);
        } else femaleRadioButton.setChecked(true);

        private_switch.setChecked(PrefRes.getBoolean(PRIVATE_NOTIFICATIONS));
        replies_switch.setChecked(PrefRes.getBoolean(REPLY_NOTIFICATIONS));
        comments_switch.setChecked(PrefRes.getBoolean(COMMENT_NOTIFICATIONS));
        events_switch.setChecked(PrefRes.getBoolean(EVENT_NOTIFICATIONS));
        company_switch.setChecked(PrefRes.getBoolean(COMPANY_NOTIFICATIONS));
        vibrate_switch.setChecked(PrefRes.getBoolean(VIBRATE_NOTIFICATIONS));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fragment avattu")
                .putContentType(this.getClass().getSimpleName()));

        return v;
    }

    private void saveUser() {
        final Calendar birthday = birthdayPicker.getDate();
        // Ikä väärin
        if (birthday == null || birthday.after(DateUtil.getDefaultBirthday())) {
            InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.alert_profile_error_title), getString(birthday == null ? R.string.alert_incorrect_value : R.string.profile_too_young));
            dialog.show(getFragmentManager(), "Virheellinen arvo");
            return;
        }

        String gender = maleRadioButton.isChecked() ? MALE : FEMALE;
        final String city = location != null && cityNames.contains(Helper.getCityNameFromLocation(location)) ? AppRes.getCity() : citySpinner.getSelectedItem().toString();

        boolean onlyNotificationsChanged = false;
        if ((private_switch.isChecked() != PrefRes.getBoolean(PRIVATE_NOTIFICATIONS)
                || replies_switch.isChecked() != PrefRes.getBoolean(REPLY_NOTIFICATIONS)
                || comments_switch.isChecked() != PrefRes.getBoolean(COMMENT_NOTIFICATIONS)
                || events_switch.isChecked() != PrefRes.getBoolean(EVENT_NOTIFICATIONS)
                || company_switch.isChecked() != PrefRes.getBoolean(COMPANY_NOTIFICATIONS)
                || vibrate_switch.isChecked() != PrefRes.getBoolean(VIBRATE_NOTIFICATIONS))
                && birthday.getTimeInMillis() == AppRes.getUser().birthday && gender.equals(AppRes.getUser().gender)
                && city.equals(AppRes.getCity())) {
            onlyNotificationsChanged = true;
        }

        PrefRes.putBoolean(PRIVATE_NOTIFICATIONS, private_switch.isChecked());
        PrefRes.putBoolean(REPLY_NOTIFICATIONS, replies_switch.isChecked());
        PrefRes.putBoolean(COMMENT_NOTIFICATIONS, comments_switch.isChecked());
        PrefRes.putBoolean(EVENT_NOTIFICATIONS, events_switch.isChecked());
        PrefRes.putBoolean(COMPANY_NOTIFICATIONS, company_switch.isChecked());
        PrefRes.putBoolean(VIBRATE_NOTIFICATIONS, vibrate_switch.isChecked());

        if (onlyNotificationsChanged) {
            Helper.startNotificationService();
            getActivity().onBackPressed();
            Logger.toast(R.string.profile_saved);
        } else {
            final User user = AppRes.getUser().clone();
            user.birthday = birthday.getTimeInMillis();
            user.gender = gender;
            user.city = city;
            UsersResource.getInstance().editUser(user, new EditSuccessListener() {
                @Override
                public void onEditSuccess() {
                    AppRes.setUser(user);

                    // Kaupunki vaihtui
                    if (!city.equals(AppRes.getCity())) {
                        if (user.isLookingFor) {
                            // Tallenna companyMessage appResiin, poista se vanhasta kaupungista ja lisää uuteen kaupunkiin
                            UsersLookingForCompanyResource.getInstance().removeUserLookingForCompany(new EditSuccessListener() {
                                @Override
                                public void onEditSuccess() {
                                    AppRes.setCity(city);
                                    // Siirretään companyMessage uuteen kaupunkiin
                                    UsersLookingForCompanyResource.getInstance().editUserLookingForCompany(appRes.getCompanyMessage(), new EditSuccessListener() {
                                        @Override
                                        public void onEditSuccess() {
                                            Logger.toast(R.string.profile_saved);
                                            FragmentListeners.getInstance().getFragmentChangeListener().goToLoaderFragment();
                                        }
                                    });
                                }
                            });
                        } else {
                            Logger.toast(R.string.profile_saved);
                            AppRes.setCity(city);
                            FragmentListeners.getInstance().getFragmentChangeListener().goToLoaderFragment();
                        }
                    } else {
                        // Takaisin pääsivulle
                        getActivity().onBackPressed();
                    }
                }
            });
        }
    }
}
