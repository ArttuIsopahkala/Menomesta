package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by Arttu on 15.9.2016.
 */
public class VoteStat {
    @Exclude
    public static Comparator<VoteStat> customVoteComparator = new Comparator<VoteStat>() {
        @Override
        public int compare(final VoteStat entry1, final VoteStat entry2) {
            final long value1 = entry1.voteCount;
            final long value2 = entry2.voteCount;
            if (value1 == value2) {
                return entry1.barId.compareTo(entry2.barId);
            }
            return value1 > value2 ? -1 : 1;
        }
    };
    public String barId;
    public int voteCount;
    public int femaleCount;
    public int maleCount;
    public long ageSum;
    public long updateTime;

    public VoteStat() {
        // Default constructor required for calls to DataSnapshot.getValue(VoteStat.class)
    }

    @Exclude
    public long getAverageAge() {
        if (voteCount > 0) {
            return ageSum / voteCount;
        } else {
            return 0;
        }
    }
}
