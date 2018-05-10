package com.ardeapps.menomesta;

import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.menomesta.objects.Event.customEventComparator;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FbRes {

    private static Map<Bar, String> fbBarFacebookIds = new HashMap<>();
    private static ArrayList<Event> fbEvents = new ArrayList<>();
    private static Map<String, ArrayList<Event>> fbEventsMap = new HashMap<>();
    private static ArrayList<String> fbUnsupportedBars = new ArrayList<>();
    private static Map<String, FacebookBarDetails> fbBarDetails = new HashMap<>();
    private static AccessToken fbAccessToken;

    // Palauttaa käyttäjän access tokenin, jos on kirjautunut.
    public static AccessToken getAccessToken() {
        return isUserLoggedIn() ? AccessToken.getCurrentAccessToken() : fbAccessToken;
    }

    public static void setAppAccessToken(AccessToken facebookAccessToken) {
        fbAccessToken = facebookAccessToken;
    }

    public static boolean isUserLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    /**
     * BARS
     */
    public static void setBarFacebookIds(Map<Bar, String> barFacebookIds) {
        fbBarFacebookIds = barFacebookIds;
    }

    public static Map<Bar, String> getBarFacebookIds() {
        return fbBarFacebookIds;
    }

    /**
     * UNSUPPORTED BARS
     */
    public static void setUnsupportedBars(ArrayList<String> unsupportedBars) {
        fbUnsupportedBars = unsupportedBars;
    }

    public static ArrayList<String> getUnsupportedBars() {
        return fbUnsupportedBars;
    }

    /**
     * BAR DETAILS
     */
    public static void setBarDetails(Map<String, FacebookBarDetails> barDetails) {
        fbBarDetails = barDetails;
    }

    public static Map<String, FacebookBarDetails> getBarDetails() {
        return fbBarDetails;
    }

    public static FacebookBarDetails getBarDetail(String barId) {
        return fbBarDetails.get(barId);
    }

    /**
     * EVENTS
     */
    public static void setEvents(Map<String, ArrayList<Event>> eventsMap) {
        fbEventsMap = eventsMap;
        ArrayList<Event> events = Event.convertToArrayList(eventsMap);
        Collections.sort(events, customEventComparator);
        fbEvents = events;
    }

    public static ArrayList<Event> getEvents() {
        return fbEvents;
    }

    public static ArrayList<Event> getEvents(String barId) {
        return fbEventsMap.get(barId) != null ? fbEventsMap.get(barId) : new ArrayList<Event>();
    }
}
