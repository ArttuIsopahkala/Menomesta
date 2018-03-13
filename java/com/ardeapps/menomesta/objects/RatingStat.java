package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by Arttu on 15.9.2016.
 */
public class RatingStat {
    @Exclude
    public static Comparator<RatingStat> customRatingComparator = new Comparator<RatingStat>() {
        @Override
        public int compare(final RatingStat entry1, final RatingStat entry2) {
            final double value1 = entry1.getRating();
            final double value2 = entry2.getRating();
            if (value1 == value2) {
                return entry1.barId.compareTo(entry2.barId);
            }
            return value1 > value2 ? -1 : 1;
        }
    };
    public String barId;
    public double ratingSum;
    public long ratingCount;
    public long updateTime;

    public RatingStat() {
        // Default constructor required for calls to DataSnapshot.getValue(RatingStat.class)
    }

    @Exclude
    public double getRating() {
        if (ratingCount > 0) {
            return ratingSum / ratingCount;
        } else {
            return 0;
        }
    }
}
