package com.ardeapps.menomesta;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.CompanyMessage;
import com.ardeapps.menomesta.objects.Drink;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.EventVote;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.Rating;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.objects.VoteStat;
import com.ardeapps.menomesta.utils.StringUtils;
import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.menomesta.PrefRes.CITY;

/**
 * Created by Arttu on 4.5.2017.
 */
public class AppRes extends Application {

    public static String PRIVACY_POLICY_LINK;
    private static Context mContext;
    private static User myUser;
    private static String myCity;
    private boolean isAdmin;
    private boolean isUserPremium;
    private ArrayList<String> cityNames = new ArrayList<>();
    private Map<String, Bar> bars = new HashMap<>();
    private Map<String, ArrayList<Vote>> votes = new HashMap<>();
    private Map<String, String> userVotes = new HashMap<>();
    private Map<String, ArrayList<EventVote>> eventVotes = new HashMap<>();
    private Map<String, String> userEventVotes = new HashMap<>();
    private ArrayList<Comment> cityComments = new ArrayList<>();
    private ArrayList<Drink> drinks = new ArrayList<>();
    private Map<String, ArrayList<Rating>> ratings = new HashMap<>();
    private Map<String, String> userRatings = new HashMap<>();
    private Map<String, VoteStat> allTimeVoteStats = new HashMap<>();
    private Map<String, RatingStat> allTimeRatingStats = new HashMap<>();
    private Location location;
    private String currentAppVersion;

    // Ei käytetä
    private CompanyMessage companyMessage;
    private boolean isUsersLookingForCompany;

    public static Context getContext() {
        return mContext;
    }

    public static User getUser() {
        return myUser;
    }

    public static void setUser(User user) {
        myUser = user;

        // Tallennetaan myös lokaalisti
        PrefRes.setUser(user);
    }

    public static String getCity() {
        return myCity;
    }

    public static void setCity(String city) {
        myCity = city;

        // Tallennetaan myös lokaalisti
        PrefRes.putString(CITY, city);
    }

    public void setCurrentAppVersion(String currentAppVersion) {
        this.currentAppVersion = currentAppVersion;
    }

    public String getCurrentAppVersion() {
        return currentAppVersion;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public ArrayList<String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(ArrayList<String> cityNames) {
        this.cityNames = cityNames;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Map<String, RatingStat> getAllTimeRatingStats() {
        return allTimeRatingStats;
    }

    public void setAllTimeRatingStats(Map<String, RatingStat> allTimeRatingStats) {
        this.allTimeRatingStats = allTimeRatingStats;
    }

    public Map<String, VoteStat> getAllTimeVoteStats() {
        return allTimeVoteStats;
    }

    public void setAllTimeVoteStats(Map<String, VoteStat> allTimeVoteStats) {
        this.allTimeVoteStats = allTimeVoteStats;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean getIsUsersLookingForCompany() {
        return isUsersLookingForCompany;
    }

    public void setIsUsersLookingForCompany(boolean isUsersLookingForCompany) {
        this.isUsersLookingForCompany = isUsersLookingForCompany;
    }

    public CompanyMessage getCompanyMessage() {
        return companyMessage;
    }

    public void setCompanyMessage(CompanyMessage companyMessage) {
        this.companyMessage = companyMessage;
    }

    /**
     * BARS
     */
    public void setBars(Map<String, Bar> bars) {
        this.bars = bars;
    }

    public Map<String, Bar> getBars() {
        return bars;
    }

    public void setBar(String barId, Bar bar) {
        bars.put(barId, bar);
    }

    /**
     * VOTES
     */
    public void setVotes(Map<String, ArrayList<Vote>> votes) {
        this.votes = votes;
    }

    public Map<String, String> getUserVotes() {
        return userVotes;
    }

    public void setUserVotes(Map<String, String> userVotes) {
        this.userVotes = userVotes;
    }

    public void setVote(String barId, String voteId, Vote vote) {
        if (vote != null) {
            userVotes.put(barId, vote.voteId);
        } else {
            userVotes.remove(barId);
        }
        ArrayList<Vote> existingVotes = votes.get(barId);
        if (existingVotes != null) {
            for (int index = 0; index < existingVotes.size(); index++) {
                if (existingVotes.get(index).voteId.equals(voteId)) {
                    if (vote == null) {
                        votes.get(barId).remove(index);
                    } else {
                        // Tätä ei koskaan käytetä
                        votes.get(barId).set(index, vote);
                    }
                    return;
                }
            }
            existingVotes.add(vote);
            votes.put(barId, existingVotes);
        } else {
            existingVotes = new ArrayList<>();
            existingVotes.add(vote);
            votes.put(barId, existingVotes);
        }
    }

    public Map<String, ArrayList<Vote>> getVotes() {
        return votes;
    }

    /**
     * EVENT VOTES
     */
    public void setEventVotes(Map<String, ArrayList<EventVote>> eventVotes) {
        this.eventVotes = eventVotes;
    }

    public Map<String, String> getUserEventVotes() {
        return userEventVotes;
    }

    public void setUserEventVotes(Map<String, String> userEventVotes) {
        this.userEventVotes = userEventVotes;
    }

    public void setEventVote(String eventId, String voteId, EventVote eventVote) {
        if (eventVote != null) {
            userVotes.put(eventId, eventVote.voteId);
        } else {
            userVotes.remove(eventId);
        }
        ArrayList<EventVote> existingEventVotes = eventVotes.get(eventId);
        if (existingEventVotes != null) {
            for (int index = 0; index < existingEventVotes.size(); index++) {
                if (existingEventVotes.get(index).voteId.equals(voteId)) {
                    if (eventVote == null) {
                        eventVotes.get(eventId).remove(index);
                    } else {
                        // Tätä ei koskaan käytetä
                        eventVotes.get(eventId).set(index, eventVote);
                    }
                    return;
                }
            }
            existingEventVotes.add(eventVote);
            eventVotes.put(eventId, existingEventVotes);
        } else {
            existingEventVotes = new ArrayList<>();
            existingEventVotes.add(eventVote);
            eventVotes.put(eventId, existingEventVotes);
        }
    }

    public Map<String, ArrayList<EventVote>> getEventVotes() {
        return eventVotes;
    }

    /**
     * RATINGS
     */
    public void setRatings(Map<String, ArrayList<Rating>> ratings) {
        this.ratings = ratings;
    }

    public Map<String, String> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Map<String, String> userRatings) {
        this.userRatings = userRatings;
    }

    public void setRating(String barId, String ratingId, Rating rating) {
        if (rating != null) {
            userRatings.put(barId, rating.ratingId);
        } else {
            userRatings.remove(barId);
        }
        ArrayList<Rating> existingRatings = ratings.get(barId);
        if (existingRatings != null) {
            for (int index = 0; index < existingRatings.size(); index++) {
                if (existingRatings.get(index).ratingId.equals(ratingId)) {
                    if (rating == null) {
                        ratings.get(barId).remove(index);
                    } else {
                        ratings.get(barId).set(index, rating);
                    }
                    return;
                }
            }
            existingRatings.add(rating);
            ratings.put(barId, existingRatings);
        } else {
            // Vain tätä käytetään
            existingRatings = new ArrayList<>();
            existingRatings.add(rating);
            ratings.put(barId, existingRatings);
        }
    }

    public Map<String, ArrayList<Rating>> getRatings() {
        return ratings;
    }

    /**
     * CITY COMMENTS
     */
    public void setCityComments(ArrayList<Comment> cityComments) {
        this.cityComments = cityComments;
    }

    public void setCityComment(String commentId, Comment comment) {
        if (cityComments == null)
            cityComments = new ArrayList<>();
        for (int index = 0; index < cityComments.size(); index++) {
            if (cityComments.get(index).commentId.equals(commentId)) {
                if (comment == null) {
                    cityComments.remove(index);
                } else {
                    cityComments.set(index, comment);
                }
                return;
            }
        }
        if (comment != null) {
            cityComments.add(0, comment);
        }
    }

    public ArrayList<Comment> getCityComments() {
        return cityComments;
    }

    /**
     * DRINKS
     */
    public void setDrinks(ArrayList<Drink> drinks) {
        this.drinks = drinks;
    }

    public void setDrink(String drinkId, Drink drink) {
        if (drinks == null)
            drinks = new ArrayList<>();
        for (int index = 0; index < drinks.size(); index++) {
            if (drinks.get(index).drinkId.equals(drinkId)) {
                if (drink == null) {
                    drinks.remove(index);
                } else {
                    drinks.set(index, drink);
                }
                return;
            }
        }
        if (drink != null) {
            drinks.add(drink);
        }
    }

    public ArrayList<Drink> getDrinks() {
        return drinks;
    }

}
