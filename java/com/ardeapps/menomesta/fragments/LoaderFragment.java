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
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FirebaseLoginHandler;
import com.ardeapps.menomesta.handlers.GetAppDataHandler;
import com.ardeapps.menomesta.handlers.GetBarsHandler;
import com.ardeapps.menomesta.handlers.GetCityCommentsHandler;
import com.ardeapps.menomesta.handlers.GetCityNamesHandler;
import com.ardeapps.menomesta.handlers.GetDrinkListHandler;
import com.ardeapps.menomesta.handlers.GetEventVotesHandler;
import com.ardeapps.menomesta.handlers.GetFacebookBarDetailsHandler;
import com.ardeapps.menomesta.handlers.GetFacebookEventsHandler;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.handlers.GetRatingsHandler;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.handlers.GetVotesHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.AppDataResource;
import com.ardeapps.menomesta.resources.BarsResource;
import com.ardeapps.menomesta.resources.CityNamesResource;
import com.ardeapps.menomesta.resources.CommentsResource;
import com.ardeapps.menomesta.resources.DrinksResource;
import com.ardeapps.menomesta.resources.EventVotesResource;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RatingsResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesResource;
import com.ardeapps.menomesta.services.FacebookService;
import com.ardeapps.menomesta.services.FirebaseAuthService;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.Loader;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.menomesta.PrefRes.CITY;

public class LoaderFragment extends Fragment implements FirebaseLoginHandler, GetUserHandler, GetCityNamesHandler {

    public interface Listener {
        void onMainDataLoaded();

        void onUserNotFound();
    }

    Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    AppRes appRes = (AppRes) AppRes.getContext();
    TextView addCityText;
    Spinner citySpinner;
    Button saveCityButton;
    RelativeLayout selectCityContent;

    User userToSave;
    String cityToSave;

    private int callsCompleted = 0;
    private static final int callsToMake = 9;

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
            FirebaseAuthService.getInstance().logInToFirebase(this);

        return v;
    }

    @Override
    public void onFirebaseLoginSuccess(String userId) {
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
                        AddCityDialogFragment dialog = new AddCityDialogFragment();
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

        loadMainData();
    }

    private void loadMainData() {
        Loader.showPermanent();
        callsCompleted = 0;
        AppDataResource.getInstance().getAppData(new GetAppDataHandler() {
            @Override
            public void onAppDataLoaded(String facebookAppToken, String currentAppVersion) {
                appRes.setCurrentAppVersion(currentAppVersion);

                // Sovelluksen access token
                final String appAccessToken = getString(R.string.facebook_app_id) + "|" + facebookAppToken;
                AccessToken token = new AccessToken(appAccessToken, getString(R.string.facebook_app_id), getString(R.string.facebook_app_id),
                        null, null, null, null, null);
                FbRes.setAppAccessToken(token);
                BarsResource.getInstance().getBars(new GetBarsHandler() {
                    @Override
                    public void onBarsLoaded(final Map<String, Bar> bars) {
                        appRes.setBars(bars);
                        ArrayList<String> facebookIds = new ArrayList<>();
                        final Map<Bar, String> barFacebookIds = new HashMap<>();
                        for (final Bar bar : bars.values()) {
                            if (!StringUtils.isEmptyString(bar.facebookId)) {
                                barFacebookIds.put(bar, bar.facebookId);
                            }
                        }
                        FbRes.setBarFacebookIds(barFacebookIds);
                        FacebookService.getInstance().getEvents(barFacebookIds, new GetFacebookEventsHandler() {
                            @Override
                            public void onFacebookEventsLoaded(Map<String, ArrayList<Event>> facebookEvents) {
                                FbRes.setEvents(facebookEvents);
                                checkIfMainDataLoaded();
                            }
                        });
                        FacebookService.getInstance().getBarDetails(barFacebookIds, new GetFacebookBarDetailsHandler() {
                            @Override
                            public void onFacebookBarDetailsLoaded(ArrayList<String> unsupportedBars, Map<String, FacebookBarDetails> facebookBarDetails) {
                                FbRes.setUnsupportedBars(unsupportedBars);
                                FbRes.setBarDetails(facebookBarDetails);
                                checkIfMainDataLoaded();
                            }
                        });
                    }
                });
            }
        });
        CommentsResource.getInstance().getComments(new GetCityCommentsHandler() {
            @Override
            public void onCityCommentsLoaded(ArrayList<Comment> cityComments) {
                Collections.reverse(cityComments);
                appRes.setCityComments(cityComments);
                checkIfMainDataLoaded();
            }
        });
        VotesResource.getInstance().getVotes(new GetVotesHandler() {
            @Override
            public void onVotesLoaded(final Map<String, ArrayList<Vote>> votes, final Map<String, String> userVotes) {
                appRes.setVotes(votes);
                appRes.setUserVotes(userVotes);
                checkIfMainDataLoaded();
            }
        });
        RatingsResource.getInstance().getRatings(new GetRatingsHandler() {
            @Override
            public void onRatingsLoaded(final Map<String, ArrayList<Rating>> ratings, final Map<String, String> userRatings) {
                appRes.setRatings(ratings);
                appRes.setUserRatings(userRatings);
                checkIfMainDataLoaded();
            }
        });
        VoteStatsResource.getInstance().getAllTimeVoteStats(new GetVoteStatsHandler() {
            @Override
            public void onVoteStatsLoaded(final Map<String, VoteStat> allTimeVoteStats) {
                appRes.setAllTimeVoteStats(allTimeVoteStats);
                checkIfMainDataLoaded();
            }
        });
        RatingStatsResource.getInstance().getAllTimeRatingStats(new GetRatingStatsHandler() {
            @Override
            public void onRatingStatsLoaded(final Map<String, RatingStat> allTimeRatingStats) {
                appRes.setAllTimeRatingStats(allTimeRatingStats);
                checkIfMainDataLoaded();
            }
        });
        DrinksResource.getInstance().getAllDrinks(new GetDrinkListHandler() {
            @Override
            public void onDrinkListLoaded(final ArrayList<Drink> drinks) {
                appRes.setDrinks(drinks);
                checkIfMainDataLoaded();
            }
        });
        EventVotesResource.getInstance().getEventVotes(new GetEventVotesHandler() {
            @Override
            public void onEventVotesLoaded(Map<String, ArrayList<EventVote>> eventVotes, Map<String, String> userEventVotes) {
                appRes.setEventVotes(eventVotes);
                appRes.setUserEventVotes(userEventVotes);
                checkIfMainDataLoaded();
            }
        });
    }

    private void checkIfMainDataLoaded() {
        if (++callsCompleted == callsToMake) {
            Loader.hidePermanent();
            mListener.onMainDataLoaded();
        }
    }
}
