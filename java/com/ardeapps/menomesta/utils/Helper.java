package com.ardeapps.menomesta.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.GetBarListDataHandler;
import com.ardeapps.menomesta.handlers.GetBarsHandler;
import com.ardeapps.menomesta.handlers.GetDrinkListHandler;
import com.ardeapps.menomesta.handlers.GetEventVotesHandler;
import com.ardeapps.menomesta.handlers.GetRatingStatsHandler;
import com.ardeapps.menomesta.handlers.GetRatingsHandler;
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
import com.ardeapps.menomesta.objects.Review;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.resources.BarsResource;
import com.ardeapps.menomesta.resources.DrinksResource;
import com.ardeapps.menomesta.resources.EventVotesResource;
import com.ardeapps.menomesta.resources.RatingStatsResource;
import com.ardeapps.menomesta.resources.RatingsResource;
import com.ardeapps.menomesta.resources.VoteStatsResource;
import com.ardeapps.menomesta.resources.VotesResource;
import com.ardeapps.menomesta.services.NotificationService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Arttu on 29.1.2018.
 */

public class Helper {
    private static final String MON = "mon";
    private static final String TUE = "tue";
    private static final String WED = "wed";
    private static final String THU = "thu";
    private static final String FRI = "fri";
    private static final String SAT = "sat";
    private static final String SUN = "sun";

    private static List<String> days = Arrays.asList(MON, TUE, WED, THU, FRI, SAT, SUN);

    public static boolean isOpenToday(Map<String, String> hours) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String day = dayFormat.format(calendar.getTime()).toLowerCase();
        String open = hours.get(day + "_1_open");
        String close = hours.get(day + "_1_close");
        return open != null && close != null;
    }

    public static boolean isOpenNow(Map<String, String> hours) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6) {
            calendar.add(Calendar.DATE, -1);
        }
        String day = dayFormat.format(calendar.getTime()).toLowerCase();
        String open = hours.get(day + "_1_open");
        String close = hours.get(day + "_1_close");
        Calendar start = getCalendarFromHour(open);
        Calendar end = getCalendarFromHour(close);
        if(start != null && end != null) {
            if (end.getTimeInMillis() < start.getTimeInMillis()) {
                end.add(Calendar.DAY_OF_MONTH, 1);
            }
            long now = System.currentTimeMillis();
            return start.getTimeInMillis() < now && now < end.getTimeInMillis();
        }
        return false;
    }

    private static Calendar getCalendarFromHour(String time) {
        if(!StringUtils.isEmptyString(time)) {
            String[] values = time.split(":");
            if (values.length == 2) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(values[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(values[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal;
            }
        }
        return null;
    }

    public static String getTodayOpenText(String prefix, Map<String, String> hours, boolean includeCloseTime) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String day = dayFormat.format(calendar.getTime()).toLowerCase();
        String time = getHoursString(hours, day, includeCloseTime);
        if(!StringUtils.isEmptyString(time)) {
            return prefix + " " + time;
        }
        return "";
    }

    public static ArrayList<String> getFacebookHoursList(Map<String, String> hours) {
        ArrayList<String> openingHours = new ArrayList<>();
        for(String day : days) {
            String dayString = "";
            String hourString = getHoursString(hours, day, true);
            Context ctx = AppRes.getContext();
            if(day.equals(MON)) {
                dayString = ctx.getString(R.string.monday);
            } else if(day.equals(TUE)) {
                dayString = ctx.getString(R.string.tuesday);
            } else if(day.equals(WED)) {
                dayString = ctx.getString(R.string.wednesday);
            } else if(day.equals(THU)) {
                dayString = ctx.getString(R.string.thursday);
            } else if(day.equals(FRI)) {
                dayString = ctx.getString(R.string.friday);
            } else if(day.equals(SAT)) {
                dayString = ctx.getString(R.string.saturday);
            } else if(day.equals(SUN)) {
                dayString = ctx.getString(R.string.sunday);
            }
            if (!StringUtils.isEmptyString(hourString)) {
                dayString += ": " + hourString;
            } else {
                dayString += ": " + ctx.getString(R.string.bar_details_closed);
            }
            openingHours.add(dayString);
        }
        return openingHours;
    }

    private static String getHoursString(Map<String, String> hours, String day, boolean includeCloseTime) {
        String open = "_1_open";
        String close = "_1_close";
        String dayString = "";
        String hourString = "";
        if(hours.get(day + open) != null) {
            hourString = hours.get(day + open);
            if(includeCloseTime && hours.get(day + close) != null) {
                hourString += "-" + hours.get(day + close);
            }
        }
        return hourString;
    }

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

    public static boolean hasUserAddedReviewToday(ArrayList<Review> reviews) {
        for(Review review : reviews) {
            if(DateUtil.isToday(review.time) && review.userId.equals(AppRes.getUser().userId)) {
                return true;
            }
        }
        return false;
    }

    public static Event getEventByBarId(ArrayList<Event> events, String barId) {
        // Lähin tapahtuma on ensin
        for (Event event : events) {
            if (barId.equals(event.barId)) {
                return event;
            }
        }
        return null;
    }

    public static int getPositionVoted(Map<String, FacebookBarDetails> details, String barId) {
        List<Map.Entry<String, FacebookBarDetails>> list = new LinkedList<>(details.entrySet());

        Collections.sort(list, FacebookBarDetails.wereHereComparator);

        int position = 1;
        for (Map.Entry<String, FacebookBarDetails> entry : list) {
            if(entry.getKey().equals(barId)) {
                return position;
            }
            position++;
        }
        return position;
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

    private static int callsCompleted = 0;
    private static final int callsToMake = 7;
    public static void getBarListData(final GetBarListDataHandler handler) {
        callsCompleted = 0;
        final AppRes appRes = (AppRes) AppRes.getContext();
        BarsResource.getInstance().getBars(new GetBarsHandler() {
            @Override
            public void onBarsLoaded(final Map<String, Bar> bars) {
                appRes.setBars(bars);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        VotesResource.getInstance().getVotes(new GetVotesHandler() {
            @Override
            public void onVotesLoaded(final Map<String, ArrayList<Vote>> votes, final Map<String, String> userVotes) {
                appRes.setVotes(votes);
                appRes.setUserVotes(userVotes);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        RatingsResource.getInstance().getRatings(new GetRatingsHandler() {
            @Override
            public void onRatingsLoaded(final Map<String, ArrayList<Rating>> ratings, final Map<String, String> userRatings) {
                appRes.setRatings(ratings);
                appRes.setUserRatings(userRatings);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        VoteStatsResource.getInstance().getAllTimeVoteStats(new GetVoteStatsHandler() {
            @Override
            public void onVoteStatsLoaded(final Map<String, VoteStat> allTimeVoteStats) {
                appRes.setAllTimeVoteStats(allTimeVoteStats);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        RatingStatsResource.getInstance().getAllTimeRatingStats(new GetRatingStatsHandler() {
            @Override
            public void onRatingStatsLoaded(final Map<String, RatingStat> allTimeRatingStats) {
                appRes.setAllTimeRatingStats(allTimeRatingStats);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        DrinksResource.getInstance().getAllDrinks(new GetDrinkListHandler() {
            @Override
            public void onDrinkListLoaded(final ArrayList<Drink> drinks) {
                appRes.setDrinks(drinks);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
            }
        });
        EventVotesResource.getInstance().getEventVotes(new GetEventVotesHandler() {
            @Override
            public void onEventVotesLoaded(Map<String, ArrayList<EventVote>> eventVotes, Map<String, String> userEventVotes) {
                appRes.setEventVotes(eventVotes);
                appRes.setUserEventVotes(userEventVotes);
                if (++callsCompleted == callsToMake) {
                    handler.onBarListDataLoaded();
                }
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
