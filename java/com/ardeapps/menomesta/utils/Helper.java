package com.ardeapps.menomesta.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.GetBarListDataHandler;
import com.ardeapps.menomesta.handlers.GetBarsHandler;
import com.ardeapps.menomesta.handlers.GetDrinkListHandler;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.handlers.GetRatingsHandler;
import com.ardeapps.menomesta.handlers.GetVoteStatsHandler;
import com.ardeapps.menomesta.handlers.GetVotesHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.BarsResource;
import com.ardeapps.menomesta.resources.DrinksResource;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RatingsResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesResource;
import com.ardeapps.menomesta.services.NotificationService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Arttu on 29.1.2018.
 */

public class Helper {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(float sp) {
        return (int) (sp / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static boolean hasUserSentCommentInMinute(ArrayList<Comment> comments) {
        return comments != null && comments.size() > 0 && comments.get(0).userId.equals(AppRes.getUser().userId) && comments.get(0).time > System.currentTimeMillis() - 60000;
    }

/*    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }*/

    public static Event getEventByName(ArrayList<Event> events, String barName) {
        for (Event event : events) {
            if (barName.equals(event.address)) {
                return event;
            }
        }
        return null;
    }

    public static Bar getBarFromAddress(Collection<Bar> bars, String address) {
        for (Bar bar : bars) {
            if (address.equals(bar.name)) {
                return bar;
            }
        }
        return null;
    }

    public static void stopNotificationService() {
        if (isMyServiceRunning(NotificationService.class)) {
            Intent serviceIntent = new Intent(AppRes.getContext(), NotificationService.class);
            AppRes.getContext().stopService(serviceIntent);
        }
    }

    public static void startNotificationService() {
        if (!isMyServiceRunning(NotificationService.class)) {
            Intent serviceIntent = new Intent(AppRes.getContext(), NotificationService.class);
            AppRes.getContext().startService(serviceIntent);
        }
    }

    private static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) AppRes.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void getBarListData(final GetBarListDataHandler handler) {
        final Handler timeHandler = new Handler();
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.toast(R.string.error_network_waiting);
            }
        }, 10000);
        BarsResource.getInstance().getBars(new GetBarsHandler() {
            @Override
            public void onBarsLoaded(final Map<String, Bar> bars) {
                VotesResource.getInstance().getVotes(new GetVotesHandler() {
                    @Override
                    public void onVotesLoaded(final Map<String, ArrayList<Vote>> votes, final Map<String, String> userVotes) {
                        RatingsResource.getInstance().getRatings(new GetRatingsHandler() {
                            @Override
                            public void onRatingsLoaded(final Map<String, ArrayList<Rating>> ratings, final Map<String, String> userRatings) {
                                VoteStatsResource.getInstance().getAllTimeVoteStats(new GetVoteStatsHandler() {
                                    @Override
                                    public void onVoteStatsLoaded(final Map<String, VoteStat> allTimeVoteStats) {
                                        RatingStatsResource.getInstance().getAllTimeRatingStats(new GetRatingStatsHandler() {
                                            @Override
                                            public void onRatingStatsLoaded(final Map<String, RatingStat> allTimeRatingStats) {
                                                DrinksResource.getInstance().getAllDrinks(new GetDrinkListHandler() {
                                                    @Override
                                                    public void onDrinkListLoaded(final ArrayList<Drink> drinks) {
                                                        timeHandler.removeCallbacksAndMessages(null);
                                                        handler.onBarListDataLoaded(bars, votes, userVotes, ratings, userRatings, allTimeVoteStats, allTimeRatingStats, drinks);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public static void showKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) AppRes.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static void hideKeyBoard(View tokenView) {
        InputMethodManager imm = (InputMethodManager) AppRes.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(tokenView.getWindowToken(), 0);
        }
    }

    public static LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(AppRes.getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            p1 = new LatLng(lat, lng);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    public static String getCityNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(AppRes.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                if (!StringUtils.isEmptyString(address.getLocality())) {
                    return address.getLocality();
                } else {
                    return "";
                }
            }
            return "";
        } catch (IOException ex) {
            return "";
        }
    }
}
