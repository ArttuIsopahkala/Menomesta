package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.handlers.GetAppDataHandler;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class AppDataResource extends FirebaseDatabaseService {
    private static AppDataResource instance;
    private static DatabaseReference database;

    public static AppDataResource getInstance() {
        if (instance == null) {
            instance = new AppDataResource();
        }
        database = getDatabase().child(APP_DATA);
        return instance;
    }

    /**
     * Get app data and set it to application.
     */
    public void getAppData(final GetAppDataHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                DataSnapshot karmaPoints = dataSnapshot.child(KARMA_POINTS);
                KarmaPoints.VOTED = (long) karmaPoints.child("VOTED").getValue();
                KarmaPoints.LOGGED_IN = (long) karmaPoints.child("LOGGED_IN").getValue();
                KarmaPoints.COMMENTED_CITY = (long) karmaPoints.child("COMMENTED_CITY").getValue();
                KarmaPoints.COMMENTED_REPLY = (long) karmaPoints.child("COMMENTED_REPLY").getValue();
                KarmaPoints.STARS_ADDED = (long) karmaPoints.child("STARS_ADDED").getValue();
                KarmaPoints.LOGGED_IN = (long) karmaPoints.child("LOGGED_IN").getValue();
                KarmaPoints.DRINK_ADDED = (long) karmaPoints.child("DRINK_ADDED").getValue();
                KarmaPoints.DRINK_UPDATED = (long) karmaPoints.child("DRINK_UPDATED").getValue();
                KarmaPoints.DETAILS_UPDATED = (long) karmaPoints.child("DETAILS_UPDATED").getValue();
                KarmaPoints.BEER_BUTTON_PRESSED = (long) karmaPoints.child("BEER_BUTTON_PRESSED").getValue();
                KarmaPoints.APP_RATED = (long) karmaPoints.child("APP_RATED").getValue();
                KarmaPoints.FACEBOOK_LIKED = (long) karmaPoints.child("FACEBOOK_LIKED").getValue();
                KarmaPoints.FEEDBACK_GAVE = (long) karmaPoints.child("FEEDBACK_GAVE").getValue();
                KarmaPoints.POINTS_TO_PREMIUM = (long) karmaPoints.child("POINTS_TO_PREMIUM").getValue();
                KarmaPoints.COMMENT_LIKED = (long) karmaPoints.child("COMMENT_LIKED").getValue();
                KarmaPoints.COMMENT_REPORTED = (long) karmaPoints.child("COMMENT_REPORTED").getValue();
                KarmaPoints.PERCENT_TO_BELONG_PREMIUM = (long) karmaPoints.child("PERCENT_TO_BELONG_PREMIUM").getValue();
                KarmaPoints.REVIEWED_BAR = (long) karmaPoints.child("REVIEWED_BAR").getValue();
                KarmaPoints.CODE_TO_PREMIUM = (String) karmaPoints.child("CODE_TO_PREMIUM").getValue();

                DataSnapshot reportCounts = dataSnapshot.child(REPORT_COUNTS);
                ReportCounts.REPORTS_TO_DELETE_COMMENT = (long) reportCounts.child("REPORTS_TO_DELETE_COMMENT").getValue();
                ReportCounts.REPORTS_TO_DELETE_REPLY = (long) reportCounts.child("REPORTS_TO_DELETE_REPLY").getValue();
                ReportCounts.REPORTS_TO_DELETE_REVIEW = (long) reportCounts.child("REPORTS_TO_DELETE_REVIEW").getValue();

                String facebookAppToken = (String) dataSnapshot.child("FACEBOOK_APP_TOKEN").getValue();
                String currentAppVersion = (String) dataSnapshot.child("CURRENT_APP_VERSION").getValue();
                AppRes.PRIVACY_POLICY_LINK = (String) dataSnapshot.child("PRIVACY_POLICY_LINK").getValue();

                handler.onAppDataLoaded(facebookAppToken, currentAppVersion);
            }
        });
    }
}
