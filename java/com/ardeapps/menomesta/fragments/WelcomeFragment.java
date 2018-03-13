package com.ardeapps.menomesta.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FirebaseAuthHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.BirthdayPicker;

import java.util.Calendar;

import static com.ardeapps.menomesta.PrefRes.COMMENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.COMPANY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.EVENT_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.PRIVATE_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.REPLY_NOTIFICATIONS;
import static com.ardeapps.menomesta.PrefRes.VIBRATE_NOTIFICATIONS;
import static com.ardeapps.menomesta.objects.User.FEMALE;
import static com.ardeapps.menomesta.objects.User.MALE;

public class WelcomeFragment extends Fragment {

    Listener mListener = null;
    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;
    Button saveButton;
    TextView description;
    BirthdayPicker birthdayPicker;
    User oldUser;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        maleRadioButton = (RadioButton) v.findViewById(R.id.radioMan);
        femaleRadioButton = (RadioButton) v.findViewById(R.id.radioWoman);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        description = (TextView) v.findViewById(R.id.description);
        birthdayPicker = (BirthdayPicker) v.findViewById(R.id.birthday_picker);

        Calendar birthday = Calendar.getInstance();

        /** Tämä on siirtymää varten versionCode 21 -> 22, versionName 1.7. Siirretään vanhat käyttäjätiedot uuteen tietokantaan. **/
        SharedPreferences userPref = AppRes.getContext().getSharedPreferences("user", 0);
        String userId = userPref.getString("userId", "");
        if (!StringUtils.isEmptyString(userId)) {
            oldUser = new User();
            oldUser.userId = userId;
            oldUser.gender = userPref.getInt("sex", 0) == 0 ? MALE : FEMALE;
            int age = userPref.getInt("age", 18);
            if (age < 18) {
                age = 18;
            }

            birthday.set(birthday.get(Calendar.YEAR) - age, 1, 1, 0, 0, 0);
            birthday.set(Calendar.MILLISECOND, 0);
            oldUser.birthday = birthday.getTimeInMillis();
            oldUser.lastLoginTime = userPref.getLong("lastLoginTime", System.currentTimeMillis());
            oldUser.creationTime = userPref.getLong("creationTime", 0);
            oldUser.karma = userPref.getLong("karma", 0);
            oldUser.isLookingFor = userPref.getInt("lookingFor", 1) == 0;
            oldUser.premium = userPref.getBoolean("premium", true);

            if (!oldUser.isMale()) {
                femaleRadioButton.setChecked(true);
            }
            description.setText(R.string.welcome_desc_old);
        } else {
            birthday = DateUtil.getDefaultBirthday();
            description.setText(R.string.welcome_desc_new);

            InfoDialogFragment welcomeDialog = InfoDialogFragment.newInstance(getString(R.string.welcome_info_title), getString(R.string.welcome_info_desc));
            welcomeDialog.show(getFragmentManager(), "käyttöehdot");
        }

        birthdayPicker.setDate(birthday);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        return v;
    }

    private void saveProfile() {
        final Calendar birthday = birthdayPicker.getDate();
        // Ikä väärin
        if (birthday == null || birthday.after(DateUtil.getDefaultBirthday())) {
            InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.alert_profile_error_title), getString(birthday == null ? R.string.alert_incorrect_value : R.string.profile_too_young));
            dialog.show(getFragmentManager(), "Virheellinen arvo");
            return;
        }

        // Rekisteröidytään Firebaseen, saadaan userId
        FirebaseService.registerToFirebase(new FirebaseAuthHandler() {
            @Override
            public void onFirebaseAuthSuccess(String userId) {
                long creationTime = System.currentTimeMillis();
                final User user = new User();
                user.userId = userId;
                user.gender = maleRadioButton.isChecked() ? MALE : FEMALE;
                user.birthday = birthday.getTimeInMillis();
                user.creationTime = oldUser != null && oldUser.creationTime > 0 ? oldUser.creationTime : creationTime;
                user.lastLoginTime = oldUser != null && oldUser.lastLoginTime > 0 ? oldUser.lastLoginTime : creationTime;
                user.karma = oldUser != null ? oldUser.karma : 0;
                user.isLookingFor = false;
                user.premium = oldUser != null && oldUser.premium;

                // Tallennetaan uusi käyttäjä tietokantaan
                UsersResource.getInstance().editUser(user, new EditSuccessListener() {
                    @Override
                    public void onEditSuccess() {
                        AppRes.setUser(user);

                        PrefRes.putBoolean(PRIVATE_NOTIFICATIONS, true);
                        PrefRes.putBoolean(REPLY_NOTIFICATIONS, true);
                        PrefRes.putBoolean(COMMENT_NOTIFICATIONS, true);
                        PrefRes.putBoolean(EVENT_NOTIFICATIONS, true);
                        PrefRes.putBoolean(COMPANY_NOTIFICATIONS, true);
                        PrefRes.putBoolean(VIBRATE_NOTIFICATIONS, true);

                        mListener.onNewUserSaved();
                    }
                });
            }
        });
    }

    public interface Listener {
        void onNewUserSaved();
    }
}
