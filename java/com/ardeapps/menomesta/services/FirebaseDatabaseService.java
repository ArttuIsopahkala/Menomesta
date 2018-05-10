package com.ardeapps.menomesta.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.BuildConfig;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.AddSuccessListener;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FirebaseLoginHandler;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.views.Loader;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.ardeapps.menomesta.PrefRes.EMAIL;
import static com.ardeapps.menomesta.PrefRes.PASSWORD;
import static com.ardeapps.menomesta.PrefRes.TOKEN;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseDatabaseService {
    public static final int messageLimit = 20;
    public static final int barCommentLimit = 100;
    protected static final String APP_DATA = "appData";
    protected static final String BAR_REQUESTS = "barRequests";
    protected static final String CITY_NAMES = "cityNames";
    protected static final String PRIVATE_MESSAGES = "privateMessages";
    protected static final String PRIVATE_SESSIONS = "privateSessions";
    protected static final String CITY_REQUESTS = "cityRequests";
    protected static final String USERS = "users";
    protected static final String CITIES = "cities";
    protected static final String FEED_BACKS = "feedBacks";
    protected static final String NOTIFICATIONS = "notifications";

    protected static final String KARMA_POINTS = "karmaPoints";
    protected static final String REPORT_COUNTS = "reportCounts";
    protected static final String BARS = "bars";
    protected static final String BAR_COMMENTS = "barComments"; // Vanhentunut
    protected static final String REVIEWS = "reviews";
    protected static final String DRINKS = "drinks";
    protected static final String BAR_DETAILS = "barDetails";
    protected static final String COMMENTS = "comments";
    protected static final String REPLIES = "replies";
    protected static final String USERS_LOOKING_FOR_COMPANY = "usersLookingForCompany";
    protected static final String RATINGS = "ratings";
    protected static final String VOTES = "votes";
    protected static final String EVENT_VOTES = "eventVotes";
    protected static final String VOTE_STATS = "voteStats";
    protected static final String RATING_STATS = "ratingStats";
    protected static final String VOTES_LOG = "votesLog";
    protected static final String RATINGS_LOG = "ratingsLog";
    protected static final String ALL_TIME = "allTime";
    protected static final String THIS_WEEK = "thisWeek";
    protected static final String KARMA = "karma";
    private static final String DEBUG = "DEBUG";
    private static final String RELEASE = "RELEASE";

    private static final String FACEBOOK = "facebook.com";

    protected static DatabaseReference getDatabase() {
        if (BuildConfig.DEBUG) {
            return FirebaseDatabase.getInstance().getReference().child(DEBUG);
        } else {
            return FirebaseDatabase.getInstance().getReference().child(RELEASE);
        }
    }

    private static void onNetworkError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_network);
    }

    private static void onDatabaseError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_database);
    }

    private static void logAction() {
        String callingClass = Thread.currentThread().getStackTrace()[4].getFileName();
        int lineNumber = Thread.currentThread().getStackTrace()[4].getLineNumber();
        String callingMethod = Thread.currentThread().getStackTrace()[4].getMethodName();
        Logger.log(callingClass + ":" + lineNumber + " - " + callingMethod);
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppRes.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * This method is used to add data to database without showing any loaders or callbacks
     *
     * @param database reference
     * @param object   value to add
     */
    protected static void addData(final DatabaseReference database, Object object) {
        logAction();
        if (isNetworkAvailable()) {
            database.setValue(object).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Logger.log(e.getMessage() + AppRes.getContext().getString(R.string.error_service_action));
                }
            });
        } else onNetworkError();
    }

    protected static void addData(final DatabaseReference database, Object object, final AddSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            database.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Loader.hide();
                    if (task.isSuccessful()) {
                        handler.onAddSuccess(database.getKey());
                    } else {
                        Logger.toast(R.string.error_service_action);
                    }
                }
            });
        } else onNetworkError();
    }

    /**
     * This method is used to set data to database without showing any loaders or callbacks
     *
     * @param database reference
     * @param object   value to set
     */
    protected static void editData(DatabaseReference database, Object object) {
        logAction();
        if (isNetworkAvailable()) {
            database.setValue(object).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Logger.log(e.getMessage() + AppRes.getContext().getString(R.string.error_service_action));
                }
            });
        } else onNetworkError();
    }

    protected static void editData(DatabaseReference database, Object object, final EditSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            database.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Loader.hide();
                    if (task.isSuccessful()) {
                        handler.onEditSuccess();
                    } else {
                        Logger.toast(R.string.error_service_action);
                    }
                }
            });
        } else onNetworkError();
    }

    /**
     * Käytetään update metodeissa. Ei näytetä loaderia tai virheviestejä.
     */
    protected static void getDataAnonymously(DatabaseReference database, final GetDataSuccessListener handler) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handler.onGetDataSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected static void getData(DatabaseReference database, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Loader.hide();
                    handler.onGetDataSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onDatabaseError();
                }
            });
        } else onNetworkError();
    }

    protected static void getData(Query query, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Loader.hide();
                    handler.onGetDataSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onDatabaseError();
                }
            });
        } else onNetworkError();
    }

    protected static void setChildAddListener(Query query, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    handler.onGetDataSuccess(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onDatabaseError();
                }
            });
        } else onNetworkError();
    }

    protected static void setChildRemoveListener(Query query, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    handler.onGetDataSuccess(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onDatabaseError();
                }
            });
        } else onNetworkError();
    }

    protected static void setDataChangeListener(Query query, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    handler.onGetDataSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onDatabaseError();
                }
            });
        } else onNetworkError();
    }

    protected interface GetDataSuccessListener {
        void onGetDataSuccess(DataSnapshot dataSnapshot);
    }
}
