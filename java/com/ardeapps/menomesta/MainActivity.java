package com.ardeapps.menomesta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.adapters.ViewPagerAdapter;
import com.ardeapps.menomesta.fragments.BarDetailsFragment;
import com.ardeapps.menomesta.fragments.BarRequestFragment;
import com.ardeapps.menomesta.fragments.BarsFragment;
import com.ardeapps.menomesta.fragments.ChatFragment;
import com.ardeapps.menomesta.fragments.CompanyFragment;
import com.ardeapps.menomesta.fragments.ConfirmationDialogFragment;
import com.ardeapps.menomesta.fragments.EditBarFragment;
import com.ardeapps.menomesta.fragments.EventsFragment;
import com.ardeapps.menomesta.fragments.FbBarDetailsFragment;
import com.ardeapps.menomesta.fragments.InfoDialogFragment;
import com.ardeapps.menomesta.fragments.InfoFragment;
import com.ardeapps.menomesta.fragments.LoaderFragment;
import com.ardeapps.menomesta.fragments.MapFragment;
import com.ardeapps.menomesta.fragments.PrivateFragment;
import com.ardeapps.menomesta.fragments.ProfileFragment;
import com.ardeapps.menomesta.fragments.ReplyFragment;
import com.ardeapps.menomesta.fragments.ShowLocationFragment;
import com.ardeapps.menomesta.fragments.StatisticsFragment;
import com.ardeapps.menomesta.fragments.WelcomeFragment;
import com.ardeapps.menomesta.fragments.WriteCommentFragment;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FacebookLoginHandler;
import com.ardeapps.menomesta.handlers.GetBarDetailsHandler;
import com.ardeapps.menomesta.handlers.GetCityCommentHandler;
import com.ardeapps.menomesta.handlers.GetDrinkListHandler;
import com.ardeapps.menomesta.handlers.GetFacebookBarDetailsHandler;
import com.ardeapps.menomesta.handlers.GetFacebookEventsHandler;
import com.ardeapps.menomesta.handlers.GetFanCountHandler;
import com.ardeapps.menomesta.handlers.GetPrivateMessagesHandler;
import com.ardeapps.menomesta.handlers.GetPrivateSessionsHandler;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.handlers.GetRepliesHandler;
import com.ardeapps.menomesta.handlers.GetReviewsHandler;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.handlers.GetUsersLookingForCompanyHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.handlers.IsUsersLookingForCompanyHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewPrivateMessageHandler;
import com.ardeapps.menomesta.handlers.serviceHandlers.NewSessionHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.BarDetails;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Review;
import com.ardeapps.menomesta.objects.Session;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.BarDetailsResource;
import com.ardeapps.menomesta.resources.CommentsResource;
import com.ardeapps.menomesta.resources.DrinksResource;
import com.ardeapps.menomesta.resources.PrivateMessagesResource;
import com.ardeapps.menomesta.resources.PrivateSessionsResource;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RepliesResource;
import com.ardeapps.menomesta.resources.ReviewsResource;
import com.ardeapps.menomesta.resources.UsersLookingForCompanyResource;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.services.FacebookService;
import com.ardeapps.menomesta.services.FirebaseAuthService;
import com.ardeapps.menomesta.services.FragmentListeners;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.Loader;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.CallbackManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.fabric.sdk.android.Fabric;

import static com.ardeapps.menomesta.PrefRes.APP_STARTED_FIRST_TIME;
import static com.ardeapps.menomesta.PrefRes.FACEBOOK_PERMISSION_DENY_CITIES;
import static com.ardeapps.menomesta.PrefRes.IS_APP_VISIBLE;
import static com.ardeapps.menomesta.PrefRes.LAST_COMMENT_ID;
import static com.ardeapps.menomesta.PrefRes.LATEST_APP_VERSION_HANDLED;
import static com.ardeapps.menomesta.PrefRes.SESSION_IDS;
import static com.ardeapps.menomesta.PrefRes.TOKEN;
import static com.ardeapps.menomesta.services.FragmentListeners.MY_PERMISSION_ACCESS_COARSE_LOCATION;

public class MainActivity extends FragmentActivity implements IsUsersLookingForCompanyHandler, NewPrivateMessageHandler, NewSessionHandler {

    //Fragments
    LoaderFragment loaderFragment;
    BarRequestFragment barRequestFragment;
    WelcomeFragment welcomeFragment;
    InfoFragment infoFragment;
    ReplyFragment replyFragment;
    StatisticsFragment statisticsFragment;
    PrivateFragment privateFragment;
    MapFragment mapFragment;
    EditBarFragment editBarFragment;
    CompanyFragment companyFragment;
    ShowLocationFragment showLocationFragment;
    ProfileFragment profileFragment;
    WriteCommentFragment writeCommentFragment;
    BarsFragment barsFragment;
    ChatFragment chatFragment;
    EventsFragment eventsFragment;
    BarDetailsFragment barDetailsFragment;
    FbBarDetailsFragment fbBarDetailsFragment;

    TabLayout tabLayout;
    ViewPager viewPager;
    FrameLayout fragmentContainer;
    TextView cityText;
    TextView dateText;
    ViewPagerAdapter pagerAdapter;
    RelativeLayout loader;
    ImageView loader_spinner;
    ImageView usersLookingMark;
    ImageView newMessageMark;
    LinearLayout menu_bottom;
    LinearLayout facebookPermission;
    Button loginButton;
    TextView unsupportedBarsText;
    AdView mAdView;

    RelativeLayout companyMessageContent; // TODO Seuranhaku-toimintoa vielä toteutettu

    AppRes appRes;
    boolean appStartedFirstTime = true;
    int notificationOptionExtra;
    String notificationExtra;
    private volatile boolean isOnDestroyCalled = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Logger.toast("DEBUG MODE");
        }
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        cityText = (TextView) findViewById(R.id.cityText);
        dateText = (TextView) findViewById(R.id.dateText);
        loader_spinner = (ImageView) findViewById(R.id.loader_spinner);
        usersLookingMark = (ImageView) findViewById(R.id.usersLookingMark);
        newMessageMark = (ImageView) findViewById(R.id.newMessageMark);
        loader = (RelativeLayout) findViewById(R.id.loader);
        menu_bottom = (LinearLayout) findViewById(R.id.menu_bottom);
        facebookPermission = (LinearLayout) findViewById(R.id.facebookPermission);
        loginButton = (Button) findViewById(R.id.loginButton);
        unsupportedBarsText = (TextView) findViewById(R.id.unsupportedBars);
        mAdView = (AdView) findViewById(R.id.adView);
        // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
        companyMessageContent = (RelativeLayout) findViewById(R.id.companyMessageContent);
        companyMessageContent.setVisibility(View.GONE);

        loaderFragment = new LoaderFragment();
        barRequestFragment = new BarRequestFragment();
        welcomeFragment = new WelcomeFragment();
        infoFragment = new InfoFragment();
        replyFragment = new ReplyFragment();
        statisticsFragment = new StatisticsFragment();
        mapFragment = new MapFragment();
        privateFragment = new PrivateFragment();
        editBarFragment = new EditBarFragment();
        companyFragment = new CompanyFragment();
        showLocationFragment = new ShowLocationFragment();
        profileFragment = new ProfileFragment();
        writeCommentFragment = new WriteCommentFragment();
        barsFragment = new BarsFragment();
        chatFragment = new ChatFragment();
        eventsFragment = new EventsFragment();
        barDetailsFragment = new BarDetailsFragment();
        fbBarDetailsFragment = new FbBarDetailsFragment();

        appRes = (AppRes) AppRes.getContext();

        Loader.create(loader, loader_spinner);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        FacebookService.setCallbackManager(callbackManager);
        setListeners();

        appStartedFirstTime = PrefRes.getBoolean(APP_STARTED_FIRST_TIME);
        notificationOptionExtra = getIntent().getIntExtra("notificationOptionExtra", 0);
        notificationExtra = getIntent().getStringExtra("notificationExtra");

        if (appStartedFirstTime) {
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    MainActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();

            // Create Implicit intent and assign Shortcut Application Name, Icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(
                            getApplicationContext(), R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);

            PrefRes.putBoolean(APP_STARTED_FIRST_TIME, false);
        }

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        final boolean locationPermissionNeeded = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (locationPermissionNeeded) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            appRes.setLocation(location);
                        }
                    }
                    openApp();
                }
            });
        }
    }

    private void openApp() {
        String token = PrefRes.getString(TOKEN);
        if (StringUtils.isEmptyString(token)) {
            // Jos shared preferences on tyhjennetty, katsotaan voiko Facebookin avulla edelleen kirjautua.
            // Jos ei voi, tiedot voi edelleen saada uudelleen kun käyttäjä rekisteröityy
            if (FbRes.isUserLoggedIn()) {
                // Tänne ei ehkä koskaan päädytä
                FirebaseAuthService.restoreCredentialsWithFacebook(new FirebaseAuthService.RestoreCredentialsHandler() {
                    @Override
                    public void onRestoreCredentialsWithFacebookSuccess() {
                        FragmentListeners.getInstance().getFragmentChangeListener().goToLoaderFragment();
                    }

                    @Override
                    public void onRestoreCredentialsWithFacebookFailed() {
                        FragmentListeners.getInstance().getFragmentChangeListener().goToWelcomeFragment();
                    }
                });
            } else {
                // Avataan welcomeFragment
                FragmentListeners.getInstance().getFragmentChangeListener().goToWelcomeFragment();
            }
        } else {
            // Avataan loader fragment
            FragmentListeners.getInstance().getFragmentChangeListener().goToLoaderFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FragmentListeners.getInstance().getPermissionHandledListener().onPermissionGranted(requestCode);
        } else {
            FragmentListeners.getInstance().getPermissionHandledListener().onPermissionDenied(requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookService.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * SET FRAGMENT LISTENERS
     */
    private void setListeners() {
        FacebookService.getInstance().setLogInListener(this, loginButton, new FacebookLoginHandler() {
            @Override
            public void onLoginSuccess(String gender, String birthday) {
                FirebaseAuthService.getInstance().linkFacebookIfNeeded(new FirebaseAuthService.LinkFacebookIfNeededHandler() {
                    @Override
                    public void onLinkFacebookIfNeededSuccess() {
                        // Lataa Facebook tapahtumat ja baarien tiedot uudelleen
                        FacebookService.getInstance().getEvents(FbRes.getBarFacebookIds(), new GetFacebookEventsHandler() {
                            @Override
                            public void onFacebookEventsLoaded(Map<String, ArrayList<Event>> facebookEvents) {
                                FbRes.setEvents(facebookEvents);
                                FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshEventsFragment();
                                FacebookService.getInstance().getBarDetails(FbRes.getBarFacebookIds(), new GetFacebookBarDetailsHandler() {
                                    @Override
                                    public void onFacebookBarDetailsLoaded(ArrayList<String> unsupportedBars, Map<String, FacebookBarDetails> facebookBarDetails) {
                                        FbRes.setUnsupportedBars(unsupportedBars);
                                        FbRes.setBarDetails(facebookBarDetails);
                                        FragmentListeners.getInstance().getPageAdapterRefreshListener().refreshBarsFragment();
                                    }
                                });
                            }
                        });
                        facebookPermission.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onLoginFailed() {

            }
        });
        welcomeFragment.setListener(new WelcomeFragment.Listener() {
            @Override
            public void onNewUserSaved() {
                removeAddedFragment();
                FragmentListeners.getInstance().getFragmentChangeListener().goToLoaderFragment();
            }
        });
        FragmentListeners.getInstance().setPermissionHandledListener(new FragmentListeners.PermissionHandledListener() {
            @Override
            public void onPermissionGranted(int MY_PERMISSION) {
                if (MY_PERMISSION == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
                    checkLocationPermission();
                }
            }

            @Override
            public void onPermissionDenied(int MY_PERMISSION) {
                if (MY_PERMISSION == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
                    openApp();
                }
            }
        });
        FragmentListeners.getInstance().setPageAdapterRefreshListener(new FragmentListeners.PageAdapterRefreshListener() {
            @Override
            public void refreshMainActivity() {
                setFacebookLoginHint();
            }

            @Override
            public void refreshEventsFragment() {
                pagerAdapter.updateEvents();
            }

            @Override
            public void refreshBarsFragment() {
                pagerAdapter.updateBarList();
            }

            @Override
            public void refreshChatFragment() {
                pagerAdapter.updateChatList();
            }
        });
        FragmentListeners.getInstance().setFragmentChangeListener(new FragmentListeners.FragmentChangeListener() {

            @Override
            public void goToWelcomeFragment() {
                addFragment(welcomeFragment);
            }

            @Override
            public void goToLoaderFragment() {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if(loaderFragment.isAdded()) {
                    switchToFragment(loaderFragment);
                } else {
                    addFragment(loaderFragment);
                }
            }

            @Override
            public void goToReplyFragment(final Comment comment) {
                RepliesResource.getInstance().getReplies(comment.commentId, new GetRepliesHandler() {
                    @Override
                    public void onRepliesLoaded(ArrayList<Comment> replies) {
                        replies.add(0, comment);
                        replyFragment.setComment(comment);
                        replyFragment.setReplies(replies);
                        switchToFragment(replyFragment);
                    }
                });
            }

            @Override
            public void goToBarDetailsFragment(final Bar bar) {
                DrinksResource.getInstance().getDrinks(bar.barId, new GetDrinkListHandler() {
                    @Override
                    public void onDrinkListLoaded(final ArrayList<Drink> drinks) {
                        BarDetailsResource.getInstance().getBarDetails(bar.barId, new GetBarDetailsHandler() {
                            @Override
                            public void onBarDetailsLoaded(final BarDetails barDetails) {
                                ReviewsResource.getInstance().getReviews(bar.barId, new GetReviewsHandler() {
                                    @Override
                                    public void onReviewsLoaded(ArrayList<Review> reviews) {
                                        Collections.reverse(reviews);
                                        // Katsotaan onko tiedot saatu Facebookista
                                        if (FbRes.getBarDetail(bar.barId) != null) {
                                            fbBarDetailsFragment.setBar(bar);
                                            fbBarDetailsFragment.setDrinks(drinks);
                                            fbBarDetailsFragment.setReviews(reviews);
                                            fbBarDetailsFragment.setBarDetails(barDetails);
                                            fbBarDetailsFragment.setVoteStat(appRes.getAllTimeVoteStats().get(bar.barId));
                                            editBarFragment.setBarDetails(barDetails);
                                            switchToFragment(fbBarDetailsFragment);
                                        } else {
                                            barDetailsFragment.refreshData();
                                            barDetailsFragment.setBar(bar);
                                            barDetailsFragment.setDrinks(drinks);
                                            barDetailsFragment.setReviews(reviews);
                                            barDetailsFragment.setBarDetails(barDetails);
                                            editBarFragment.setBarDetails(barDetails);
                                            switchToFragment(barDetailsFragment);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void goToShowLocationFragment(Bar bar) {
                showLocationFragment.setBar(bar);
                switchToFragment(showLocationFragment);
            }

            @Override
            public void goToPrivateFragment(User recipient, String sessionId, ArrayList<Comment> messages) {
                privateFragment.setRecipient(recipient);
                privateFragment.setSessionId(sessionId);
                privateFragment.setMessages(messages);
                switchToFragment(privateFragment);
            }

            @Override
            public void goToProfileFragment() {
                profileFragment.refreshData();
                switchToFragment(profileFragment);
            }

            @Override
            public void goToStatisticsFragment() {
                VoteStatsResource.getInstance().getAllTimeVoteStats(new GetVoteStatsHandler() {
                    @Override
                    public void onVoteStatsLoaded(final Map<String, VoteStat> allTimeVoteStats) {
                        VoteStatsResource.getInstance().getThisWeekVoteStats(new GetVoteStatsHandler() {
                            @Override
                            public void onVoteStatsLoaded(final Map<String, VoteStat> thisWeekVoteStats) {
                                RatingStatsResource.getInstance().getAllTimeRatingStats(new GetRatingStatsHandler() {
                                    @Override
                                    public void onRatingStatsLoaded(final Map<String, RatingStat> allTimeRatingStats) {
                                        RatingStatsResource.getInstance().getThisWeekRatingStats(new GetRatingStatsHandler() {
                                            @Override
                                            public void onRatingStatsLoaded(final Map<String, RatingStat> thisWeekRatingStats) {
                                                statisticsFragment.refreshData();
                                                statisticsFragment.setStats(allTimeVoteStats, thisWeekVoteStats, allTimeRatingStats, thisWeekRatingStats);
                                                switchToFragment(statisticsFragment);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void goToCompanyFragment() {
                newMessageMark.setVisibility(View.INVISIBLE);
                UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(new GetUsersLookingForCompanyHandler() {
                    @Override
                    public void onUsersLookingForCompanyLoaded(ArrayList<CompanyMessage> usersLookingForCompany) {
                        companyFragment.setUsersLookingForCompany(usersLookingForCompany);
                        PrivateSessionsResource.getInstance().getPrivateSessions(new GetPrivateSessionsHandler() {
                            @Override
                            public void onPrivateSessionsLoaded(ArrayList<Session> sessions) {
                                companyFragment.setSessions(sessions);
                                switchToFragment(companyFragment);
                            }
                        });
                    }
                });
            }

            @Override
            public void goToInfoFragment() {
                FacebookService.getInstance().getFanCount(new GetFanCountHandler() {
                    @Override
                    public void onGetFanCountSuccess(int fanCount) {
                        infoFragment.setFanCount(fanCount);
                        switchToFragment(infoFragment);
                    }
                });
            }

            @Override
            public void goToMapFragment(String barName) {
                mapFragment.refreshData(barName);
                switchToFragment(mapFragment);
            }

            @Override
            public void goToBarRequestFragment() {
                barRequestFragment.refreshData();
                switchToFragment(barRequestFragment);
            }

            @Override
            public void goToEditBarFragment(final Bar bar, final boolean isFacebookBar) {
                DrinksResource.getInstance().getDrinks(bar.barId, new GetDrinkListHandler() {
                    @Override
                    public void onDrinkListLoaded(ArrayList<Drink> drinks) {
                        editBarFragment.setDrinks(drinks);
                        editBarFragment.setBar(bar);
                        editBarFragment.setIsFacebookBar(isFacebookBar);
                        switchToFragment(editBarFragment);
                    }
                });
            }

            @Override
            public void goToWriteCommentFragment(WriteCommentFragment.Listener listener) {
                writeCommentFragment.setListener(listener);
                switchToFragment(writeCommentFragment);
            }
        });

        loaderFragment.setListener(new LoaderFragment.Listener() {
            @Override
            public void onMainDataLoaded() {
                openMainApp();
            }

            @Override
            public void onUserNotFound() {
                FragmentListeners.getInstance().getFragmentChangeListener().goToWelcomeFragment();
            }
        });

        editBarFragment.setListener(new EditBarFragment.Listener() {
            @Override
            public void onDrinksUpdated(ArrayList<Drink> drinks, boolean isFacebookBar) {
                if (isFacebookBar) {
                    fbBarDetailsFragment.setDrinks(drinks);
                    fbBarDetailsFragment.updateDrinks();
                } else {
                    barDetailsFragment.setDrinks(drinks);
                    barDetailsFragment.updateDrinks();
                }
                editBarFragment.setDrinks(drinks);
                editBarFragment.updateDrinks();
            }

            @Override
            public void onBarDetailsUpdated(BarDetails barDetails) {
                barDetailsFragment.setBarDetails(barDetails);
                barDetailsFragment.updateBarDetails();
                editBarFragment.setBarDetails(barDetails);
                editBarFragment.updateBarDetails();
            }

            @Override
            public void onBarUpdated(Bar bar) {
                barDetailsFragment.setBar(bar);
                barDetailsFragment.updateBar();
            }
        });

        privateFragment.setListener(new PrivateFragment.Listener() {
            @Override
            public void onSessionChanged() {
                UsersLookingForCompanyResource.getInstance().getUsersLookingForCompany(new GetUsersLookingForCompanyHandler() {
                    @Override
                    public void onUsersLookingForCompanyLoaded(ArrayList<CompanyMessage> usersLookingForCompany) {
                        companyFragment.setUsersLookingForCompany(usersLookingForCompany);
                        PrivateSessionsResource.getInstance().getPrivateSessions(new GetPrivateSessionsHandler() {
                            @Override
                            public void onPrivateSessionsLoaded(ArrayList<Session> sessions) {
                                companyFragment.setSessions(sessions);
                                companyFragment.update();
                            }
                        });
                    }
                });
            }
        });
    }

    private void removeAddedFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentContainer.setVisibility(View.INVISIBLE);
    }

    public void addFragment(Fragment newFrag) {
        if (!isOnDestroyCalled) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, newFrag)
                    .commitAllowingStateLoss();

            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    public void switchToFragment(Fragment newFrag) {
        if (!isOnDestroyCalled) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newFrag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();

            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    public void openMainApp() {
        removeAddedFragment();

        Crashlytics.setUserIdentifier(AppRes.getUser().userId);

        if (appRes.getIsAdmin() || AppRes.getUser().premium) {
            mAdView.setVisibility(View.GONE);
        } else {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-2033708472283724~3513959379");
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        UsersResource.getInstance().editUser(AppRes.getUser(), new EditSuccessListener() {
            @Override
            public void onEditSuccess() {
                UsersResource.getInstance().updateUserKarma(KarmaPoints.LOGGED_IN, true);
            }
        });

        // Jos versionimi on uusi ja päivitystä ei ole hylätty, näytetään dialogi
        if (!BuildConfig.VERSION_NAME.equals(appRes.getCurrentAppVersion()) && !appRes.getCurrentAppVersion().equals(PrefRes.getString(LATEST_APP_VERSION_HANDLED))) {
            PrefRes.putString(LATEST_APP_VERSION_HANDLED, appRes.getCurrentAppVersion());
            ConfirmationDialogFragment confirm_dialog = ConfirmationDialogFragment.newInstance(AppRes.getContext().getString(R.string.app_new_version_description));
            confirm_dialog.show(getSupportFragmentManager(), "päivitä uusin versio dialogi");
            confirm_dialog.setListener(new ConfirmationDialogFragment.ConfirmationDialogCloseListener() {
                @Override
                public void onDialogYesButtonClick() {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.app_google_play_link)));
                    startActivity(i);
                }
            });
        }

        menu_bottom.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        cityText.setText(AppRes.getCity());
        dateText.setText(StringUtils.getWeekDayText(System.currentTimeMillis()));

        setFacebookLoginHint();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(eventsFragment);
        fragments.add(barsFragment);
        fragments.add(chatFragment);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        pagerAdapter.updateEvents();
        pagerAdapter.updateBarList();
        pagerAdapter.updateChatList();

        if (appRes.getCityComments().size() > 0) {
            PrefRes.putString(LAST_COMMENT_ID, appRes.getCityComments().get(0).commentId);
        }

        // Jos sovellus käynnistettiin ilmoituksen kautta, avataan oikea näkymä
        if (notificationExtra != null) {
            openNotificationOption();
        }

        // TODO Seuranhaku-toimintoa ei ole vielä toteutettu
        // Asetetaan kuuntelijat
        /*UsersLookingForCompanyResource.getInstance().isUsersLookingForCompany(this);
        PrivateMessagesResource.getInstance().isNewPrivateMessages(new ArrayList<>(PrefRes.getStringSet(SESSION_IDS)), this);
        PrivateSessionsResource.getInstance().isNewPrivateSessions(this);*/

        // Asetetaan topicit Firebasen ilmoituksiin
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.VERSION_CODE + "");
        for (int i = 1; i < BuildConfig.VERSION_CODE; i++) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(i + "");
        }
        String city = AppRes.getCity().replace("ä", "a").replace("ö", "o").replace("å", "oe");
        FirebaseMessaging.getInstance().subscribeToTopic(city);

        for (String cityName : appRes.getCityNames()) {
            String cityConverted = cityName.replace("ä", "a").replace("ö", "o").replace("å", "oe");
            if (!cityConverted.equals(city))
                FirebaseMessaging.getInstance().unsubscribeFromTopic(cityConverted);
        }

        // käynnistä service
        Helper.startNotificationService();
    }

    public void setFacebookLoginHint() {
        // Näytä facebook hyväksyntäteksti, jos ei ole aiemmin evätty ja jos on baareja jotka eivät salli näkyä
        ArrayList<String> unsupportedBars = FbRes.getUnsupportedBars();
        if (!FbRes.isUserLoggedIn() && unsupportedBars.size() > 0 && !PrefRes.getStringSet(FACEBOOK_PERMISSION_DENY_CITIES).contains(AppRes.getCity())) {
            facebookPermission.setVisibility(View.VISIBLE);
            unsupportedBarsText.setText(getString(R.string.facebook_description, StringUtils.getUnsupportedBarsText(unsupportedBars)));
        } else {
            facebookPermission.setVisibility(View.GONE);
        }
    }

    // Jos sovellus aukaistaan notificaation kautta
    public void openNotificationOption() {
        switch (notificationOptionExtra) {
            case 1:
                // Siirry vastaukseen
                CommentsResource.getInstance().getComment(notificationExtra, new GetCityCommentHandler() {
                    @Override
                    public void onCityCommentLoaded(Comment comment) {
                        viewPager.setCurrentItem(2);
                        FragmentListeners.getInstance().getFragmentChangeListener().goToReplyFragment(comment);
                    }
                });
                break;
            case 2:
                // Siirry yksityisviestiin
                privateFragment.setSessionId(notificationExtra);
                // käynnistä service
                Set<String> sessionIdsSet = PrefRes.getStringSet(SESSION_IDS);
                sessionIdsSet.add(notificationExtra);
                PrefRes.putStringSet(SESSION_IDS, sessionIdsSet);
                Helper.startNotificationService();

                PrivateMessagesResource.getInstance().getPrivateMessages(notificationExtra, new GetPrivateMessagesHandler() {
                    @Override
                    public void onPrivateMessagesLoaded(final ArrayList<Comment> messages) {
                        String recipientId = messages.get(messages.size() - 1).userId;
                        UsersResource.getInstance().getUser(recipientId, new GetUserHandler() {
                            @Override
                            public void onUserLoaded(User recipient) {
                                FragmentListeners.getInstance().getFragmentChangeListener().goToPrivateFragment(recipient, notificationExtra, messages);
                            }
                        });
                    }
                });
                break;
            case 7:
                // Näytä ilmoitus dialogi
                InfoDialogFragment infoDialog = InfoDialogFragment.newInstance(getString(R.string.notification_title), notificationExtra);
                infoDialog.show(getSupportFragmentManager(), "ilmoitus info");
                break;
            case 8:
                // Näytä tapahtumat
                viewPager.setCurrentItem(0);
                break;
            case 9:
                // Uusia seuranahkijoita
                FragmentListeners.getInstance().getFragmentChangeListener().goToCompanyFragment();
                break;
            default:
                break;
        }
    }

    public void openMap(View view) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToMapFragment(null);
    }

    public void showInfo(View view) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToInfoFragment();
    }

    public void showProfile(View view) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToProfileFragment();
    }

    public void openCompanyFragment(View view) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToCompanyFragment();
    }

    public void addBar(View view) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToBarRequestFragment();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void denyFacebookLoginHint(View view) {
        Set<String> facebookPermissionDenyCities = PrefRes.getStringSet(FACEBOOK_PERMISSION_DENY_CITIES);
        facebookPermissionDenyCities.add(AppRes.getCity());
        PrefRes.putStringSet(FACEBOOK_PERMISSION_DENY_CITIES, facebookPermissionDenyCities);
        facebookPermission.setVisibility(View.GONE);
    }

    @Override
    public void onNewPrivateMessage(String sessionId, Comment privateMessage) {
        if (privateFragment.isVisible())
            privateFragment.addNewMessage(sessionId, privateMessage);

        newMessageMark = (ImageView) findViewById(R.id.newMessageMark);
        if (newMessageMark != null) {
            newMessageMark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNewSession(Session session) {
        Set<String> sessionIdsSet = PrefRes.getStringSet(SESSION_IDS);
        sessionIdsSet.add(session.sessionId);
        PrefRes.putStringSet(SESSION_IDS, sessionIdsSet);
        PrivateMessagesResource.getInstance().isNewPrivateMessages(new ArrayList<>(PrefRes.getStringSet(SESSION_IDS)), this);
        // käynnistä service
        Helper.startNotificationService();

        if (newMessageMark != null)
            newMessageMark.setVisibility(View.VISIBLE);
    }

    @Override
    public void onIsUsersLookingForCompanyChecked(boolean isUsersLookingForCompany) {
        appRes.setIsUsersLookingForCompany(isUsersLookingForCompany);
        if (usersLookingMark != null) {
            if (isUsersLookingForCompany)
                usersLookingMark.setVisibility(View.VISIBLE);
            else
                usersLookingMark.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefRes.putBoolean(IS_APP_VISIBLE, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefRes.putBoolean(IS_APP_VISIBLE, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Pysäytetään service, jotta se käynnistyy uudelleen sovelluksen sulkeutuessa.
        Helper.stopNotificationService();
        isOnDestroyCalled = true;
    }

    @Override
    public void onBackPressed() {
        if (welcomeFragment.isVisible()) {
            super.onBackPressed();
        }

        Helper.hideKeyBoard(getWindow().getDecorView());

        //go to previous fragment or close app if user is on mainscreen
        int backStack = getSupportFragmentManager().getBackStackEntryCount();

        if (backStack > 0) {
            if (backStack == 1) {
                fragmentContainer.setVisibility(View.INVISIBLE);
            }
            getSupportFragmentManager().popBackStack();
        }
        if (backStack == 0) {
            if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
                viewPager.setCurrentItem(1);
            } else super.onBackPressed();
        }
    }
}
