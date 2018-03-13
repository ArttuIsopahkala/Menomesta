package com.ardeapps.menomesta;

import android.content.SharedPreferences;

import com.ardeapps.menomesta.objects.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Arttu on 26.2.2018.
 */

public class PrefRes {
    // App
    public static final String IS_APP_VISIBLE = "isAppVisible";
    public static final String APP_STARTED_FIRST_TIME = "appStartedFirstTime";
    // Credentials
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String CITY = "city";
    // Info page
    public static final String RATE_TIME = "rateTime";
    public static final String LIKE_TIME = "likeTime";
    public static final String FEEDBACK_TIME = "feedbackTime";
    // Notification service
    public static final String SESSION_IDS = "sessionIds";
    public static final String USER_COMMENT_IDS = "userCommentIds";
    public static final String LAST_SESSION_ID = "lastSessionId";
    public static final String LAST_REPLY_ID = "lastReplyId";
    public static final String LAST_COMMENT_ID = "lastCommentId";
    public static final String LAST_EVENT_ID = "lastEventId";
    public static final String LAST_USER_LOOKING_FOR_USER_ID = "lastUserLookingForUserId";
    public static final String LAST_NOTIFICATION = "lastNotification";
    // Notifications
    public static final String PRIVATE_NOTIFICATIONS = "privateNotifications";
    public static final String REPLY_NOTIFICATIONS = "replyNotifications";
    public static final String COMMENT_NOTIFICATIONS = "commentNotifications";
    public static final String EVENT_NOTIFICATIONS = "eventNotifications";
    public static final String COMPANY_NOTIFICATIONS = "companyNotifications";
    public static final String VIBRATE_NOTIFICATIONS = "vibrateNotifications";
    private static final String APP_PREF = "app_pref";
    // User
    private static final String USER_ID = "userId";
    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";
    private static final String CREATION_TIME = "creationTime";
    private static final String LAST_LOGIN_TIME = "lastLoginTime";
    private static final String KARMA = "karma";
    private static final String IS_LOOKING_FOR = "isLookingFor";
    private static final String PREMIUM = "premium";

    private static SharedPreferences getSharedPref() {
        return AppRes.getContext().getSharedPreferences(APP_PREF, 0);
    }

    public static User getUser() {
        SharedPreferences pref = getSharedPref();
        User user = new User();
        user.userId = getString(USER_ID);
        user.gender = getString(GENDER);
        user.birthday = getLong(BIRTHDAY);
        user.creationTime = getLong(CREATION_TIME);
        user.lastLoginTime = getLong(LAST_LOGIN_TIME);
        user.karma = getLong(KARMA);
        user.isLookingFor = getBoolean(IS_LOOKING_FOR);
        user.premium = getBoolean(PREMIUM);
        return user;
    }

    public static void setUser(User user) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        putString(USER_ID, user.userId);
        putString(GENDER, user.gender);
        putLong(BIRTHDAY, user.birthday);
        putLong(CREATION_TIME, user.creationTime);
        putLong(LAST_LOGIN_TIME, user.lastLoginTime);
        putLong(KARMA, user.karma);
        putBoolean(IS_LOOKING_FOR, user.isLookingFor);
        putBoolean(PREMIUM, user.premium);
        editor.apply();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return getSharedPref().getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return getSharedPref().getBoolean(key, false);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key) {
        return getSharedPref().getLong(key, 0);
    }

    public static void putStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static Set<String> getStringSet(String key) {
        Set<String> emptySet = new HashSet<>();
        return getSharedPref().getStringSet(key, emptySet);
    }
}
