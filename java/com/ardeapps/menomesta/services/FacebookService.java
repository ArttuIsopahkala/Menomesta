package com.ardeapps.menomesta.services;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.handlers.FacebookLoginHandler;
import com.ardeapps.menomesta.handlers.GetFacebookBarDetailsHandler;
import com.ardeapps.menomesta.handlers.GetFacebookEventsHandler;
import com.ardeapps.menomesta.handlers.GetFanCountHandler;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.BarLocation;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.views.Loader;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Arttu on 15.3.2018.
 */

public class FacebookService {
    private static final String ATTENDING_COUNT = "attending_count";
    private static final String DESCRIPTION = "description";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String EVENT_TIMES = "event_times";

    private static final String OVERALL_STAR_RATING = "overall_star_rating";
    private static final String RATING_COUNT = "rating_count";
    private static final String HOURS = "hours";
    private static final String LOCATION = "location";
    private static final String PHONE = "phone";
    private static final String PRICE_RANGE = "price_range";
    private static final String WEBSITE = "website";
    private static final String WERE_HERE_COUNT = "were_here_count";
    private static final String FAN_COUNT = "fan_count";
    private static final String ABOUT = "about";

    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String STREET = "street";
    private static final String ZIP = "zip";

    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";

    private static FacebookService facebookService;

    public static FacebookService getInstance() {
        if (facebookService == null) {
            facebookService = new FacebookService();
        }
        return facebookService;
    }

    private Object getNode(JSONObject object, String node, Object defaultValue) {
        try {
            return object.get(node);
        } catch (JSONException e) {
            Logger.logInfo("getNodeError - " + node + " not found from " + object.toString());
            return defaultValue;
        }
    }

    private String getStringNode(JSONObject object, String node) {
        return (String) getNode(object, node, "");
    }

    private int getIntNode(JSONObject object, String node) {
        return (int) getNode(object, node, 0);
    }

    private float getFloatNode(JSONObject object, String node) {
        return Float.parseFloat(String.valueOf(getNode(object, node, 0f)));
    }

    private double getDoubleNode(JSONObject object, String node) {
        return (double) getNode(object, node, 0);
    }

    private JSONObject getJSONObject(JSONArray objects, int index) {
        JSONObject obj = new JSONObject();
        try {
            obj = objects.getJSONObject(index);
        } catch (JSONException e) {
            Logger.logInfo("getJSONObjectError - index " + index + " not found from " + objects.toString());
        }
        return obj;
    }

    private JSONObject getJSONObject(JSONObject object, String node) {
        JSONObject obj = new JSONObject();
        try {
            obj = object.getJSONObject(node);
        } catch (JSONException e) {
            Logger.logInfo("getJSONObjectError - " + node + " not found from " + object.toString());
        }
        return obj;
    }

    private JSONArray getJSONArray(JSONObject object, String node) {
        JSONArray arr = new JSONArray();
        try {
            arr = object.getJSONArray(node);
        } catch (JSONException e) {
            Logger.logInfo("getJSONArrayError - " + node + " not found from " + object.toString());
        }
        return arr;
    }

    private static Map<String, String> getMap(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        try {
            Iterator<String> keysItr = object.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = object.getString(key);
                map.put(key, value);
            }
        } catch (JSONException e) {
            Logger.logInfo("getMapError - " + object.toString());
        }
        return map;
    }

    public void getFanCount(final GetFanCountHandler handler) {
        Loader.show();
        GraphRequest request = GraphRequest.newGraphPathRequest(
                FbRes.getAccessToken(),
                "/303466930048405",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Loader.hide();
                        if (response.getError() != null) {
                            // Permission declined code 100, subError 33
                            Logger.log("Menomesta fan_count ei haettu.");
                            Logger.log(response.getError().getErrorMessage());
                        } else {
                            JSONObject object = response.getJSONObject();
                            handler.onGetFanCountSuccess(getIntNode(object, FAN_COUNT));
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "fan_count");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void getBarDetails(final Map<Bar, String> barFacebookIds, final GetFacebookBarDetailsHandler handler) {
        final Map<String, FacebookBarDetails> facebookBarDetailsMap = new HashMap<>();
        final ArrayList<String> unsupportedBars = new ArrayList<>();
        Loader.show();
        if (barFacebookIds.entrySet().size() > 0) {
            for (Map.Entry<Bar, String> entry : barFacebookIds.entrySet()) {
                final Bar bar = entry.getKey();
                final String facebookId = entry.getValue();

                GraphRequest request = GraphRequest.newGraphPathRequest(
                        FbRes.getAccessToken(),
                        facebookId,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Loader.hide();
                                if (response.getError() != null) {
                                    unsupportedBars.add(bar.name);
                                    // Permission declined code 100, subError 33
                                    Logger.log("Code: " + response.getError().getErrorCode() + " SubError: " + response.getError().getSubErrorCode());
                                    Logger.log(response.getError().getErrorMessage());
                                } else {
                                    JSONObject object = response.getJSONObject();
                                    FacebookBarDetails facebookBarDetails = new FacebookBarDetails();
                                    facebookBarDetails.name = getStringNode(object, NAME);
                                    facebookBarDetails.overallStarRating = getFloatNode(object, OVERALL_STAR_RATING);
                                    facebookBarDetails.ratingCount = getIntNode(object, RATING_COUNT);
                                    facebookBarDetails.description = getStringNode(object, DESCRIPTION);
                                    facebookBarDetails.hours = getMap(getJSONObject(object, HOURS));
                                    facebookBarDetails.phone = getStringNode(object, PHONE);
                                    facebookBarDetails.priceRange = getStringNode(object, PRICE_RANGE);
                                    facebookBarDetails.website = getStringNode(object, WEBSITE);
                                    facebookBarDetails.wereHereCount = getIntNode(object, WERE_HERE_COUNT);
                                    facebookBarDetails.fanCount = getIntNode(object, FAN_COUNT);
                                    facebookBarDetails.about = getStringNode(object, ABOUT);

                                    JSONObject locationObject = getJSONObject(object, LOCATION);
                                    BarLocation barLocation = new BarLocation();
                                    barLocation.city = getStringNode(locationObject, CITY);
                                    barLocation.country = getStringNode(locationObject, COUNTRY);
                                    barLocation.latitude = getDoubleNode(locationObject, LATITUDE);
                                    barLocation.longitude = getDoubleNode(locationObject, LONGITUDE);
                                    barLocation.street = getStringNode(locationObject, STREET);
                                    barLocation.zip = getStringNode(locationObject, ZIP);
                                    facebookBarDetails.barLocation = barLocation;

                                    facebookBarDetailsMap.put(bar.barId, facebookBarDetails);
                                }
                                if (facebookBarDetailsMap.size() + unsupportedBars.size() == barFacebookIds.size()) {
                                    handler.onFacebookBarDetailsLoaded(unsupportedBars, facebookBarDetailsMap);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,overall_star_rating,rating_count,description,hours,location,phone,category,price_range,website,were_here_count,fan_count,about");
                request.setParameters(parameters);
                request.executeAsync();
            }
        } else {
            Loader.hide();
            handler.onFacebookBarDetailsLoaded(unsupportedBars, facebookBarDetailsMap);
        }
    }

    public void getEvents(final Map<Bar, String> barFacebookIds, final GetFacebookEventsHandler handler) {
        final Map<String, ArrayList<Event>> facebookEventsMap = new HashMap<>();
        final ArrayList<String> unsupportedBars = new ArrayList<>();
        Loader.show();
        if (barFacebookIds.entrySet().size() > 0) {
            for (Map.Entry<Bar, String> entry : barFacebookIds.entrySet()) {
                final Bar bar = entry.getKey();
                final String facebookId = entry.getValue();

                GraphRequest request = GraphRequest.newGraphPathRequest(
                        FbRes.getAccessToken(),
                        facebookId + "/events",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Loader.hide();
                                if (response.getError() != null) {
                                    unsupportedBars.add(bar.name);
                                    // Permission declined code 100, subError 33
                                    Logger.log("Code: " + response.getError().getErrorCode() + " SubError: " + response.getError().getSubErrorCode());
                                    Logger.log(response.getError().getErrorMessage());
                                } else {
                                    ArrayList<Event> facebookEvents = new ArrayList<>();
                                    long now = System.currentTimeMillis();
                                    JSONObject data = response.getJSONObject();
                                    JSONArray objects = getJSONArray(data, "data");
                                    for (int i = 0; i < objects.length(); i++) {
                                        JSONObject object = getJSONObject(objects, i);
                                        String name = getStringNode(object, NAME);
                                        String description = getStringNode(object, DESCRIPTION);
                                        int attendingCount = getIntNode(object, ATTENDING_COUNT);
                                        JSONArray eventTimes = getJSONArray(object, EVENT_TIMES);
                                        if (eventTimes.length() > 0) {
                                            for (int j = 0; j < eventTimes.length(); j++) {
                                                JSONObject objectItem = getJSONObject(eventTimes, j);
                                                String id = getStringNode(objectItem, ID);
                                                String startTime = getStringNode(objectItem, START_TIME);
                                                String endTime = getStringNode(objectItem, END_TIME);
                                                long start = DateUtil.getMillisFromUTCString(startTime);
                                                long end = DateUtil.getMillisFromUTCString(endTime);
                                                // Lisätään tulevaisuudessa olevat tapahtumat jotka ovat illalla ja kestävät alle päivän
                                                if (end - start < TimeUnit.DAYS.toMillis(1) && end > now && DateUtil.isAfterSixAfternoon(start)) {
                                                    Event event = new Event();
                                                    event.eventId = id;
                                                    event.barId = bar.barId;
                                                    event.startTime = start;
                                                    event.endTime = end;
                                                    event.name = name;
                                                    event.description = description;
                                                    event.attendingCount = attendingCount;

                                                    facebookEvents.add(event);
                                                }
                                            }
                                        } else {
                                            String id = getStringNode(object, ID);
                                            String startTime = getStringNode(object, START_TIME);
                                            String endTime = getStringNode(object, END_TIME);
                                            long start = DateUtil.getMillisFromUTCString(startTime);
                                            long end = DateUtil.getMillisFromUTCString(endTime);

                                            // Lisätään tulevaisuudessa olevat tapahtumat jotka ovat illalla ja kestävät alle päivän
                                            if (end - start < TimeUnit.DAYS.toMillis(1) && end > now && DateUtil.isAfterSixAfternoon(start)) {
                                                Event event = new Event();
                                                event.eventId = id;
                                                event.barId = bar.barId;
                                                event.startTime = start;
                                                event.endTime = end;
                                                event.name = name;
                                                event.description = description;
                                                event.attendingCount = attendingCount;

                                                facebookEvents.add(event);
                                            }
                                        }
                                    }
                                    facebookEventsMap.put(bar.barId, facebookEvents);
                                }
                                if (facebookEventsMap.size() + unsupportedBars.size() == barFacebookIds.size()) {
                                    handler.onFacebookEventsLoaded(facebookEventsMap);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,attending_count,description,start_time,end_time,name,event_times");
                parameters.putString("include_canceled", "false");
                parameters.putString("event_state_filter", "['published']");
                parameters.putString("time_filter", "upcoming");
                request.setParameters(parameters);
                request.executeAsync();
            }
        } else {
            Loader.hide();
            handler.onFacebookEventsLoaded(facebookEventsMap);
        }
    }

    private static CallbackManager callbackManager;

    public static void setCallbackManager(CallbackManager manager) {
        callbackManager = manager;
    }

    public static CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setLogInListener(final Activity activity, Button loginButton, final FacebookLoginHandler handler) {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("user_birthday"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                FbRes.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        String gender = getStringNode(object, GENDER);
                                        String birthday = getStringNode(object, BIRTHDAY);
                                        handler.onLoginSuccess(gender, birthday);
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "gender,birthday");

                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        handler.onLoginFailed();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        handler.onLoginFailed();
                    }
                });
            }
        });
    }
}
