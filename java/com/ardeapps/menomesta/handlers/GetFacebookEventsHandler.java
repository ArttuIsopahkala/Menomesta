package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.Event;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetFacebookEventsHandler {
    // barId, facebookEvents
    void onFacebookEventsLoaded(Map<String, ArrayList<Event>> facebookEvents);
}
