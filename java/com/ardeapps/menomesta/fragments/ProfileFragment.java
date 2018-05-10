package com.ardeapps.menomesta.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FacebookLoginHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.UsersLookingForCompanyResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FacebookService;
import com.ardeapps.menomesta.services.FirebaseAuthService;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.views.BirthdayPicker;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    Button loginButton;
    Button deleteButton;

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
        birthdayPicker = (BirthdayPicker) v.findViewById(R.id.birthdayPicker);
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
        loginButton = (Button) v.findViewById(R.id.loginButton);
        deleteButton = (Button) v.findViewById(R.id.deleteButton);

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
                AddCityDialogFragment dialog = new AddCityDialogFragment();
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

            citySpinner.post(new Runnable() {
                @Override
                public void run() {
                    citySpinner.setSelection(cityNames.indexOf(AppRes.getCity()));
                }
            });
        }

        // Asetetaan syntymäaika
        final Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(AppRes.getUser().birthday);
        birthdayPicker.post(new Runnable() {
            @Override
            public void run() {
                birthdayPicker.setDate(birthday);
            }
        });

        if (AppRes.getUser().isMale()) {
            setViewChecked(maleRadioButton, true);
        } else setViewChecked(femaleRadioButton, true);

        setViewChecked(private_switch, PrefRes.getBoolean(PRIVATE_NOTIFICATIONS));
        setViewChecked(replies_switch, PrefRes.getBoolean(REPLY_NOTIFICATIONS));
        setViewChecked(comments_switch, PrefRes.getBoolean(COMMENT_NOTIFICATIONS));
        setViewChecked(events_switch, PrefRes.getBoolean(EVENT_NOTIFICATIONS));
        setViewChecked(company_switch, PrefRes.getBoolean(COMPANY_NOTIFICATIONS));
        setViewChecked(vibrate_switch, PrefRes.getBoolean(VIBRATE_NOTIFICATIONS));

        setLoginButton();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        // TODO Käyttäjän poistoa ei käytetä
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(AppRes.getContext().getString(R.string.profile_delete_confirmation));
                confirm_dialog.show(getFragmentManager(), "poista profiili dialogi");
                confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        UsersResource.getInstance().removeUser(AppRes.getUser().userId, new EditSuccessListener() {
                            @Override
                            public void onEditSuccess() {
                                FirebaseAuthService.getInstance().deleteAccount(new FirebaseAuthService.DeleteAccountHandler() {
                                    @Override
                                    public void onDeleteAccountSuccess() {
                                        LoginManager.getInstance().logOut();
                                        PrefRes.clearPref();
                                        Activity activity = getActivity();
                                        Intent i = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
                                        if (i != null) {
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        }
                                        startActivity(i);
                                        activity.finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fragment avattu")
                .putContentType(this.getClass().getSimpleName()));

        return v;
    }

    private void setViewChecked(final View view, final boolean checked) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (view instanceof Switch) {
                    Switch switchView = (Switch) view;
                    switchView.setChecked(checked);
                } else if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;
                    radioButton.setChecked(checked);
                }
            }
        });
    }

    private void setLoginButton() {
        if (FbRes.isUserLoggedIn()) {
            loginButton.setText(getString(R.string.facebook_logout));
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final FirebaseUser user = mAuth.getCurrentUser();
                    ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(AppRes.getContext().getString(R.string.profile_logout_confirmation));
                    confirm_dialog.show(getFragmentManager(), "kirjaudu ulos dialogi");
                    confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            LoginManager.getInstance().logOut();
                            FirebaseAuthService.getInstance().unlinkFacebook(new FirebaseAuthService.UnlinkFacebookHandler() {
                                @Override
                                public void onUnlinkFacebookSuccess() {
                                    setLoginButton();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            loginButton.setText(getString(R.string.facebook_login));
            FacebookService.getInstance().setLogInListener(getActivity(), loginButton, new FacebookLoginHandler() {
                @Override
                public void onLoginSuccess(String gender, String birthday) {
                    FirebaseAuthService.getInstance().linkFacebookIfNeeded(new FirebaseAuthService.LinkFacebookIfNeededHandler() {
                        @Override
                        public void onLinkFacebookIfNeededSuccess() {
                            setLoginButton();
                            FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshMainActivity();
                        }
                    });
                }

                @Override
                public void onLoginFailed() {
                    setLoginButton();
                }
            });

        }
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
