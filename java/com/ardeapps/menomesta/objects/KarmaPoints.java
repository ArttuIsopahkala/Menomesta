package com.ardeapps.menomesta.objects;

/**
 * Created by Arttu on 19.2.2017.
 */
public class KarmaPoints {
    public static long VOTED;
    public static long COMMENTED_CITY;
    public static long COMMENTED_REPLY;
    public static long COMMENTED_BAR; // Vanhentunut
    public static long STARS_ADDED; // Vanhentunut
    public static long REVIEWED_BAR;
    public static long LOGGED_IN;
    public static long DRINK_ADDED;
    public static long DRINK_UPDATED;
    public static long DETAILS_UPDATED;
    public static long BEER_BUTTON_PRESSED;
    public static long APP_RATED;
    public static long FACEBOOK_LIKED;
    public static long FEEDBACK_GAVE;
    public static long POINTS_TO_PREMIUM;
    public static long COMMENT_LIKED;
    public static long COMMENT_REPORTED;
    public static long PERCENT_TO_BELONG_PREMIUM;
    public static long EVENT_ADDED; // Vanhentunut
    public static long EVENT_REPORTED; // Vanhentunut
    public static String CODE_TO_PREMIUM;

    public KarmaPoints() {
        // Default constructor required for calls to DataSnapshot.getValue(KarmaPoints.class)
    }

}
