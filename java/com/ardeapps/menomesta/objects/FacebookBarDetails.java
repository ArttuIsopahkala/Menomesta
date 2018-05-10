package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Arttu on 15.9.2016.
 */
public class FacebookBarDetails {
    public String barId;
    public String name;
    public String description;
    public String phone;
    public String priceRange;
    public String website;
    public String about;
    public float overallStarRating;
    public int ratingCount;
    public int wereHereCount;
    public int fanCount;
    public Map<String, String> hours;
    public BarLocation barLocation;

    public static Comparator<Map.Entry<String, FacebookBarDetails>> wereHereComparator = new Comparator<Map.Entry<String, FacebookBarDetails>>() {
        public int compare(Map.Entry<String, FacebookBarDetails> o1, Map.Entry<String, FacebookBarDetails> o2) {
            return o1.getValue().wereHereCount > o2.getValue().wereHereCount ? -1 : 1;
        }
    };

    public static Comparator<FacebookBarDetails> starsComparator = new Comparator<FacebookBarDetails>() {
        @Override
        public int compare(final FacebookBarDetails entry1, final FacebookBarDetails entry2) {
            return entry1.overallStarRating > entry2.overallStarRating ? -1 : 1;
        }
    };
}
