package com.ardeapps.menomesta.fragments;

import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FirebaseAuthHandler;
import com.ardeapps.menomesta.handlers.GetAppDataHandler;
import com.ardeapps.menomesta.handlers.GetBarListDataHandler;
import com.ardeapps.menomesta.handlers.GetCityCommentsHandler;
import com.ardeapps.menomesta.handlers.GetCityNamesHandler;
import com.ardeapps.menomesta.handlers.GetEventsHandler;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.AppDataResource;
import com.ardeapps.menomesta.resources.CityNamesResource;
import com.ardeapps.menomesta.resources.CommentsResource;
import com.ardeapps.menomesta.resources.EventsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.services.FirebaseService;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static com.ardeapps.menomesta.PrefRes.CITY;

public class LoaderFragment extends Fragment implements FirebaseAuthHandler, GetUserHandler, GetBarListDataHandler,
        GetCityCommentsHandler, GetCityNamesHandler, GetAppDataHandler, GetEventsHandler {

    AppRes appRes = (AppRes) AppRes.getContext();
    TextView addCityText;
    Spinner citySpinner;
    Button saveCityButton;
    RelativeLayout selectCityContent;

    User userToSave;
    String cityToSave;
    Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loader, container, false);

        selectCityContent = (RelativeLayout) v.findViewById(R.id.selectCityContent);
        saveCityButton = (Button) v.findViewById(R.id.saveCityButton);
        citySpinner = (Spinner) v.findViewById(R.id.citySpinner);
        addCityText = (TextView) v.findViewById(R.id.add_city);

        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        boolean isAdmin = android_id != null && android_id.equals("8d6349fed10ab504");
        //boolean isAdmin = false;
        appRes.setIsAdmin(isAdmin);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            UsersResource.getInstance().getUser(user.getUid(), this);
        else
            FirebaseService.logInToFirebase(this);

        return v;
    }

    @Override
    public void onFirebaseAuthSuccess(String userId) {
        UsersResource.getInstance().getUser(userId, this);
    }

    @Override
    public void onUserLoaded(User user) {
        if (user == null) {
            // Käyttäjä on tietokannasta poistunut. Tallenna lokaali User, jos löytyy.
            final User prefUser = PrefRes.getUser();
            if (prefUser != null) {
                UsersResource.getInstance().editUser(prefUser, new EditSuccessListener() {
                    @Override
                    public void onEditSuccess() {
                        userToSave = prefUser;
                        CityNamesResource.getInstance().getCityNames(LoaderFragment.this);
                    }
                });
            } else {
                mListener.onUserNotFound();
            }
        } else {
            userToSave = user;
            CityNamesResource.getInstance().getCityNames(this);
        }
    }

    @Override
    public void onCityNamesLoaded(ArrayList<String> cityNames) {
        appRes.setCityNames(cityNames);

        Location location = appRes.getLocation();
        // Jos sijainti on päällä ja cityNames sisältää kaupunkin
        if (location != null && cityNames.contains(Helper.getCityNameFromLocation(location))) {
            cityToSave = Helper.getCityNameFromLocation(location);
            saveUserAndCity();
        } else {
            // Jos sijainti on kiinni, mutta kaupunki on aikaisemmin tallennettu, mene sovellukseen
            if (!StringUtils.isEmptyString(PrefRes.getString(CITY))) {
                cityToSave = PrefRes.getString(CITY);
                saveUserAndCity();
            } else {
                // Kaupunkia ei löytynyt
                selectCityContent.setVisibility(View.VISIBLE);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityNames);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerArrayAdapter.notifyDataSetChanged();
                citySpinner.setAdapter(spinnerArrayAdapter);

                addCityText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewCityDialogFragment dialog = new NewCityDialogFragment();
                        dialog.show(getFragmentManager(), "Lisää kaupunki");
                    }
                });

                saveCityButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCityContent.setVisibility(View.GONE);
                        cityToSave = citySpinner.getSelectedItem().toString().trim();
                        saveUserAndCity();
                    }
                });
            }
        }
    }

    private void saveUserAndCity() {
        AppRes.setCity(cityToSave);

        // Tallennetaan kirjautumisaika ja asetetaan käyttäjälle kaupunki
        userToSave.lastLoginTime = System.currentTimeMillis();
        userToSave.city = cityToSave;

        UsersResource.getInstance().editUser(userToSave);
        AppRes.setUser(userToSave);

        AppDataResource.getInstance().getAppData(LoaderFragment.this);
    }

    @Override
    public void onAppDataLoaded() {
        CommentsResource.getInstance().getComments(this);
    }

    @Override
    public void onCityCommentsLoaded(ArrayList<Comment> cityComments) {
        Collections.reverse(cityComments);
        appRes.setCityComments(cityComments);
        EventsResource.getInstance().getEvents(this);
    }

    @Override
    public void onEventsLoaded(ArrayList<Event> events) {
        appRes.setEvents(events);
        Helper.getBarListData(this);
    }

    @Override
    public void onBarListDataLoaded(Map<String, Bar> bars, Map<String, ArrayList<Vote>> votes, Map<String, String> userVotes,
                                    Map<String, ArrayList<Rating>> ratings, Map<String, String> userRatings, Map<String, VoteStat> allTimeVoteStats,
                                    Map<String, RatingStat> allTimeRatingStats, ArrayList<Drink> drinks) {
        appRes.setBarsAndBarNames(bars);
        appRes.setVotes(votes);
        appRes.setUserVotes(userVotes);
        appRes.setRatings(ratings);
        appRes.setUserRatings(userRatings);
        appRes.setAllTimeVoteStats(allTimeVoteStats);
        appRes.setAllTimeRatingStats(allTimeRatingStats);
        appRes.setDrinks(drinks);
        mListener.onMainDataLoaded();
    }

    public interface Listener {
        void onMainDataLoaded();

        void onUserNotFound();
    }

}
