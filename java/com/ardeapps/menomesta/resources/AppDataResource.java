package com.ardeapps.menomesta.resources;

import com.ardeapps.menomesta.handlers.GetAppDataHandler;
import com.ardeapps.menomesta.objects.KarmaPoints;
import com.ardeapps.menomesta.objects.ReportCounts;
import com.ardeapps.menomesta.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class AppDataResource extends FirebaseService {
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
        getData(database.child(KARMA_POINTS), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                KarmaPoints.VOTED = (long) dataSnapshot.child("VOTED").getValue();
                KarmaPoints.LOGGED_IN = (long) dataSnapshot.child("LOGGED_IN").getValue();
                KarmaPoints.COMMENTED_CITY = (long) dataSnapshot.child("COMMENTED_CITY").getValue();
                KarmaPoints.COMMENTED_REPLY = (long) dataSnapshot.child("COMMENTED_REPLY").getValue();
                KarmaPoints.COMMENTED_BAR = (long) dataSnapshot.child("COMMENTED_BAR").getValue();
                KarmaPoints.STARS_ADDED = (long) dataSnapshot.child("STARS_ADDED").getValue();
                KarmaPoints.LOGGED_IN = (long) dataSnapshot.child("LOGGED_IN").getValue();
                KarmaPoints.DRINK_ADDED = (long) dataSnapshot.child("DRINK_ADDED").getValue();
                KarmaPoints.DRINK_UPDATED = (long) dataSnapshot.child("DRINK_UPDATED").getValue();
                KarmaPoints.DETAILS_UPDATED = (long) dataSnapshot.child("DETAILS_UPDATED").getValue();
                KarmaPoints.BEER_BUTTON_PRESSED = (long) dataSnapshot.child("BEER_BUTTON_PRESSED").getValue();
                KarmaPoints.APP_RATED = (long) dataSnapshot.child("APP_RATED").getValue();
                KarmaPoints.FACEBOOK_LIKED = (long) dataSnapshot.child("FACEBOOK_LIKED").getValue();
                KarmaPoints.FEEDBACK_GAVE = (long) dataSnapshot.child("FEEDBACK_GAVE").getValue();
                KarmaPoints.POINTS_TO_PREMIUM = (long) dataSnapshot.child("POINTS_TO_PREMIUM").getValue();
                KarmaPoints.COMMENT_LIKED = (long) dataSnapshot.child("COMMENT_LIKED").getValue();
                KarmaPoints.COMMENT_REPORTED = (long) dataSnapshot.child("COMMENT_REPORTED").getValue();
                KarmaPoints.PERCENT_TO_BELONG_PREMIUM = (long) dataSnapshot.child("PERCENT_TO_BELONG_PREMIUM").getValue();
                KarmaPoints.EVENT_ADDED = (long) dataSnapshot.child("EVENT_ADDED").getValue();
                KarmaPoints.EVENT_REPORTED = (long) dataSnapshot.child("EVENT_REPORTED").getValue();
                KarmaPoints.CODE_TO_PREMIUM = (String) dataSnapshot.child("CODE_TO_PREMIUM").getValue();

                getData(database.child(REPORT_COUNTS), new GetDataSuccessListener() {
                    @Override
                    public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                        ReportCounts.REPORTS_TO_DELETE_COMMENT = (long) dataSnapshot.child("REPORTS_TO_DELETE_COMMENT").getValue();
                        ReportCounts.REPORTS_TO_DELETE_REPLY = (long) dataSnapshot.child("REPORTS_TO_DELETE_REPLY").getValue();
                        ReportCounts.REPORTS_TO_DELETE_BAR_COMMENT = (long) dataSnapshot.child("REPORTS_TO_DELETE_BAR_COMMENT").getValue();
                        ReportCounts.REPORTS_TO_DELETE_EVENT = (long) dataSnapshot.child("REPORTS_TO_DELETE_EVENT").getValue();
                        handler.onAppDataLoaded();
                    }
                });
            }
        });
    }
}
